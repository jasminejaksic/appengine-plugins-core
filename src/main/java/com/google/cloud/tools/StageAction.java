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

import com.google.appengine.tools.admin.AppCfg;
import com.google.common.base.Preconditions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Stages an application to be deployed.
 */
public class StageAction extends Action {

  private static Option[] acceptedFlags = {
      Option.ENABLE_QUICKSTART,
      Option.DISABLE_UPDATE_CHECK,
      Option.VERSION,
      Option.GCLOUD_PROJECT,
      Option.ENABLE_JAR_SPLITTING,
      Option.JAR_SPLITTING_EXCLUDES,
      Option.RETAIN_UPLOAD_DIR,
      Option.COMPILE_ENCODING,
      Option.FORCE,
      Option.DELETE_JSPS,
      Option.ENABLE_JAR_CLASSES,
      Option.RUNTIME,
  };
  private File sourceDirectory;
  private File stagingDirectory;
  // AppCfg requires that the SDK root not be set to the jar, but to the root directory.
  // SDK location depends on the client who sends it.
  private String sdkRoot;

  /**
   * Stages an application by arranging the files in a way App Engine recognises them.
   *
   * @param sourceDirectoryLocation Directory containing app.yaml and WEB-INF with class binaries.
   * @param stagingDirectoryLocation Directory where staged application will be located.
   * @param sdkRoot App Engine SDK installation directory in the user file system.
   * @param flags Staging flags.
   * @throws InvalidDirectoryException
   */
  public StageAction(String sourceDirectoryLocation, String stagingDirectoryLocation,
      String sdkRoot, Map<Option, String> flags) throws InvalidDirectoryException {
    Preconditions.checkNotNull(sourceDirectoryLocation);
    Preconditions.checkNotNull(stagingDirectoryLocation);
    Preconditions.checkNotNull(sdkRoot);

    super.acceptedFlags = this.acceptedFlags;
    checkFlags(flags);
    sourceDirectory = new File(sourceDirectoryLocation);
    stagingDirectory = new File(stagingDirectoryLocation);

    // Error checking.
    if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
      throw new InvalidDirectoryException(String.format(
          "Source directory does not exist or is a file. Location: %s",
          sourceDirectory.getAbsolutePath()));
    }
    this.sdkRoot = sdkRoot;
    this.flags = flags;
  }

  @Override
  public boolean execute() {
    List<String> arguments = new ArrayList<>();
    arguments.add("stage");
    arguments.add(sourceDirectory.getAbsolutePath());
    arguments.add(stagingDirectory.getAbsolutePath());
    for (Entry<Option, String> flag : flags.entrySet()) {
      arguments.add(flag.getKey().getLongForm());
      arguments.add(flag.getValue());
    }

    // AppCfg requires this system property to be set.
    System.setProperty("appengine.sdk.root", sdkRoot);
    AppCfg.main(arguments.toArray(new String[arguments.size()]));

    return true;
  }
}
