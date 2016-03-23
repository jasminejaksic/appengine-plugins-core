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
import com.google.cloud.tools.app.GCloudExecutionException;
import com.google.cloud.tools.app.Option;
import com.google.cloud.tools.app.ProcessCaller;
import com.google.cloud.tools.app.ProcessCaller.ProcessCallerFactory;
import com.google.cloud.tools.app.ProcessCaller.Tool;
import com.google.cloud.tools.app.config.module.ListConfiguration;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Lists the versions for a module, or every version of every module if no module is specified.
 */
public class ListAction extends Action {

  private ProcessCallerFactory processCallerFactory = ProcessCaller.getFactory();
  private ListConfiguration configuration;

  public ListAction(ListConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public boolean execute() throws GCloudExecutionException {
    List<String> arguments = new ArrayList<>();
    arguments.add("modules");
    arguments.add("list");
    arguments.addAll(configuration.getModules());

    return processCallerFactory.newProcessCaller(
        Tool.GCLOUD, arguments, configuration.getOptionalParameters()).call();
  }

  @VisibleForTesting
  public void setProcessCallerFactory(ProcessCallerFactory processCallerFactory) {
    this.processCallerFactory = processCallerFactory;
  }
}
