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
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Deletes a version of one or more modules.
 */
public class DeleteAction extends Action {

  private static ProcessCallerFactory processCallerFactory = ProcessCaller.getFactory();
  private Collection<String> modules = UNSET_COLLECTION;
  private String version = UNSET_STRING;
  private String server = UNSET_STRING;

  public static DeleteAction newDeleteAction() {
    return new DeleteAction();
  }

  public DeleteAction setModules(Collection<String> modules) {
    Preconditions.checkArgument(this.modules == UNSET_COLLECTION, "Modules can only be set once.");
    this.modules = modules;
    return this;
  }

  public DeleteAction setVersion(String version) {
    Preconditions.checkArgument(this.version == UNSET_STRING, "Version can only be set once.");
    this.version = version;
    return this;
  }

  public DeleteAction setServer(String server) {
    Preconditions.checkArgument(this.server == UNSET_STRING, "Server can only be set once.");
    this.server = server;
    return this;
  }

  @Override
  public boolean execute() throws GCloudExecutionException {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(version));

    List<String> arguments = new ArrayList<>();
    arguments.add("modules");
    arguments.add("delete");
    if (modules == null) {
      arguments.add("default");
    } else {
      arguments.addAll(modules);
    }
    arguments.add("--version");
    arguments.add(version);
    if (!Strings.isNullOrEmpty(server)) {
      arguments.add(Option.SERVER.getLongForm());
      arguments.add(server);
    }
    arguments.add("--quiet");

    return processCallerFactory.newProcessCaller(Tool.GCLOUD, arguments).call();
  }

  @VisibleForTesting
  public void setProcessCallerFactory(ProcessCallerFactory processCallerFactory) {
    this.processCallerFactory = processCallerFactory;
  }
}
