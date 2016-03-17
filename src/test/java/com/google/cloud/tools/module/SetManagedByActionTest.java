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
package com.google.cloud.tools.module;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.tools.GCloudErrorException;
import com.google.cloud.tools.InvalidFlagException;
import com.google.cloud.tools.Option;
import com.google.cloud.tools.ProcessCaller;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Unit tests for {@link SetManagedByAction}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SetManagedByActionTest {
  @Mock
  private ProcessCaller callerMock;

  @Before
  public void setUp() throws GCloudErrorException {
    when(callerMock.getGcloudPath()).thenReturn("here");
    when(callerMock.call()).thenReturn(true);
  }

  @Test
  public void testPrepareCommand() {
    // Specifies a module.
    SetManagedByAction action = new SetManagedByAction(
        ImmutableList.of("module1"),
        "v1",
        Option.SELF,
        ImmutableMap.<Option, String>of());

    Set<String> expected = ImmutableSet.of(action.getProcessCaller().getGcloudPath(), "preview",
        "app", "modules", "set-managed-by", "module1", "--version", "v1", "--self");
    Set<String> actual = new HashSet<>();
    actual.addAll(action.getProcessCaller().getCommand());
    assertEquals(expected, actual);

    // Specifies no module.
    action = new SetManagedByAction(
        ImmutableList.<String>of(),
        "v1",
        Option.GOOGLE,
        ImmutableMap.of(Option.INSTANCE, "xkyz", Option.SERVER, "server.com")
    );

    expected = ImmutableSet.of(action.getProcessCaller().getGcloudPath(), "preview", "app",
        "modules", "set-managed-by", "--version", "v1", "--google", "--instance", "xkyz",
        "--server", "server.com");
    actual = new HashSet<>();
    actual.addAll(action.getProcessCaller().getCommand());

    assertEquals(expected, actual);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyVersion() {
    new SetManagedByAction(
        ImmutableList.of("module1", "module2"),
        "",
        Option.SELF,
        ImmutableMap.<Option, String>of()
    );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullVersion() {
    new SetManagedByAction(
        ImmutableList.of("module1", "module2"),
        null,
        Option.SELF,
        ImmutableMap.<Option, String>of()
    );
  }

  @Test(expected = NullPointerException.class)
  public void testNullManagedBy() {
    new SetManagedByAction(
        ImmutableList.of("module1", "module2"),
        "v1",
        null,
        ImmutableMap.<Option, String>of()
    );
  }

  @Test
  // All flags are accepted.
  public void testCheckFlags() {
    Map<Option, String> flags = ImmutableMap.of(
        Option.INSTANCE, "xyz",
        Option.SERVER, "server.com"
    );

    new SetManagedByAction(
        ImmutableList.of("module1", "module2"),
        "v1",
        Option.SELF,
        flags
    );

    // Not every accepted flag goes. Which is OK.
    flags = ImmutableMap.of(Option.INSTANCE, "xyz");
    new SetManagedByAction(
        ImmutableList.of("module1", "module2"),
        "v1",
        Option.SELF,
        flags
    );
  }

  @Test(expected = InvalidFlagException.class)
  public void testCheckFlags_error() {
    Map<Option, String> flags = ImmutableMap.of(
        Option.INSTANCE, "xyz",
        Option.SERVER, "server.com",
        Option.ADMIN_HOST, "disallowed flag!!!"
    );

    new SetManagedByAction(
        ImmutableList.of("module1", "module2"),
        "v1",
        Option.SELF,
        flags
    );
  }

  @Test
  public void testGetAcceptedFlags() {
    Set<Option> expectedFlags = ImmutableSet.of(
        Option.INSTANCE,
        Option.SERVER);

    SetManagedByAction action = new SetManagedByAction(
        ImmutableList.of("module1", "module2"),
        "v1",
        Option.SELF,
        ImmutableMap.<Option, String>of()
    );

    assertEquals(expectedFlags, action.getAcceptedFlags());
  }

  @Test
  public void testExecute() throws GCloudErrorException {
    SetManagedByAction action = new SetManagedByAction(
        ImmutableList.of("module1", "module2"),
        "v1",
        Option.SELF,
        ImmutableMap.<Option, String>of()
    );
    action.setProcessCaller(callerMock);

    action.execute();

    verify(callerMock, times(1)).call();
  }
}
