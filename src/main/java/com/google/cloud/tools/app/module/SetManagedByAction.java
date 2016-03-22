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
package com.google.cloud.tools.app.module;

import com.google.cloud.tools.app.Action;
import com.google.cloud.tools.app.Option;
import com.google.cloud.tools.app.ProcessCaller;
import com.google.cloud.tools.app.ProcessCaller.Tool;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Sets a specific instance or every instance of a module to managed by Google or Self.
 */
public class SetManagedByAction extends Action {

  private static Set<Option> acceptedFlags = ImmutableSet.of(Option.INSTANCE, Option.SERVER);

  public SetManagedByAction(List<String> modules, String version, Option managedBy,
      Map<Option, String> flags) {
    super(flags);
    Preconditions.checkArgument(!Strings.isNullOrEmpty(version));
    Preconditions.checkNotNull(managedBy);
    checkFlags(flags, acceptedFlags);

    List<String> arguments = new ArrayList<>();
    arguments.add("modules");
    arguments.add("set-managed-by");
    arguments.addAll(modules);
    arguments.add("--version");
    arguments.add(version);
    arguments.add(managedBy.getLongForm());

    this.processCaller = new ProcessCaller(
        Tool.GCLOUD,
        arguments,
        flags
    );
  }
}
