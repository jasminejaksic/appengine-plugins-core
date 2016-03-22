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
package com.google.cloud.tools.app;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Stops the local development server.
 */
public class StopAction extends Action {

  private static String DEFAULT_ADMIN_HOST = "localhost";
  private static int DEFAULT_ADMIN_PORT = 8000;
  private static Logger LOG = Logger.getLogger(StopAction.class.getName());

  private static Set<Option> acceptedFlags = ImmutableSet.of(
      Option.ADMIN_PORT,
      Option.ADMIN_HOST,
      Option.SERVER
  );

  private HttpURLConnection connection;

  public StopAction(Map<Option, String> flags) throws GCloudExecutionException {
    super(flags);
    checkFlags(flags, acceptedFlags);

    try {
      URL adminServerUrl = new URL(
          "http",
          flags.containsKey(Option.ADMIN_HOST) ? flags.get(Option.ADMIN_HOST) : DEFAULT_ADMIN_HOST,
          flags.containsKey(Option.ADMIN_PORT) ?
              Integer.parseInt(flags.get(Option.ADMIN_PORT)) : DEFAULT_ADMIN_PORT,
          "/quit");
      connection = (HttpURLConnection) adminServerUrl.openConnection();
    } catch (IOException ex) {
      throw new GCloudExecutionException(ex);
    }
  }

  @VisibleForTesting
  public void setConnection(HttpURLConnection connection) {
    this.connection = connection;
  }

  /**
   * Issues a HTTP GET request to /quit of the admin port of the local development server admin
   * host.
   */
  @Override
  public boolean execute() throws IOException {
    connection.setReadTimeout(4000);
    connection.connect();
    connection.disconnect();
    int responseCode = connection.getResponseCode();
    if (responseCode < 200 || responseCode > 299) {
      LOG.severe("The development server responded with " + connection.getResponseMessage() + ".");
      return false;
    }

    return true;
  }
}
