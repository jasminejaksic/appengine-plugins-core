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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.tools.app.GCloudExecutionException;
import com.google.cloud.tools.app.InvalidFlagException;
import com.google.cloud.tools.app.Option;
import com.google.cloud.tools.app.ProcessCaller;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Unit tests for {@link GetLogsAction}.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetLogsActionTest {

  @Mock
  private ProcessCaller callerMock;

  @Before
  public void setUp() throws GCloudExecutionException {
    when(callerMock.getGcloudPath()).thenReturn("here");
    when(callerMock.call()).thenReturn(true);
  }

  @Test
  public void testPrepareCommand() {
    GetLogsAction action = new GetLogsAction(
        ImmutableList.of("module1", "module2"),
        "v1",
        "thisFile",
        ImmutableMap.of(Option.VHOST, "vhost"));

    Set<String> expected = ImmutableSet.of(action.getProcessCaller().getGcloudPath(), "preview",
        "app", "modules", "get-logs", "module1", "module2", "thisFile", "--version", "v1",
        "--vhost", "vhost");
    Set<String> actual = new HashSet<>(action.getProcessCaller().getCommand());
    assertEquals(expected, actual);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyVersion() {
    new GetLogsAction(
        ImmutableList.of("module1", "module2"),
        "",
        "thisFile",
        ImmutableMap.<Option, String>of()
    );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullVersion() {
    new GetLogsAction(
        ImmutableList.of("module1", "module2"),
        null,
        "thisFile",
        ImmutableMap.<Option, String>of()
    );
  }

  @Test
  public void testCheckFlags_allFlags() {
    Map<Option, String> flags = new HashMap<>();
    flags.put(Option.APPEND, "this and that");
    flags.put(Option.DAYS, "3");
    flags.put(Option.DETAILS, "true");
    flags.put(Option.END_DATE, "tomorrow");
    flags.put(Option.SERVER, "server.com");
    flags.put(Option.SEVERITY, "debug");
    flags.put(Option.VHOST, "vhost");

    new GetLogsAction(
        ImmutableList.of("module1", "module2"),
        "v1",
        "thisFile",
        flags
    );
  }

  @Test
  public void testCheckFlags_oneFlag() {
    Map<Option, String> flags = ImmutableMap.of(Option.VHOST, "vhost");
    new GetLogsAction(
        ImmutableList.of("module1", "module2"),
        "v1",
        "thisFile",
        flags
    );
  }

  @Test(expected = InvalidFlagException.class)
  public void testCheckFlags_error() {
    Map<Option, String> flags = ImmutableMap.of(
        Option.SERVER, "server.com",
        Option.ADMIN_HOST, "disallowed flag!!!"
    );

    new GetLogsAction(
        ImmutableList.of("module1", "module2"),
        "v1",
        "thisFile",
        flags
    );
  }

  @Test
  public void testExecute() throws GCloudExecutionException, IOException {
    GetLogsAction action = new GetLogsAction(
        ImmutableList.of("module1", "module2"),
        "v1",
        "thisFile",
        ImmutableMap.<Option, String>of()
    );
    action.setProcessCaller(callerMock);

    action.execute();

    verify(callerMock, times(1)).call();
  }
}
