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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
 * Unit tests for {@link DeployAction}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeployActionTest {

  @Mock
  private ProcessCaller callerMock;
  private static Map<Option, String> NO_FLAGS = ImmutableMap.of();

  @Before
  public void setUp() throws GCloudExecutionException {
    when(callerMock.getGcloudPath()).thenReturn("here");
    when(callerMock.call()).thenReturn(true);
  }

  @Test
  public void testPrepareCommand_noFlags() {
    DeployAction action = new DeployAction(".", NO_FLAGS);

    Set<String> expected = ImmutableSet.of(action.getProcessCaller().getGcloudPath(), "preview",
        "app", "deploy", "./app.yaml", "--quiet");
    Set<String> actual = new HashSet<>(action.getProcessCaller().getCommand());
    assertEquals(expected, actual);
  }

  @Test
  public void testPrepareCommand_withFlags() {
    Map<Option, String> flags = new HashMap<>();
    flags.put(Option.BUCKET, "bucket");
    flags.put(Option.DOCKER_BUILD, "docker");
    flags.put(Option.FORCE, "true");
    flags.put(Option.IMAGE_URL, "image url");
    flags.put(Option.PROMOTE, "true");
    flags.put(Option.SERVER, "server.com");
    flags.put(Option.VERSION, "v1");

    DeployAction action = new DeployAction(".", flags);
    Set<String> expected = ImmutableSet.of(action.getProcessCaller().getGcloudPath(), "preview",
        "app", "deploy", "./app.yaml", "--bucket", "bucket", "--docker-build", "docker",
        "--force", "true", "--image-url", "image url", "--promote", "true", "--server",
        "server.com", "--version", "v1", "--quiet");
    Set<String> actual = new HashSet<>(action.getProcessCaller().getCommand());
    assertEquals(expected, actual);
  }

  @Test(expected = NullPointerException.class)
  public void testNullStaging() {
    new DeployAction(null, NO_FLAGS);
  }

  @Test
  public void testCheckFlags_allGood() {
    Map<Option, String> flags = new HashMap<>();
    flags.put(Option.BUCKET, "gsbucket");
    flags.put(Option.DOCKER_BUILD, "image");
    flags.put(Option.FORCE, "false");
    flags.put(Option.IMAGE_URL, "image url");
    flags.put(Option.PROMOTE, "true");
    flags.put(Option.SERVER, "google.com");
    flags.put(Option.VERSION, "v1");

    new DeployAction(".", flags);
  }

  @Test
  public void testCheckFlags_onlyOne() {
    Map<Option, String> flags = ImmutableMap.of(Option.SERVER, "google.com");
    new DeployAction(".", flags);
  }

  @Test(expected = InvalidFlagException.class)
  public void testCheckFlags_error() {
    Map<Option, String> flags = ImmutableMap.of(
        Option.SERVER, "server.com",
        Option.ADMIN_HOST, "disallowed flag!!!"
    );

    new DeployAction(".", flags);
  }

  @Test
  public void testExecute() throws GCloudExecutionException, IOException {
    DeployAction action = new DeployAction(".", NO_FLAGS);
    action.setProcessCaller(callerMock);

    action.execute();

    verify(callerMock, times(1)).call();
  }
}
