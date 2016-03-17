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

import com.google.cloud.tools.ProcessCaller.Tool;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Deploys an app to the server.
 */
public class DeployAction extends Action {

  private static Option[] acceptedFlags = {
      Option.BUCKET,
      Option.DOCKER_BUILD,
      Option.FORCE,
      Option.IMAGE_URL,
      Option.PROMOTE,
      Option.SERVER
  };
  private AppType appType;
  private Action stageAction;
  private File stagedDirectory;

  /**
   * Initialises all the necessary properties to deploy an application.
   *
   * @param stagedDirectoryLocation Directory where staged application is located.
   * @param appType CLASSIC_APP_ENGINE, MVM, JAVA8, etc.
   * @param flags Deployment flags.
   * @throws InvalidDirectoryException If either staging directories are invalid.
   */
  public DeployAction(String stagedDirectoryLocation, AppType appType, Map<Option, String> flags,
      Action stageAction) throws InvalidDirectoryException {
    Preconditions.checkNotNull(stagedDirectoryLocation);
    Preconditions.checkNotNull(appType);
    Preconditions.checkNotNull(stageAction);

    stagedDirectory = new File(stagedDirectoryLocation);

    super.acceptedFlags = this.acceptedFlags;
    this.appType = appType;
    checkFlags(flags);
    this.flags = flags;
    this.stageAction = stageAction;

    // Set process caller.
    List<String> arguments = new ArrayList<>();
    arguments.add("deploy");
    //arguments.add(stagedDirectoryLocation + "/app.yaml");

    this.processCaller = new ProcessCaller(
        Tool.GCLOUD,
        arguments,
        flags
    );
  }

  @Override
  public boolean execute() throws GCloudErrorException {
    if (!stagedDirectory.exists() || !stagedDirectory.isDirectory()) {
      throw new InvalidDirectoryException(String.format(
          "Staging directory does not exist or is not a directory. %s",
          stagedDirectory.getAbsolutePath()));
    }

    if (appType.equals(AppType.CLASSIC_APP_ENGINE)) {
      if (!stageAction.execute()) {
        throw new GCloudErrorException("Staging phase returned an error.");
      }
    } else if (appType.equals(AppType.MVM)) {
      // TODO(joaomartins): Missing MVM deployment.
    }

    return processCaller.call();
  }

  public enum AppType {
    CLASSIC_APP_ENGINE,
    MVM
  }
}
