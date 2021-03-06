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

package com.google.cloud.tools.appengine.cloudsdk.serialization;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link CloudSdkComponent}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CloudSdkComponentTest {

  @Test
  public void testFromJson() {
    CloudSdkComponent result = CloudSdkComponent.fromJson(getCloudSdkComponentTestFixtureAsJson());
    CloudSdkComponent expected = getCloudSdkComponentTestFixture();
    assertCloudSdkComponentsEqual(expected, result);
  }

  @Test
  public void testFromJsonList_nonempty() {
    String jsonList = "[" + getCloudSdkComponentTestFixtureAsJson() + "]";
    List<CloudSdkComponent> result = CloudSdkComponent.fromJsonList(jsonList);

    assertEquals(1, result.size());
    assertCloudSdkComponentsEqual(getCloudSdkComponentTestFixture(), result.get(0));
  }

  @Test
  public void testFromJsonList_empty() {
    String emptyList = "[]";
    List<CloudSdkComponent> result = CloudSdkComponent.fromJsonList(emptyList);

    assertEquals(0, result.size());
  }

  @Test
  public void testToJson() {
    CloudSdkComponent cloudSdkComponent = getCloudSdkComponentTestFixture();
    String result = cloudSdkComponent.toJson();

    // Since the ordering of fields in JSON objects is not guaranteed, we cannot compare the full
    // strings for equality. Instead, use regexes to validate that key/value pairs are present.
    assertJsonKeyValueExists("current_version_string", cloudSdkComponent.getCurrentVersion(),
        result);
    assertJsonKeyValueExists("id", cloudSdkComponent.getId(), result);
    assertJsonKeyValueExists("is_configuration", cloudSdkComponent.getIsConfiguration(), result);
    assertJsonKeyValueExists("is_hidden", cloudSdkComponent.getIsHidden(), result);
    assertJsonKeyValueExists("latest_version_string", cloudSdkComponent.getLatestVersion(),
        result);
    assertJsonKeyValueExists("name", cloudSdkComponent.getName(), result);
    assertJsonKeyValueExists("size", cloudSdkComponent.getSizeInBytes(), result);
  }

  @Test
  public void testToAndFromJson() {
    CloudSdkComponent initial = getCloudSdkComponentTestFixture();
    String serialized = initial.toJson();
    CloudSdkComponent result = CloudSdkComponent.fromJson(serialized);
    assertCloudSdkComponentsEqual(initial, result);
  }

  @Test
  public void testStateIsInstalled_true() {
    CloudSdkComponent.State state = new CloudSdkComponent.State();
    state.setName("Installed");
    assertTrue(state.isInstalled());
  }

  @Test
  public void testStateIsInstalled_false() {
    List<String> notInstalledStates = Arrays.asList("uninstalled", "not installed", "", null);
    for (String stateName : notInstalledStates) {
      CloudSdkComponent.State state = new CloudSdkComponent.State();
      state.setName(stateName);
      assertFalse(state.isInstalled());
    }
  }

  private void assertJsonKeyValueExists(String expectedKey, String expectedValue, String result) {
    String regex = String.format(".*%s\":\\s*\"%s\".*", expectedKey, expectedValue);
    assertTrue(result.matches(regex));
  }

  private void assertJsonKeyValueExists(String expectedKey, int expectedValue, String result) {
    String regex = String.format(".*%s\":\\s*%s.*", expectedKey, expectedValue);
    assertTrue(result.matches(regex));
  }

  private void assertJsonKeyValueExists(String expectedKey, boolean expectedValue, String result) {
    String regex = String.format(".*%s\":\\s*%s.*", expectedKey, expectedValue);
    assertTrue(result.matches(regex));
  }

  private String getCloudSdkComponentTestFixtureAsJson() {
    return "{" +
        "\"current_version_string\": \"1.9.43\"," +
        "\"id\": \"app-engine-java\"," +
        "\"is_configuration\": false," +
        "\"is_hidden\": false," +
        "\"latest_version_string\": \"1.9.44\"," +
        "\"name\": \"gcloud app Java Extensions\"," +
        "\"size\": 138442691," +
        "\"state\": { " +
        "  \"name\": \"Installed\" " +
        "}" +
      "}";
  }

  private CloudSdkComponent getCloudSdkComponentTestFixture() {
    CloudSdkComponent.State state = new CloudSdkComponent.State();
    state.setName("Installed");

    CloudSdkComponent expected = new CloudSdkComponent();
    expected.setState(state);
    expected.setCurrentVersion("1.9.43");
        expected.setId("app-engine-java");
    expected.setIsConfiguration(false);
    expected.setIsHidden(false);
    expected.setLatestVersionString("1.9.44");
    expected.setName("gcloud app Java Extensions");
    expected.setSizeInBytes(138442691);

    return expected;
  }

  private void assertCloudSdkComponentsEqual(CloudSdkComponent expected, CloudSdkComponent actual) {
    assertEquals(expected.getCurrentVersion(), actual.getCurrentVersion());
    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getIsConfiguration(), actual.getIsConfiguration());
    assertEquals(expected.getIsHidden(), actual.getIsHidden());
    assertEquals(expected.getLatestVersion(), actual.getLatestVersion());
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getSizeInBytes(), actual.getSizeInBytes());
    assertEquals(expected.getState().getName(), actual.getState().getName());
  }

}
