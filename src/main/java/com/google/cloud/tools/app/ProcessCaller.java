/**
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.tools.app;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * Calls external tools like gcloud or dev_appserver. Hides system process invocation logic from
 * {@link Action}.
 *
 * <p>All the logic for generating and running commands is contained in this class.
 */
public class ProcessCaller {

  // TODO(joaomartins): Will this work in e.g., Windows?
  private static File DEFAULT_WORKING_DIR = new File("/");
  private static Logger LOG = Logger.getLogger(ProcessCaller.class.getName());
  private List<String> command;
  private File workingDirectory;
  private boolean synchronous;
  private String cloudSdkLocation = System.getProperty("user.home") + "/google-cloud-sdk";

  private ProcessCaller(Tool tool, Collection<String> arguments) {
    this(tool, arguments, ImmutableMap.<Option, String>of());
  }

  public ProcessCaller(Tool tool, Collection<String> arguments, Map<Option, String> flags) {
    this(tool, arguments, flags, DEFAULT_WORKING_DIR, true);
  }

  public ProcessCaller(Tool tool, Collection<String> arguments, Map<Option, String> flags,
      File workingDirectory) {
    this(tool, arguments, flags, workingDirectory, true);
  }

  public ProcessCaller(Tool tool, Collection<String> arguments, Map<Option, String> flags,
      boolean synchronous) {
    this(tool, arguments, flags, DEFAULT_WORKING_DIR, synchronous);
  }

  public ProcessCaller(Tool tool, Collection<String> arguments, Map<Option, String> flags,
      File workingDirectory, boolean synchronous) {
    this.workingDirectory = workingDirectory;
    this.synchronous = synchronous;
    this.command = prepareCommand(tool, arguments, flags);
  }

  public boolean call() throws GCloudExecutionException {
    LOG.info("Calling " + Joiner.on(" ").join(command));

    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.directory(workingDirectory);
    processBuilder.inheritIO();
    try {
      Process gcloudProcess = processBuilder.start();

      // If call is synchronous, wait for the process to end.
      if (synchronous) {
        int exitStatus = gcloudProcess.waitFor();

        if (exitStatus != 0) {
          throw new GCloudExecutionException(exitStatus);
        }
      }
    } catch (IOException|InterruptedException ex) {
      LOG.severe("Error running gcloud CLI. " + ex);
      return false;
    }

    return true;
  }

  /**
   * Finds the executable path for gcloud.
   */
  @VisibleForTesting
  public String getGcloudPath() {
    String gcloudLocation = cloudSdkLocation + "/bin/gcloud";
    if (!new File(gcloudLocation).exists()) {
      throw new RuntimeException("Could not locate gcloud from Cloud SDK directory \""
          + cloudSdkLocation + "/bin\". Please provide the correct Cloud SDK root directory.");
    }

    return gcloudLocation;
  }

  /**
   * Finds the executable path for dev_appserver.py.
   */
  protected String getDevAppserverPath() {
    String devAppserverLocation = cloudSdkLocation + "/bin/dev_appserver.py";
    if (!new File(devAppserverLocation).exists()) {
      throw new RuntimeException("Could not locate dev_appserver from Cloud SDK directory \""
          + cloudSdkLocation + "/bin\". Please provide the correct Cloud SDK root directory.");
    }

    return devAppserverLocation;
  }

  @VisibleForTesting
  public List<String> getCommand() {
    return command;
  }

  /**
   * Prepares the gcloud command ran by the {@link Action}.
   *
   * <p>Obsoleted when gcloud CLI is no longer a dependency.
   */
  protected List<String> prepareCommand(Tool tool, Collection<String> arguments,
      Map<Option, String> flags) {
    List<String> command = new ArrayList<>();

    if (tool.equals(Tool.DEV_APPSERVER)) {
      command.add(getDevAppserverPath());
    } else {
      command.add(getGcloudPath());
      command.add("preview");
      command.add("app");
    }

    // Command. e.g. "deploy" or "modules list".
    command.addAll(arguments);

    // Flags passed by the client.
    for (Entry<Option, String> flag : flags.entrySet()) {
      command.add(flag.getKey().getLongForm());
      command.add(flag.getValue());
    }

    return command;
  }

  public void setCloudSdkOverride(String cloudSdkOverride) {
    if (!Strings.isNullOrEmpty(cloudSdkOverride)) {
      File cloudSdkDir =  new File(cloudSdkOverride);
      if (!cloudSdkDir.exists() || !cloudSdkDir.isDirectory()) {
        throw new InvalidDirectoryException("Invalid Cloud SDK directory provided.");
      }

      cloudSdkLocation = cloudSdkOverride;
    }
  }

  public enum Tool {
    GCLOUD,
    DEV_APPSERVER
  }

  public static ProcessCallerFactory getFactory() {
    return new ProcessCallerFactory();
  }

  public static class ProcessCallerFactory {
    public ProcessCaller newProcessCaller(Tool tool, Collection<String> arguments) {
      return new ProcessCaller(tool, arguments);
    }
  }
}
