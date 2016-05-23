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

package com.google.cloud.tools.app.deploy.gcloud;

import com.google.cloud.tools.app.api.AppEngineException;
import com.google.cloud.tools.app.api.deploy.DeployConfiguration;
import com.google.cloud.tools.app.deploy.Deploy;
import com.google.cloud.tools.app.deploy.process.CliProcessStarter;
import com.google.cloud.tools.app.deploy.process.LoggingOutputHandler;
import com.google.cloud.tools.app.deploy.process.OutputHandler;
import com.google.cloud.tools.app.deploy.process.ProcessStarter;
import com.google.cloud.tools.app.deploy.process.StreamConsumer;
import com.google.cloud.tools.app.impl.cloudsdk.internal.sdk.CloudSdk;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class GcloudAppDeploy implements Deploy {
  private final CloudSdk sdk;
  private final ConfigurationTranslator<DeployConfiguration> configurationTranslator;
  private OutputHandler outputHandler;
  private ProcessStarter processStarter;

  private GcloudAppDeploy(CloudSdk sdk,
      ConfigurationTranslator<DeployConfiguration> configurationTranslator,
      ProcessStarter processStarter) {
    this.sdk = sdk;
    this.processStarter = processStarter;
    this.configurationTranslator = configurationTranslator;
  }

  @Override
  public Future<String> deploy(DeployConfiguration config) {
    try {
      String[] command = sdk.createAppCommand("deploy", configurationTranslator.translate(config));
      Process process = processStarter.startProcess(command);

      if (outputHandler == null) {
        outputHandler = new LoggingOutputHandler(GcloudAppDeploy.class.getName(), Level.INFO);
      }
      StreamConsumer.startNewConsumer(process.getErrorStream(), outputHandler);

      return new GcloudFuture(process);

    } catch(IOException e) {
      throw new AppEngineException("Error deploying", e);
    }
  }

  // It feels strange that this isn't part of the builder and is part of the deploy API? But I'm
  // not sure what the right thing to do is. Should it be an implementation detail? or should the
  // develop always have access to the output of the Deployer?
  @Override
  public void setOutputHandler(OutputHandler outputHandler) {
    this.outputHandler = outputHandler;
  }

  public static Builder newBuilder(CloudSdk sdk) {
    // there's probably some work to get the sdk refactored as storage for path/environment and stuff
    return new Builder(sdk);
  }

  // this will probably be similar for all commands, but I don't know how to
  // abstract it out yet, or if I even should?
  public static class Builder {
    private CloudSdk sdk;
    private ProcessStarter processStarter;
    private ConfigurationTranslator<DeployConfiguration> configurationTranslator;

    private Builder(CloudSdk sdk) {
      this.sdk = sdk;
      // getEnvironment doesn't exist
      this.processStarter = CliProcessStarter.newBuilder().environment(sdk.getEnvironment()).build();
      this.configurationTranslator = new GcloudAppDeployTranslator();
    }

    public Builder processStarter(ProcessStarter processStarter) {
      this.processStarter = processStarter;
      return this;
    }

    public Builder configurationTranslator(
        ConfigurationTranslator<DeployConfiguration> configurationTranslator) {
      this.configurationTranslator = configurationTranslator;
      return this;
    }

    public GcloudAppDeploy build() {
      return new GcloudAppDeploy(sdk, configurationTranslator, processStarter);
    }
  }
}
