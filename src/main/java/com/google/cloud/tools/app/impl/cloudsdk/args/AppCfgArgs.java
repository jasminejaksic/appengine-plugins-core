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

package com.google.cloud.tools.app.impl.cloudsdk.args;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Command Line argument helper.
 */
public class AppCfgArgs {

  /**
   * @return [--name=value] or [] if value=null.
   */
  public static List<String> get(String name, String value) {
    if (!Strings.isNullOrEmpty(value)) {
      return Collections.singletonList("--" + name + "=" + value);
    }
    return Collections.emptyList();
  }

  /**
   * @return [--name] if value=true, [] if value=false/null.
   */
  public static List<String> get(String name, Boolean value) {
    if (Boolean.TRUE.equals(value)) {
      return Collections.singletonList("--" + name);
    }
    return Collections.emptyList();
  }
}
