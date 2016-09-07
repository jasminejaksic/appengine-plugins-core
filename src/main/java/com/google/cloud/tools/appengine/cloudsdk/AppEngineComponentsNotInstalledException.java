package com.google.cloud.tools.appengine.cloudsdk;

import com.google.cloud.tools.appengine.api.AppEngineException;

public class AppEngineComponentsNotInstalledException extends AppEngineException {

  AppEngineComponentsNotInstalledException(String message) {
    super(message);
  }

}
