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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Set;

/**
 * Unit tests for {@link StopAction}.
 */
@RunWith(MockitoJUnitRunner.class)
public class StopActionTest {

  @Mock
  HttpURLConnection connection;

  @Before
  public void setUp() throws IOException {
    doNothing().when(connection).setReadTimeout(anyInt());
    doNothing().when(connection).connect();
    doNothing().when(connection).disconnect();
    when(connection.getResponseMessage()).thenReturn("response");
  }

  @Test
  public void testGetAcceptedFlags() throws GCloudErrorException {
    StopAction action = new StopAction(ImmutableMap.<Option, String>of());

    Set<Option> expected = ImmutableSet.of(
        Option.ADMIN_PORT,
        Option.ADMIN_HOST,
        Option.SERVER);

    assertEquals(expected, action.getAcceptedFlags());
  }

  @Test
  public void testCheckFlags() throws GCloudErrorException {
    Map<Option, String> flags = ImmutableMap.of(
        Option.ADMIN_HOST, "adminHost",
        Option.ADMIN_PORT, "9090",
        Option.SERVER, "server.com"
    );

    new StopAction(flags);
  }

  @Test(expected = InvalidFlagException.class)
  public void testCheckFlags_error() throws GCloudErrorException {
    Map<Option, String> flags = ImmutableMap.of(
        Option.ADMIN_HOST, "adminHost",
        Option.PORT, "9000"
    );

    new StopAction(flags);
  }

  @Test
  public void testSendRequest() throws GCloudErrorException, IOException {
    when(connection.getResponseCode()).thenReturn(200);
    StopAction action = new StopAction(ImmutableMap.<Option, String>of());
    action.setConnection(connection);
    assertTrue(action.execute());
  }

  @Test(expected = GCloudErrorException.class)
  public void testSendRequest_less200() throws GCloudErrorException, IOException {
    when(connection.getResponseCode()).thenReturn(100);
    StopAction action = new StopAction(ImmutableMap.<Option, String>of());
    action.setConnection(connection);
    assertTrue(action.execute());
  }

  @Test(expected = GCloudErrorException.class)
  public void testSendRequest_500() throws GCloudErrorException, IOException {
    when(connection.getResponseCode()).thenReturn(500);
    StopAction action = new StopAction(ImmutableMap.<Option, String>of());
    action.setConnection(connection);
    assertTrue(action.execute());
  }
}
