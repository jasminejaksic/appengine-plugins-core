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

package com.google.cloud.tools.appengine.experimental.internal.process.io;

import com.google.cloud.tools.appengine.experimental.OutputHandler;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by appu on 8/22/16.
 */
public class CollectingOutputHandler implements OutputHandler {

  private List<String> lines = Lists.newArrayList();

  @Override
  public void handleLine(String line) {
    lines.add(line);
  }

  public List<String> getLines() {
    return lines;
  }
}
