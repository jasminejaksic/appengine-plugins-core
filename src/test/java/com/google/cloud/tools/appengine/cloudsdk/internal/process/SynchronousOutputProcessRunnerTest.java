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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SynchronousOutputProcessRunner}
 */
@RunWith(MockitoJUnitRunner.class)
public class SynchronousOutputProcessRunnerTest {

  private SynchronousOutputProcessRunner runner;

  @Mock
  private StringBuilderProcessOutputLineListener stdOutListener;
  @Mock
  private StringBuilderProcessOutputLineListener stdErrListener;
  @Mock
  private ExitCodeRecorderProcessExitListener exitListener;

  @Before
  public void setup() {
    runner = new SynchronousOutputProcessRunner(stdOutListener, stdErrListener, exitListener);
  }

  @Test
  public void testHasProcessExitedSuccessfully_null() {
    when(exitListener.getMostRecentExitCode()).thenReturn(null);
    assertFalse(runner.hasProcessExitedSuccessfully());
  }

  @Test
  public void testHasProcessExitedSuccessfully_error() {
    when(exitListener.getMostRecentExitCode()).thenReturn(127);
    assertFalse(runner.hasProcessExitedSuccessfully());
  }

  @Test
  public void testHasProcessExitedSuccessfully_success() {
    when(exitListener.getMostRecentExitCode()).thenReturn(0);
    assertTrue(runner.hasProcessExitedSuccessfully());
  }

  @Test
  public void testGetProcessStdOut() throws ProcessRunnerException {
    String stdout = "this is std out";
    when(stdOutListener.toString()).thenReturn(stdout);
    assertEquals(stdout, runner.getProcessStdOut());
  }

  @Test
  public void testGetProcessStdErr() throws ProcessRunnerException {
    String stderr = "this is std err";
    when(stdErrListener.toString()).thenReturn(stderr);
    assertEquals(stderr, runner.getProcessStdErr());
  }

}
