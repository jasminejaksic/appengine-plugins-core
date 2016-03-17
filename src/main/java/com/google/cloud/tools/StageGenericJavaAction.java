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
  public boolean execute() {
    // Copy app.yaml to staging.
    try {
      if (appYaml.exists()) {
        Files.copy(appYaml, stagingDir);
      }
    } catch (IOException ioe) {
      throw new RuntimeException("Failed to copy app.yaml to the staging area.", ioe);
    }

    // Copy Dockerfile to staging.
    if (dockerfile.exists()) {
      try {
        Files.copy(dockerfile, new File(stagingDir, "Dockerfile"));
      } catch (IOException ioe) {
        throw new RuntimeException("Failed to copy Dockerfile to the staging area.", ioe);
      }
    }

    // Copy the JAR/WAR file to staging.
    if (artifact.exists()) {
      try {
        Files.copy(artifact, new File(stagingDir, artifact.getName()));
      } catch (IOException ioe) {
        throw new RuntimeException(String.format(
            "Failed to copy %s to the staging area.", artifact.getName()), ioe);
      }
    }

    return true;
  }
}
