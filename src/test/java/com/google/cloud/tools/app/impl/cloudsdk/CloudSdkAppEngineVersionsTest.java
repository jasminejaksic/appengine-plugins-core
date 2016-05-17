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

package com.google.cloud.tools.app.impl.cloudsdk;

import com.google.cloud.tools.app.impl.cloudsdk.internal.process.ProcessRunnerException;
import com.google.cloud.tools.app.impl.cloudsdk.internal.sdk.CloudSdk;
import com.google.cloud.tools.app.impl.config.DefaultVersionsSelectionConfiguration;
import com.google.cloud.tools.app.impl.config.DefaultVersionsListConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link CloudSdkAppEngineVersions}
 */
@RunWith(MockitoJUnitRunner.class)
public class CloudSdkAppEngineVersionsTest {

  @Mock
  private CloudSdk sdk;

  @Test
  public void startTest() throws ProcessRunnerException {
    CloudSdkAppEngineVersions appEngineVersion = new CloudSdkAppEngineVersions(sdk);
    appEngineVersion.start(getVersionConfig());

    List<String> args = Arrays.asList("versions", "start", "v1", "v2", "--service", "myService");

    verify(sdk, times(1)).runAppCommand(eq(args));
  }

  @Test
  public void stopTest() throws ProcessRunnerException {
    CloudSdkAppEngineVersions appEngineVersion = new CloudSdkAppEngineVersions(sdk);
    appEngineVersion.stop(getVersionConfig());

    List<String> args = Arrays.asList("versions", "stop", "v1", "v2", "--service", "myService");

    verify(sdk, times(1)).runAppCommand(eq(args));
  }

  @Test
  public void deleteTest() throws ProcessRunnerException {
    CloudSdkAppEngineVersions appEngineVersion = new CloudSdkAppEngineVersions(sdk);
    appEngineVersion.delete(getVersionConfig());

    List<String> args = Arrays.asList("versions", "delete", "v1", "v2", "--service", "myService");

    verify(sdk, times(1)).runAppCommand(eq(args));
  }

  @Test
  public void listTest_doHideNoTraffic() throws ProcessRunnerException {
    CloudSdkAppEngineVersions appEngineVersion = new CloudSdkAppEngineVersions(sdk);
    appEngineVersion.list(getListConfig(true));

    List<String> args = Arrays
        .asList("versions", "list", "--service", "myService", "--hide-no-traffic");

    verify(sdk, times(1)).runAppCommand(eq(args));
  }

  @Test
  public void listTest_dontHideNoTraffic() throws ProcessRunnerException {
    CloudSdkAppEngineVersions appEngineVersion = new CloudSdkAppEngineVersions(sdk);
    appEngineVersion.list(getListConfig(false));

    List<String> args = Arrays
        .asList("versions", "list", "--service", "myService", "--no-hide-no-traffic");

    verify(sdk, times(1)).runAppCommand(eq(args));
  }

  private DefaultVersionsSelectionConfiguration getVersionConfig() {
    DefaultVersionsSelectionConfiguration configuration = new DefaultVersionsSelectionConfiguration();
    configuration.setVersions(Arrays.asList("v1", "v2"));
    configuration.setService("myService");
    return configuration;
  }

  private DefaultVersionsListConfiguration getListConfig(boolean hideNoTraffic) {
    DefaultVersionsListConfiguration listConfiguration = new DefaultVersionsListConfiguration();
    listConfiguration.setService("myService");
    listConfiguration.setHideNoTraffic(hideNoTraffic);
    return listConfiguration;
  }
}