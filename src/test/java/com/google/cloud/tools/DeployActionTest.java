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
package com.google.cloud.tools;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.tools.DeployAction.AppType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
  @Mock
  private StageAction stageAction;
  private static Map<Option, String> NO_FLAGS = ImmutableMap.of();

  @Before
  public void setUp() throws GCloudErrorException, InvalidDirectoryException {
    when(callerMock.getGcloudPath()).thenReturn("here");
    when(callerMock.call()).thenReturn(true);
    when(stageAction.execute()).thenReturn(true);
  }

  //@Test
  public void testPrepareCommand() throws InvalidDirectoryException {

    // Without flags.
    DeployAction action = new DeployAction(
        ".",
        AppType.CLASSIC_APP_ENGINE,
        NO_FLAGS,
        stageAction
    );

    Set<String> expected = ImmutableSet.of(action.getProcessCaller().getGcloudPath(), "preview",
        "app", "deploy", "./app.yaml");
    Set<String> actual = new HashSet<>();
    actual.addAll(action.getProcessCaller().getCommand());
    assertEquals(expected, actual);

    // With flags.
    Map<Option, String> flags = new HashMap<>();
    flags.put(Option.BUCKET, "bucket");
    flags.put(Option.DOCKER_BUILD, "docker");
    flags.put(Option.FORCE, "true");
    flags.put(Option.IMAGE_URL, "image url");
    flags.put(Option.PROMOTE, "true");
    flags.put(Option.SERVER, "server.com");

    action = new DeployAction(
        ".",
        AppType.CLASSIC_APP_ENGINE,
        flags,
        stageAction);
    expected = ImmutableSet.of(action.getProcessCaller().getGcloudPath(), "preview", "app",
        "deploy", "./app.yaml", "--bucket", "bucket", "--docker-build", "docker",
        "--force", "true", "--image-url", "image url", "--promote", "true", "--server",
        "server.com");
    actual = new HashSet<>();
    actual.addAll(action.getProcessCaller().getCommand());
    assertEquals(expected, actual);
  }

  @Test(expected = NullPointerException.class)
  public void testNullStaging() throws InvalidDirectoryException {
    new DeployAction(null, AppType.CLASSIC_APP_ENGINE, NO_FLAGS, stageAction);
  }

  @Test(expected = NullPointerException.class)
  public void testNullAppType() throws InvalidDirectoryException {
    new DeployAction(".", null, NO_FLAGS, stageAction);
  }

  @Test(expected = NullPointerException.class)
  public void testNullStageAction() throws InvalidDirectoryException {
    new DeployAction(".", AppType.CLASSIC_APP_ENGINE, NO_FLAGS, null);
  }

  @Test
  // All flags are accepted.
  public void testCheckFlags() throws InvalidDirectoryException {
    Map<Option, String> flags = new HashMap<>();
    flags.put(Option.BUCKET, "gsbucket");
    flags.put(Option.DOCKER_BUILD, "image");
    flags.put(Option.FORCE, "false");
    flags.put(Option.IMAGE_URL, "image url");
    flags.put(Option.PROMOTE, "true");
    flags.put(Option.SERVER, "google.com");

    new DeployAction(".", AppType.CLASSIC_APP_ENGINE, flags, stageAction);

    // Not every accepted flag goes. Which is OK.
    flags = ImmutableMap.of(Option.SERVER, "google.com");
    new DeployAction(".", AppType.CLASSIC_APP_ENGINE, flags, stageAction);
  }

  @Test(expected = InvalidFlagException.class)
  public void testCheckFlags_error() throws InvalidDirectoryException {
    Map<Option, String> flags = ImmutableMap.of(
        Option.SERVER, "server.com",
        Option.ADMIN_HOST, "disallowed flag!!!"
    );

    new DeployAction(".", AppType.CLASSIC_APP_ENGINE, flags, stageAction);
  }

  @Test
  public void testGetAcceptedFlags() throws InvalidDirectoryException {
    Set<Option> expectedFlags = ImmutableSet.of(
        Option.BUCKET,
        Option.DOCKER_BUILD,
        Option.FORCE,
        Option.IMAGE_URL,
        Option.PROMOTE,
        Option.SERVER);


    DeployAction action = new DeployAction(".", AppType.CLASSIC_APP_ENGINE, NO_FLAGS,
        stageAction);

    assertEquals(expectedFlags, action.getAcceptedFlags());
  }

  @Test
  public void testExecute() throws GCloudErrorException, InvalidDirectoryException {
    DeployAction action = new DeployAction(".", AppType.CLASSIC_APP_ENGINE, NO_FLAGS,
        stageAction);
    action.setProcessCaller(callerMock);

    action.execute();

    verify(callerMock, times(1)).call();
  }

  @Test(expected = GCloudErrorException.class)
  public void testStagingFails() throws InvalidDirectoryException, GCloudErrorException {
    when(stageAction.execute()).thenReturn(false);
    DeployAction action = new DeployAction(".", AppType.CLASSIC_APP_ENGINE, NO_FLAGS,
        stageAction);
    action.execute();
  }
}
