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
 * Generates missing configuration files.
 */
public class GenConfigAction extends Action {

  private static Set<Option> acceptedFlags = ImmutableSet.of(
      Option.CONFIG,
      Option.CUSTOM,
      Option.RUNTIME
  );

  public GenConfigAction(String sourceDirectory, Map<Option, String> flags) {
    super(flags);
    Preconditions.checkNotNull(sourceDirectory);
    checkFlags(flags, acceptedFlags);

    List<String> arguments = new ArrayList<>();
    arguments.add("gen-config");
    if (!Strings.isNullOrEmpty(sourceDirectory)) {
      arguments.add(sourceDirectory);
    }

    this.processCaller = new ProcessCaller(
        Tool.GCLOUD,
        arguments,
        flags);
  }
}
