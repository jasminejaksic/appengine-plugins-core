/*
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

package com.google.cloud.tools.appengine.cloudsdk.internal.process;

import com.google.cloud.tools.appengine.cloudsdk.process.ProcessExitListener;
import com.google.cloud.tools.appengine.cloudsdk.process.ProcessOutputLineListener;
import com.google.cloud.tools.appengine.cloudsdk.process.ProcessStartListener;
import com.google.common.collect.Lists;

/**
 * A ProcessRunner that runs synchronous processes and makes their stderr and stdout streams
 * available as Strings.
 */
public class SynchronousOutputProcessRunner extends DefaultProcessRunner implements ProcessRunner {

  private StringBuilderProcessOutputLineListener stdOutListener;
  private StringBuilderProcessOutputLineListener stdErrListener;

  // TODO should constructor be more configurable?

  private SynchronousOutputProcessRunner(StringBuilderProcessOutputLineListener stdOutListener,
                                         StringBuilderProcessOutputLineListener stdErrListener) {
    super(
        false                                                         /* async */,
        Lists.<ProcessExitListener>newArrayList()                     /* exitListeners */,
        Lists.<ProcessStartListener>newArrayList()                    /* startListeners */,
        Lists.<ProcessOutputLineListener>newArrayList(stdOutListener) /* stdOutLineListeners */,
        Lists.<ProcessOutputLineListener>newArrayList(stdErrListener) /* stdErrLineListeners */);

    this.stdOutListener = stdOutListener;
    this.stdErrListener = stdErrListener;
  }

  /**
   * Returns the stdout output over the full life of the process.
   */
  public String getProcessStdOut() {
    return stdOutListener.toString();
  }

  /**
   * Returns the stderr output over the full life of the process.
   */
  public String getProcessStdErr() {
    return stdErrListener.toString();
  }

  public static class Builder {
    public SynchronousOutputProcessRunner build() {
      return new SynchronousOutputProcessRunner(new StringBuilderProcessOutputLineListener(),
          new StringBuilderProcessOutputLineListener());
    }
  }

}
