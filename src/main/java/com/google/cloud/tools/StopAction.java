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

import com.google.common.annotations.VisibleForTesting;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Stops the local development server.
 */
public class StopAction extends Action {

  private static String DEFAULT_ADMIN_HOST = "localhost";
  private static int DEFAULT_ADMIN_PORT = 8000;

  private static Option[] acceptedFlags = {
      Option.ADMIN_PORT,
      Option.ADMIN_HOST,
      Option.SERVER

  };

  private HttpURLConnection connection;

  public StopAction(Map<Option, String> flags) throws GCloudErrorException {
    super.acceptedFlags = this.acceptedFlags;
    checkFlags(flags);
    this.flags = flags;

    try {
      URL adminServerUrl = new URL(
          "http",
          flags.containsKey(Option.ADMIN_HOST) ? flags.get(Option.ADMIN_HOST) : DEFAULT_ADMIN_HOST,
          flags.containsKey(Option.ADMIN_PORT) ?
              Integer.parseInt(flags.get(Option.ADMIN_PORT)) : DEFAULT_ADMIN_PORT,
          "/quit");
      connection = (HttpURLConnection) adminServerUrl.openConnection();
    } catch (MalformedURLException mue) {
      throw new GCloudErrorException(mue);
    } catch (IOException ioe) {
      throw new GCloudErrorException(ioe);
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
  public boolean execute() throws GCloudErrorException {
    try {
      connection.setReadTimeout(4000);
      connection.connect();
      connection.disconnect();
      int responseCode = connection.getResponseCode();
      if (responseCode < 200 || responseCode > 299) {
        throw new GCloudErrorException(String.format(
            "The development server responded with %s.", connection.getResponseMessage()));
      }
    } catch (IOException ioEx) {
      throw new GCloudErrorException(ioEx);
    }

    return true;
  }
}
