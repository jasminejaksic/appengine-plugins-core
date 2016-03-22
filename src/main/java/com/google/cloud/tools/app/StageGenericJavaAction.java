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

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

/**
 * Stages a Java JAR/WAR Managed VMs application to be deployed.
 */
public class StageGenericJavaAction extends Action {

  private File appYaml;
  private File dockerfile;
  private File artifact;
  private File stagingDir;

  public StageGenericJavaAction(File appYaml, File dockerfile, File artifact,
      File stagingDir) {
    super(NO_FLAGS);
    this.appYaml = appYaml;
    this.dockerfile = dockerfile;
    this.artifact = artifact;
    this.stagingDir = stagingDir;
  }

  /**
   * Copies app.yaml, Dockerfile and the application artifact to the staging area.
   *
   * <p>If app.yaml or Dockerfile do not exist, gcloud deploy will create them.
   */
  public boolean execute() throws IOException {
    // Copy app.yaml to staging.
    if (appYaml != null && appYaml.exists()) {
      Files.copy(appYaml, new File(stagingDir, "app.yaml"));
    }

    // Copy Dockerfile to staging.
    if (dockerfile != null && dockerfile.exists()) {
      Files.copy(dockerfile, new File(stagingDir, "Dockerfile"));
    }

    // Copy the JAR/WAR file to staging.
    if (artifact != null && artifact.exists()) {
      Files.copy(artifact, new File(stagingDir, artifact.getName()));
    }

    return true;
  }
}
