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

package com.google.cloud.tools.app.deploy;

import com.google.cloud.tools.app.api.deploy.DeployConfiguration;
import com.google.cloud.tools.app.deploy.process.OutputHandler;

import java.util.concurrent.Future;

// this is the API to deploy, it's assumed to be asynchronous and returns
// a future to manipulate the process

public interface Deploy {

  Future<String> deploy(DeployConfiguration config);

  // should this be part of the API? Getting non-result related output?
  void setOutputHandler(OutputHandler handler);

}
