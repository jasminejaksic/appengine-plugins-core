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

import com.google.cloud.tools.app.ProcessCaller.ProcessCallerFactory;
import com.google.common.annotations.VisibleForTesting;

import java.io.IOException;

/**
 * Contains common members and methods to all {@link Action} implementations.
 */
// TODO(joaomartins): Come up with a better name for this. Action too overloaded.
public abstract class Action {

  /**
   * ProcessCaller factory.
   */
  protected ProcessCallerFactory processCallerFactory = ProcessCaller.getFactory();

  /**
   * Makes system calls to invoke processes.
   */
  protected ProcessCaller processCaller;

  /**
   * Executes the logic implemented by this {@link Action}.
   *
   * <p>For most actions, this is simply calling gcloud for the command that was supplied in the
   * {@link Action} constructor.
   *
   * @return {@code true} if execution was successful, {@code false} if it was unsuccessful but no
   * exception was thrown.
   * @throws GCloudExecutionException if any error invoking or running gcloud occurred.
   */
  public abstract boolean execute() throws GCloudExecutionException, IOException;

  @VisibleForTesting
  public ProcessCaller getProcessCaller() {
    return processCaller;
  }

  /**
   * Sets a new Cloud SDK location.
   *
   * @throws IllegalArgumentException If no directory is provided, the provided directory does not
   * exist or gcloud or dev_appserver.py can not be found.
   */
  public void setCloudSdkLocation(String cloudSdkLocation) {
    this.processCaller.setCloudSdkPath(cloudSdkLocation);
  }

//  @VisibleForTesting
//  public void setProcessCaller(ProcessCaller processCaller) {
//    this.processCaller = processCaller;
//  }

  @VisibleForTesting
  public void setProcessCallerFactory(ProcessCallerFactory processCallerFactory) {
    this.processCallerFactory = processCallerFactory;
  }
}
