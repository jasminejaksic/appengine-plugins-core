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

import com.google.cloud.tools.app.ProcessCaller.Tool;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Deploys an app to the server.
 */
public class DeployAction extends Action {

  private static Set<Option> acceptedFlags = ImmutableSet.of(
      Option.BUCKET,
      Option.DOCKER_BUILD,
      Option.FORCE,
      Option.IMAGE_URL,
      Option.PROMOTE,
      Option.SERVER,
      Option.VERSION
  );
  private File stagedDirectory;

  /**
   * Initialises all the necessary properties to deploy an application.
   *
   * @param stagedDirectoryLocation directory where staged application is located
   * @param flags deployment flags
   */
  public DeployAction(String stagedDirectoryLocation, Map<Option, String> flags) {
    super(flags);
    Preconditions.checkNotNull(stagedDirectoryLocation);
    checkFlags(flags, acceptedFlags);

    stagedDirectory = new File(stagedDirectoryLocation);

    // Set process caller.
    List<String> arguments = new ArrayList<>();
    arguments.add("deploy");
    arguments.add(stagedDirectoryLocation + "/app.yaml");
    arguments.add("--quiet");

    this.processCaller = new ProcessCaller(
        Tool.GCLOUD,
        arguments,
        flags,
        stagedDirectory
    );
  }

  @Override
  public boolean execute() throws GCloudExecutionException, IOException {
    if (!stagedDirectory.exists()) {
      throw new InvalidDirectoryException(
          "Staging directory does not exist. " + stagedDirectory.getAbsolutePath());
    }

    if (!stagedDirectory.isDirectory()) {
      throw new InvalidDirectoryException(
          "Staging directory is not a directory. " + stagedDirectory.getAbsolutePath());
    }

    return processCaller.call();
  }
}
