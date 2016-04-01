package com.google.cloud.tools.app.executor;

import com.google.appengine.tools.admin.AppCfg;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by appu on 4/1/16.
 */
public class AppCfgStageExecutor implements StageExecutor {

  private final Path appengineSdk;

  public AppCfgStageExecutor(Path appengineSdk) {
    this.appengineSdk = appengineSdk;
  }

  @Override
  public int runStage(List<String> args) {
    args.add(0, "stage");
    // AppCfg requires this system property to be set.
    System.setProperty("appengine.sdk.root", appengineSdk.toString());
    AppCfg.main(args.toArray(new String[args.size()]));
    return 0;
  }
}
