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

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.tools.app.GCloudExecutionException;
import com.google.cloud.tools.app.ProcessCaller;
import com.google.cloud.tools.app.ProcessCaller.ProcessCallerFactory;
import com.google.cloud.tools.app.ProcessCaller.Tool;
import com.google.common.collect.ImmutableList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link DeleteAction}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeleteActionTest {

  @Mock
  private ProcessCaller callerMock;
  @Mock
  private ProcessCallerFactory processCallerFactory;

  @Before
  public void setUp() throws GCloudExecutionException {
    when(processCallerFactory.newProcessCaller(eq(Tool.GCLOUD), isA(ArrayList.class)))
        .thenReturn(callerMock);
    when(callerMock.call()).thenReturn(true);
  }

  @Test
  public void testNewDeleteAction() {
    DeleteAction.newDeleteAction("v1", "mod1", "mod2")
        .setServer("appengine.google.com");
  }

  @Test(expected = NullPointerException.class)
  public void testNewDeleteAction_nullModules() throws GCloudExecutionException {
    DeleteAction.newDeleteAction("v1", null).setServer("appengine.google.com");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNewDeleteAction_emptyModules() throws GCloudExecutionException {
    DeleteAction.newDeleteAction("v1").setServer("appengine.google.com");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNewDeleteAction_nullVersion() {
    DeleteAction.newDeleteAction(null, "mod1");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNewDeleteAction_emptyVersion() {
    DeleteAction.newDeleteAction("", "mod1");
  }

  @Test
  public void testArguments_all() throws GCloudExecutionException {
    List<String> arguments = ImmutableList.of("modules", "delete", "mod1", "mod2", "--version",
        "v1", "--server", "appengine.google.com", "--quiet");

    DeleteAction action = DeleteAction.newDeleteAction("v1", "mod1", "mod2")
        .setServer("appengine.google.com");
    action.setProcessCallerFactory(processCallerFactory);
    action.execute();

    verify(processCallerFactory, times(1)).newProcessCaller(eq(Tool.GCLOUD), eq(arguments));
  }

  @Test
  public void testArguments_noServer() throws GCloudExecutionException {
    List<String> arguments = ImmutableList.of("modules", "delete", "mod1", "--version", "v1",
        "--quiet");

    DeleteAction action = DeleteAction.newDeleteAction("v1", "mod1");
    action.setProcessCallerFactory(processCallerFactory);
    action.execute();

    verify(processCallerFactory, times(1)).newProcessCaller(eq(Tool.GCLOUD), eq(arguments));
  }
}
