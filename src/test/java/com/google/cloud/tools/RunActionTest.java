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
 * Unit tests for {@link RunAction}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RunActionTest {

  @Mock
  ProcessCaller callerMock;

  @Before
  public void setUp() throws GCloudErrorException {
    when(callerMock.getGcloudPath()).thenReturn("here");
    when(callerMock.call()).thenReturn(true);
  }

  @Test
  public void testPrepareCommand() {
    // With flags.
    Map<Option, String> flags = new HashMap();
    flags.put(Option.HOST, "host");
    flags.put(Option.PORT, "8000");
    flags.put(Option.ADMIN_HOST, "adminHost");
    flags.put(Option.ADMIN_PORT, "8080");
    flags.put(Option.AUTH_DOMAIN, "example.com");
    flags.put(Option.STORAGE_PATH, "storage path");
    flags.put(Option.LOG_LEVEL, "debug");
    flags.put(Option.MAX_MODULE_INSTANCES, "3");
    flags.put(Option.USE_MTIME_FILE_WATCHER, "true");
    flags.put(Option.THREADSAFE_OVERRIDE, "default:False,backend:True");
    flags.put(Option.PYTHON_STARTUP_SCRIPT, "start this");
    flags.put(Option.PYTHON_STARTUP_ARGS, "arguments");
    flags.put(Option.JVM_FLAG, "flag");
    flags.put(Option.CUSTOM_ENTRYPOINT, "custom entrypoint");
    flags.put(Option.RUNTIME, "java");
    flags.put(Option.ALLOW_SKIPPED_FILES, "true");
    flags.put(Option.API_PORT, "8091");
    flags.put(Option.AUTOMATIC_RESTART, "true");
    flags.put(Option.DEV_APPSERVER_LOG_LEVEL, "info");
    flags.put(Option.SKIP_SDK_UPDATE_CHECK, "false");
    flags.put(Option.DEFAULT_GCS_BUCKET_NAME, "buckets");

    RunAction action = new RunAction("app.yaml", true, flags);

    Set<String> expected = ImmutableSet.of(action.getProcessCaller().getDevAppserverPath(),
        "app.yaml", "--threadsafe_override", "default:False,backend:True", "--storage_path",
        "storage path", "--python_startup_script", "start this", "--host", "host",
        "--default_gcs_bucket_name", "buckets", "--automatic_restart", "true",
        "--dev_appserver_log_level", "info", "--runtime", "java", "--skip_sdk_update_check",
        "false", "--admin_port", "8080", "--port", "8000", "--use_mtime_file_watcher", "true",
        "--admin_host", "adminHost", "--log_level", "debug", "--max_module_instances", "3",
        "--jvm_flag", "flag", "--allow_skipped_files", "true", "--custom_entrypoint",
        "custom entrypoint", "--api_port", "8091", "--auth_domain", "example.com",
        "--python_startup_args", "arguments");
    Set<String> actual = new HashSet<>();
    actual.addAll(action.getProcessCaller().getCommand());
    assertEquals(expected, actual);

    // Non-synchronous produces the same command.
    action = new RunAction("app.yaml", false, flags);
    actual = new HashSet<>();
    actual.addAll(action.getProcessCaller().getCommand());
    assertEquals(expected, actual);

    // Without flags.
    flags = ImmutableMap.of();
    expected = ImmutableSet.of(action.getProcessCaller().getDevAppserverPath(), "app.yaml");
    action = new RunAction("app.yaml", true, flags);
    actual = new HashSet<>();
    actual.addAll(action.getProcessCaller().getCommand());

    assertEquals(expected, actual);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyAppYaml() {
    new RunAction("", false, ImmutableMap.<Option, String>of());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullAppYaml() {
    new RunAction(null, false, ImmutableMap.<Option, String>of());
  }

  @Test
  // All flags are accepted.
  public void testCheckFlags() {
    Map<Option, String> flags = new HashMap();
    flags.put(Option.HOST, "host");
    flags.put(Option.PORT, "8000");
    flags.put(Option.ADMIN_HOST, "adminHost");
    flags.put(Option.ADMIN_PORT, "8080");
    flags.put(Option.AUTH_DOMAIN, "example.com");
    flags.put(Option.STORAGE_PATH, "storage path");
    flags.put(Option.LOG_LEVEL, "debug");
    flags.put(Option.MAX_MODULE_INSTANCES, "3");
    flags.put(Option.USE_MTIME_FILE_WATCHER, "true");
    flags.put(Option.THREADSAFE_OVERRIDE, "default:False,backend:True");
    flags.put(Option.PYTHON_STARTUP_SCRIPT, "start this");
    flags.put(Option.PYTHON_STARTUP_ARGS, "arguments");
    flags.put(Option.JVM_FLAG, "flag");
    flags.put(Option.CUSTOM_ENTRYPOINT, "custom entrypoint");
    flags.put(Option.RUNTIME, "java");
    flags.put(Option.ALLOW_SKIPPED_FILES, "true");
    flags.put(Option.API_PORT, "8091");
    flags.put(Option.AUTOMATIC_RESTART, "true");
    flags.put(Option.DEV_APPSERVER_LOG_LEVEL, "info");
    flags.put(Option.SKIP_SDK_UPDATE_CHECK, "false");
    flags.put(Option.DEFAULT_GCS_BUCKET_NAME, "buckets");

    new RunAction("app.yaml", false, flags);

    // Not every accepted flag goes. Which is OK.
    flags = ImmutableMap.of(Option.DEFAULT_GCS_BUCKET_NAME, "buckets");
    new RunAction("app.yaml", false, flags);
  }

  @Test(expected = InvalidFlagException.class)
  public void testCheckFlags_error() {
    Map<Option, String> flags = ImmutableMap.of(
        Option.CONFIG, "app.yaml",
        Option.CUSTOM, "true",
        Option.RUNTIME, "java",
        Option.SERVER, "server.com",
        Option.INSTANCE, "disallowed flag!!!"
    );

    new RunAction("app.yaml", false, flags);
  }

  @Test
  public void testGetAcceptedFlags() {
    Set<Option> expectedFlags = ImmutableSet.of(
        Option.HOST,
        Option.PORT,
        Option.ADMIN_HOST,
        Option.ADMIN_PORT,
        Option.AUTH_DOMAIN,
        Option.STORAGE_PATH,
        Option.LOG_LEVEL,
        Option.MAX_MODULE_INSTANCES,
        Option.USE_MTIME_FILE_WATCHER,
        Option.THREADSAFE_OVERRIDE,
        Option.PYTHON_STARTUP_SCRIPT,
        Option.PYTHON_STARTUP_ARGS,
        Option.JVM_FLAG,
        Option.CUSTOM_ENTRYPOINT,
        Option.RUNTIME,
        Option.ALLOW_SKIPPED_FILES,
        Option.API_PORT,
        Option.AUTOMATIC_RESTART,
        Option.DEV_APPSERVER_LOG_LEVEL,
        Option.SKIP_SDK_UPDATE_CHECK,
        Option.DEFAULT_GCS_BUCKET_NAME);

    RunAction action = new RunAction("app.yaml", false, ImmutableMap.<Option, String>of());

    assertEquals(expectedFlags, action.getAcceptedFlags());
  }

  @Test
  public void testExecute() throws GCloudErrorException {
    RunAction action = new RunAction("app.yaml", false, ImmutableMap.<Option, String>of());
    action.setProcessCaller(callerMock);

    action.execute();

    verify(callerMock, times(1)).call();
  }
}
