/*
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

package com.google.cloud.tools.appengine.cloudsdk;

import com.google.cloud.tools.appengine.api.AppEngineException;
import com.google.cloud.tools.appengine.api.debug.GenRepoInfoFile;
import com.google.cloud.tools.appengine.api.debug.GenRepoInfoFileConfiguration;
import com.google.cloud.tools.appengine.cloudsdk.internal.args.GcloudArgs;
import com.google.cloud.tools.appengine.cloudsdk.internal.process.ProcessRunnerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Cloud SDK based implementation of {@link GenRepoInfoFile}.
 */
public class CloudSdkGenRepoInfoFile implements GenRepoInfoFile {

  private CloudSdk sdk;

  public CloudSdkGenRepoInfoFile(CloudSdk sdk) {
    this.sdk = sdk;
  }

  /**
   * Generates source context files.
   *
   * <p>It is possible for the process to return an error code. In that case, no
   * {@link ProcessRunnerException} is thrown, but the code must be caught with a
   * {@link com.google.cloud.tools.appengine.cloudsdk.process.ProcessExitListener} attached to the
   * {@link CloudSdk} object used to run the command.
   *
   * @param configuration Contains the source and output directories
   */
  @Override
  public void generate(GenRepoInfoFileConfiguration configuration) {
    List<String> arguments = new ArrayList<>();

    arguments.add("gen-repo-info-file");
    arguments.addAll(GcloudArgs.get("output-directory", configuration.getOutputDirectory()));
    arguments.addAll(GcloudArgs.get("source-directory", configuration.getSourceDirectory()));

    try {
      sdk.runSourceCommand(arguments);
    } catch (ProcessRunnerException pre) {
      throw new AppEngineException(pre);
    }
  }
}
