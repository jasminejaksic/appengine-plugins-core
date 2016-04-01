package com.google.cloud.tools.app.internal.configuration.processor;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by appu on 3/31/16.
 */
public interface ConfigurationProcessor<T,C> {

  public T process(C configuration) throws InvocationTargetException, IllegalAccessException;

}
