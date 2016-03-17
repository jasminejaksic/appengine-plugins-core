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

/**
 * Contains a flag short ("-f") and long ("--force") forms, along with their type.
 */
public enum Option {
  ENABLE_QUICKSTART("enable_quickstart", Type.BOOLEAN),
  DISABLE_UPDATE_CHECK("disable_update_check", Type.BOOLEAN),
  ENABLE_JAR_SPLITTING("enable_jar_splitting", Type.BOOLEAN),
  JAR_SPLITTING_EXCLUDES("jar_splitting_excludes", Type.STRING),
  RETAIN_UPLOAD_DIR("retain_upload_dir", Type.BOOLEAN),
  COMPILE_ENCODING("compile_encoding", Type.STRING),
  FORCE("f", "force", Type.BOOLEAN),
  DELETE_JSPS("delete_jsps", Type.BOOLEAN),
  ENABLE_JAR_CLASSES("enable_jar_classes", Type.BOOLEAN),
  RUNTIME("r", "runtime", Type.STRING),
  USE_REMOTE_RESOURCE_LIMITS("use_remote_resource_limits", Type.BOOLEAN),
  DISABLE_JAR_JSPS("disable_jar_jsps", Type.BOOLEAN),
  SERVER("s", "server", Type.STRING),
  DOCKER_BUILD("docker-build", Type.STRING),
  IMAGE_URL("image-url", Type.STRING),
  REMOTE("remote", Type.BOOLEAN),
  BUCKET("bucket", Type.STRING),
  PROMOTE("promote", Type.BOOLEAN),
  PORT("port", Type.INTEGER),
  ADMIN_PORT("admin_port", Type.INTEGER),
  HOST("host", Type.STRING),
  APPEND("append", Type.STRING),
  DAYS("days", Type.INTEGER),
  DETAILS("details", Type.BOOLEAN),
  END_DATE("end_date", Type.STRING),
  SEVERITY("severity", Type.STRING),
  VHOST("vhost", Type.STRING),
  INSTANCE("instance", Type.STRING),
  GOOGLE("google", Type.BOOLEAN),
  SELF("self", Type.BOOLEAN),
  ADMIN_HOST("admin_host", Type.STRING),
  AUTH_DOMAIN("auth_domain", Type.STRING),
  STORAGE_PATH("storage_path", Type.STRING),
  LOG_LEVEL("log_level", Type.STRING),
  MAX_MODULE_INSTANCES("max_module_instances", Type.INTEGER),
  USE_MTIME_FILE_WATCHER("use_mtime_file_watcher", Type.BOOLEAN),
  THREADSAFE_OVERRIDE("threadsafe_override", Type.STRING),
  PYTHON_STARTUP_SCRIPT("python_startup_script", Type.STRING),
  PYTHON_STARTUP_ARGS("python_startup_args", Type.STRING),
  JVM_FLAG("jvm_flag", Type.STRING),
  CUSTOM_ENTRYPOINT("custom_entrypoint", Type.STRING),
  ALLOW_SKIPPED_FILES("allow_skipped_files", Type.STRING),
  API_PORT("api_port", Type.INTEGER),
  AUTOMATIC_RESTART("automatic_restart", Type.BOOLEAN),
  DEV_APPSERVER_LOG_LEVEL("dev_appserver_log_level", Type.STRING),
  SKIP_SDK_UPDATE_CHECK("skip_sdk_update_check", Type.STRING),
  DEFAULT_GCS_BUCKET_NAME("default_gcs_bucket_name", Type.STRING),
  CONFIG("config", Type.STRING),
  CUSTOM("custom", Type.BOOLEAN),
  VERSION("v", "version", Type.STRING),
  GCLOUD_PROJECT("project", Type.STRING),
  STOP_PREVIOUS_VERSION("stop-previous-version", Type.BOOLEAN);

  private static final String LONG_FORM_PREFIX = "--";
  private static final String SHORT_FORM_PREFIX = "-";

  private String shortForm;
  private String longForm;
  /**
   * Used to verify validity of some flags and ensure their value is parsable, e.g., to and integer.
   */
  private Type type;

  Option(String longForm, Type type) {
    this.longForm = longForm;
    this.type = type;
  }

  Option(String shortForm, String longForm, Type type) {
    this(longForm, type);
    this.shortForm = shortForm;
  }

  public String getLongForm() {
    return LONG_FORM_PREFIX + longForm;
  }

  public String getShortForm() {
    return SHORT_FORM_PREFIX + shortForm;
  }

  public enum Type {
    STRING,
    INTEGER,
    BOOLEAN;
  }

  public Type getType() {
    return type;
  }
}
