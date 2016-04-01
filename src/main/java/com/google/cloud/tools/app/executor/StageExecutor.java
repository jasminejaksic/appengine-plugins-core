package com.google.cloud.tools.app.executor;

import java.util.List;

/**
 * Created by appu on 4/1/16.
 */
public interface StageExecutor {

  int runStage(List<String> args);

}
