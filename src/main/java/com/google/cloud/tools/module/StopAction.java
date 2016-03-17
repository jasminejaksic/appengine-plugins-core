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
package com.google.cloud.tools.module;

import com.google.appengine.repackaged.com.google.api.client.util.Strings;
import com.google.cloud.tools.Action;
import com.google.cloud.tools.Option;
import com.google.cloud.tools.ProcessCaller;
import com.google.cloud.tools.ProcessCaller.Tool;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Stops serving a specific version of a module.
 */
public class StopAction extends Action {

  private static Option[] acceptedFlags = {
      Option.SERVER
  };

  public StopAction(List<String> modules, String version, Map<Option, String> flags) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(version));
    super.acceptedFlags = this.acceptedFlags;
    checkFlags(flags);
    this.flags = flags;

    List<String> arguments = new ArrayList<>();
    arguments.add("modules");
    arguments.add("stop");
    arguments.addAll(modules);
    arguments.add("--version");
    arguments.add(version);

    this.processCaller = new ProcessCaller(
        Tool.GCLOUD,
        arguments,
        flags
    );
  }
}
