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
package com.google.cloud.tools;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

  public ProcessCaller(Tool tool, List<String> arguments, Map<Option, String> flags) {
    this(tool, arguments, flags, DEFAULT_WORKING_DIR, true);
  }

  public ProcessCaller(Tool tool, List<String> arguments, Map<Option, String> flags,
      File workingDirectory) {
    this(tool, arguments, flags, workingDirectory, true);
  }

  public ProcessCaller(Tool tool, List<String> arguments, Map<Option, String> flags,
      boolean synchronous) {
    this(tool, arguments, flags, DEFAULT_WORKING_DIR, synchronous);
  }

  public ProcessCaller(Tool tool, List<String> arguments, Map<Option, String> flags,
      File workingDirectory, boolean synchronous) {
    this.workingDirectory = workingDirectory;
    this.synchronous = synchronous;
    this.command = prepareCommand(tool, arguments, flags);
  }

  public boolean call() throws GCloudErrorException {
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.directory(workingDirectory);
    processBuilder.inheritIO();
    Process gcloudProcess;
    try {
      gcloudProcess = processBuilder.start();
    } catch (IOException ioEx) {
      LOG.severe(String.format("Error running gcloud CLI. %s", ioEx));
      return false;
    }

    // If call is asynchronous, don't wait for the process to end.
    if (synchronous) {
      try {
        int exitStatus = gcloudProcess.waitFor();

        if (exitStatus != 0) {
          throw new GCloudErrorException(exitStatus);
        }
      } catch (InterruptedException intEx) {
        LOG.severe(String.format("Process interrupted. Reason %s", intEx));
      }
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
      throw new RuntimeException(String.format(
          "Could not locate gcloud from Cloud SDK directory \"%s/bin\". Please provide the correct "
              + "Cloud SDK root directory.",
          cloudSdkLocation));
    }

    return gcloudLocation;
  }

  /**
   * Finds the executable path for dev_appserver.py.
   */
  protected String getDevAppserverPath() {
    String devAppserverLocation = cloudSdkLocation + "/bin/dev_appserver.py";
    if (!new File(devAppserverLocation).exists()) {
      throw new RuntimeException(String.format(
          "Could not locate gcloud from Cloud SDK directory %s. Please provide the correct Cloud "
              + "SDK root directory.",
          cloudSdkLocation));
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
   * <p>Obsoleted when if gcloud CLI is no longer a dependency.
   */
  protected List<String> prepareCommand(Tool tool, List<String> arguments,
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

  public void setCloudSdkOverride(String cloudSdkOverride) throws InvalidDirectoryException {
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
}
