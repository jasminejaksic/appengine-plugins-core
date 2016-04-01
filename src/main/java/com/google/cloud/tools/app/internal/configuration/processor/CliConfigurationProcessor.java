package com.google.cloud.tools.app.internal.configuration.processor;

import com.google.appengine.repackaged.com.google.api.client.util.Maps;
import com.google.cloud.tools.app.internal.configuration.annotation.Config;
import com.google.cloud.tools.app.internal.configuration.annotation.IgnoreForProcessing;
import com.google.cloud.tools.app.internal.configuration.annotation.Named;
import com.google.cloud.tools.app.internal.configuration.annotation.Positional;
import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

/**
 * TODO : Don't use unsupportedOperationException here
 */
public class CliConfigurationProcessor implements ConfigurationProcessor<List<String>, Object> {

  // Boolean.class is a special case for flags
  List<Class<?>> supportedObjectTypes = Arrays.<Class<?>> asList(Integer.class, int.class, String.class, Path.class);

  private CaseFormat targetFormat = CaseFormat.LOWER_HYPHEN;

  /**
   * @param caseFormat {@link CaseFormat#LOWER_HYPHEN} or {@link CaseFormat#LOWER_UNDERSCORE}
   */
  public void setTargetFormat(CaseFormat caseFormat) {
    targetFormat = caseFormat;
  }

  @Override
  public List<String> process(Object configuration) {

    List<String> flags = Lists.newArrayList();
    TreeMap<Integer, List<String>> positionalArgs = Maps.newTreeMap();

    for (Class<?> interfaze : configuration.getClass().getInterfaces()) {
      if (interfaze.isAnnotationPresent(Config.class)) {
        for (Method method : interfaze.getMethods()) {
          if (method.isAnnotationPresent(IgnoreForProcessing.class)) {
            continue;
          }
          try {
            if (method.isAnnotationPresent(Positional.class)) {
              Type type = method.getReturnType();
              int position = method.getAnnotation(Positional.class).position();
              if (supportedObjectTypes.contains(type)) {
                Object value = method.invoke(configuration);
                if (value != null) {
                  if(!positionalArgs.containsKey(position)) {
                    positionalArgs.put(position, Lists.<String>newArrayList());
                  }
                  positionalArgs.get(position).add(extractValue(method.invoke(configuration)));
                }
              }
              else if (Collection.class.equals(type)) {
                if(!positionalArgs.containsKey(position)) {
                  positionalArgs.put(position, Lists.<String>newArrayList());
                }
                positionalArgs.get(position)
                    .addAll(extractComplexValue((Collection<?>) method.invoke(configuration)));
              }
              else {
                throw new UnsupportedOperationException("Unsupported type : " + type.toString());
              }
            }

            // arguments with parameters
            String name = extractName(method);
            Type type = method.getReturnType();
            if (type.equals(Boolean.class) || type.equals(boolean.class)) {
              flags.add(name);
            }
            else if (supportedObjectTypes.contains(type)) {
              Object value = method.invoke(configuration);
              if (value != null) {
                flags.add(name);
                flags.add(extractValue(method.invoke(configuration)));
              }
            }
            else if (Collection.class.equals(type)) {
              List<String> values = extractComplexValue((Collection<?>) method.invoke(configuration));
              for (String value : values) {
                flags.add(name);
                flags.add(value);
              }
            }
            else {
              throw new UnsupportedOperationException("Unsupported type : " + type.toString());
            }
          } catch (Exception e) {
            throw new UnsupportedOperationException("Failed to process " + method.getName(), e);
          }
        }
      }
    }
    List<String> result = Lists.newArrayList();
    for (List<String> values : positionalArgs.values()) {
      result.addAll(values);
    }
    result.addAll(flags);

    return result;
  }

  private String extractName(Method method) {
    String name = null;

    if (method.isAnnotationPresent(Named.class)) {
      Named named = method.getAnnotation(Named.class);
      if (Strings.isNullOrEmpty(named.name())) {
        name = named.name();
      }
    }
    else if (method.getName().startsWith("get")) {
      name = CaseFormat.LOWER_CAMEL.to(targetFormat, method.getName().substring(3));
    }
    else if (method.getName().startsWith("is")) {
      name = CaseFormat.LOWER_CAMEL.to(targetFormat, method.getName().substring(2));
    }
    else {
      throw new UnsupportedOperationException("Cannot process method " + method.getName());
    }

    return "--" + name;
  }

  private String extractValue(Object value) {
    if (supportedObjectTypes.contains(value.getClass())) {
      return value.toString();
    }

    throw new UnsupportedOperationException("Cannot process types " + value.getClass());
  }

  private List<String> extractComplexValue(Collection<?> values) {
    List<String> result = Lists.newArrayList();
    for (Object value : values) {
      if (isSupported(value.getClass())) {
        result.add(value.toString());
      }
      else {
        throw new UnsupportedOperationException("Cannot process types " + value.getClass());
      }
    }
    return result;
  }

  private boolean isSupported(Class<?> clazz) {
    for (Class<?> type : supportedObjectTypes) {
      if (type.isAssignableFrom(clazz)) {
        return true;
      }
    }
    return false;
  }
}
