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
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Gets the logs for a version of a module.
 */
public class GetLogsAction extends Action {

  private ProcessCallerFactory processCallerFactory;
  private Collection<String> modules = UNSET_COLLECTION;
  private String version = UNSET_STRING;
  private String logFileLocation = UNSET_STRING;
  private String append = UNSET_STRING;
  private String days = UNSET_STRING;
  private Boolean details = UNSET_BOOLEAN;
  private String endDate = UNSET_STRING;
  private String server = UNSET_STRING;
  private String severity = UNSET_STRING;
  private String vhost = UNSET_STRING;

  public boolean execute() throws GCloudExecutionException {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(version));

    List<String> arguments = new ArrayList<>();
    arguments.add("modules");
    arguments.add("get-logs");
    arguments.addAll(modules);
    // TODO(joaomartins): Check if file is valid.
    // TODO(joaomartins): Should we disallow empty files? Printing to stdout will be cluttered
    // by Maven's artifacts, for example.
    if (!Strings.isNullOrEmpty(logFileLocation)) {
      arguments.add(logFileLocation);
    }
    arguments.add("--version");
    arguments.add(version);
    if (!Strings.isNullOrEmpty(append)) {
      arguments.add("--append");
      arguments.add(append);
    }
    if (!Strings.isNullOrEmpty(days)) {
      arguments.add("--days");
      arguments.add(days);
    }
    if (details != UNSET_BOOLEAN) {
      arguments.add("--details");
      arguments.add(details.toString());
    }
    if (!Strings.isNullOrEmpty(endDate)) {
      arguments.add("--end-date");
      arguments.add(endDate);
    }
    if (!Strings.isNullOrEmpty(server)) {
      arguments.add("--server");
      arguments.add(server);
    }
    if (!Strings.isNullOrEmpty(severity)) {
      arguments.add("--severity");
      arguments.add(severity);
    }
    if (!Strings.isNullOrEmpty(vhost)) {
      arguments.add("--vhost");
      arguments.add(vhost);
    }

    return processCallerFactory.newProcessCaller(Tool.GCLOUD, arguments).call();
  }

  public static GetLogsAction newGetLogsAction() {
    return new GetLogsAction();
  }

  public GetLogsAction setModules(Collection<String> modules) {
    Preconditions.checkArgument(this.modules == UNSET_COLLECTION, "Modules can only be set once.");
    this.modules = modules;
    return this;
  }

  public GetLogsAction setVersion(String version) {
    Preconditions.checkArgument(this.version == UNSET_STRING, "Version can only be set once.");
    this.version = version;
    return this;
  }

  public GetLogsAction setLogFileLocation(String logFileLocation) {
    Preconditions.checkArgument(this.logFileLocation == UNSET_STRING,
        "Log file location can only be set once.");
    this.logFileLocation = logFileLocation;
    return this;
  }

  public GetLogsAction setAppend(String append) {
    Preconditions.checkArgument(this.append == UNSET_STRING, "Append can only be set once.");
    this.append = append;
    return this;
  }

  public GetLogsAction setDays(String days) {
    Preconditions.checkArgument(this.days == UNSET_STRING, "Days can only be set once.");
    try {
      Integer.parseInt(days);
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("Days is not integer.");
    }
    this.days = days;
    return this;
  }

  public GetLogsAction setDetails(Boolean details) {
    Preconditions.checkArgument(this.details == UNSET_BOOLEAN, "Details can only be set once.");
    this.details = details;
    return this;
  }

  public GetLogsAction setEndDate(String endDate) {
    Preconditions.checkArgument(this.endDate == UNSET_STRING, "End date can only be set once.");
    this.endDate = endDate;
    return this;
  }

  public GetLogsAction setServer(String server) {
    Preconditions.checkArgument(this.server == UNSET_STRING, "Server can only be set once.");
    this.server = server;
    return this;
  }

  public GetLogsAction setSeverity(String severity) {
    Preconditions.checkArgument(this.severity == UNSET_STRING, "Severity can only be set once.");
    this.severity = severity;
    return this;
  }

  public GetLogsAction setVhost(String vhost) {
    Preconditions.checkArgument(this.vhost == UNSET_STRING, "Vhost can only be set once.");
    this.vhost = vhost;
    return this;
  }

  @VisibleForTesting
  public void setProcessCallerFactory(ProcessCallerFactory processCallerFactory) {
    this.processCallerFactory = processCallerFactory;
  }
}
