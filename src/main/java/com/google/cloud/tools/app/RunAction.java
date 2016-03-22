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
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Starts the local development server, synchronous or asynchronously.
 */
public class RunAction extends Action {

  // Only contains common, jvm, Python, VM and misc flags for now. No PHP, AppIdentity, Blobstore,
  // etc.
  // TODO(joaomartins): Evaluate if some of these flags can be reused from other existing ones.
  private static Set<Option> acceptedFlags = ImmutableSet.of(
      Option.HOST,
      Option.PORT,
      Option.ADMIN_HOST,
      Option.ADMIN_PORT,
      Option.AUTH_DOMAIN,
      Option.STORAGE_PATH,
      Option.LOG_LEVEL,
      Option.MAX_MODULE_INSTANCES,
      Option.USE_MTIME_FILE_WATCHER,
      Option.THREADSAFE_OVERRIDE,
      Option.PYTHON_STARTUP_SCRIPT,
      Option.PYTHON_STARTUP_ARGS,
      Option.JVM_FLAG,
      Option.CUSTOM_ENTRYPOINT,
      Option.RUNTIME,
      Option.ALLOW_SKIPPED_FILES,
      Option.API_PORT,
      Option.AUTOMATIC_RESTART,
      Option.DEV_APPSERVER_LOG_LEVEL,
      Option.SKIP_SDK_UPDATE_CHECK,
      Option.DEFAULT_GCS_BUCKET_NAME
  );

  public RunAction(String appYaml, boolean synchronous, Map<Option, String> flags) {
    super(flags);
    Preconditions.checkArgument(!Strings.isNullOrEmpty(appYaml));
    checkFlags(flags, acceptedFlags);

    List<String> arguments = new ArrayList<>();
    arguments.add(appYaml);
    // TODO(joaomartins): Run with more modules.

    this.processCaller = new ProcessCaller(
        Tool.DEV_APPSERVER,
        arguments,
        flags,
        synchronous);
  }

}
