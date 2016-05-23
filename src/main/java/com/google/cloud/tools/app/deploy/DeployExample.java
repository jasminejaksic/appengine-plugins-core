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

import com.google.cloud.tools.app.deploy.gcloud.GcloudAppDeploy;
import com.google.cloud.tools.app.deploy.process.LoggingOutputHandler;
import com.google.cloud.tools.app.impl.cloudsdk.internal.sdk.CloudSdk;
import com.google.cloud.tools.app.impl.config.DefaultDeployConfiguration;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class DeployExample {

  public static void howToUse() {
    // presumably the sdk object comes from somewhere else, but lets build it here
    // you can also set enviroment on it
    CloudSdk sdk = new CloudSdk.Builder().sdkPath(new File("some/path")).build();

    // create the deploy object
    Deploy deploy = GcloudAppDeploy.newBuilder(sdk).build();

    // set the output handler for non-result output
    deploy.setOutputHandler(new LoggingOutputHandler("test", Level.INFO));

    // start the deployment
    Future<String> deployFuture = deploy.deploy(new DefaultDeployConfiguration());

    try {
      // get the result -- a waiting call
      deployFuture.get();
      // or kill it!
      deployFuture.cancel(true);
      // or whatever, I don't know, anything a future can do

    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
  }
}
