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

import com.google.cloud.tools.app.Option.Type;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Contains common members and methods to all {@link Action} implementations.
 */
// TODO(joaomartins): Come up with a better name for this. Action too overloaded.
public abstract class Action {

  protected static Map<Option, String> NO_FLAGS = ImmutableMap.of();
  protected static String UNSET_STRING = null;
  protected static Boolean UNSET_BOOLEAN = null;
  protected static Collection<String> UNSET_COLLECTION = null;
  /**
   * Flags sent by the client.
   */
  protected Map<Option, String> flags;

  /**
   * Makes system calls to invoke processes.
   */
  protected ProcessCaller processCaller;

  public Action() {}

  public Action(Map<Option, String> flags) {
    this.flags = flags;
  }

  /**
   * Executes the logic implemented by this {@link Action}.
   *
   * <p>For most actions, this is simply calling gcloud for the command that was supplied in the
   * {@link Action} constructor.
   *
   * @return {@code true} if execution was successful, {@code false} if it was unsuccessful but no
   * exception was thrown.
   * @throws GCloudExecutionException if any error invoking or running gcloud occurred.
   */
  public boolean execute() throws GCloudExecutionException, IOException {
    return processCaller.call();
  }

  @VisibleForTesting
  public ProcessCaller getProcessCaller() {
    return processCaller;
  }

  /**
   * Checks if {@code flags} contains flags that are not accepted by the {@link Action} and if
   * any flag's value is invalid.
   *
   * @throws InvalidFlagException if client-provided flags contain flags not accepted by the
   * {@link Action}.
   */
  protected void checkFlags(Map<Option, String> clientProvidedFlags, Set<Option> acceptedFlags) {
    for (Entry<Option, String> clientFlag : clientProvidedFlags.entrySet()) {
      // Check for unrecognised flags.
      if (!acceptedFlags.contains(clientFlag.getKey())) {
        throw new InvalidFlagException(String.format(
            "The %s flag is not recognised by the %s command.",
            clientFlag.getKey().getLongForm(), this.getClass().getName()));
      }

      // Check for invalid values.
      String flagValue = clientFlag.getValue();
      if (clientFlag.getKey().getType().equals(Type.BOOLEAN)) {
        // If boolean, either there is no value, or the value has to be "true" or "false".
        if (!Strings.isNullOrEmpty(flagValue)) {
          String flagValueLowerCase = flagValue.toLowerCase(Locale.ENGLISH);
          if (!flagValueLowerCase.equals("true".toLowerCase(Locale.ENGLISH))
              && !flagValueLowerCase.equals("false".toLowerCase(Locale.ENGLISH))) {
            throw new InvalidFlagException(String.format(
                "The value %s for flag %s is invalid.",
                flagValue, clientFlag.getKey().getLongForm()));
          }
        }
      } else if (clientFlag.getKey().getType().equals(Type.INTEGER)) {
        // If integer, flag must have a value and be int parsable.
        if (!Strings.isNullOrEmpty(flagValue)) {
          try {
            Integer.parseInt(flagValue);
          } catch (NumberFormatException nfe) {
            throw new InvalidFlagException(String.format(
                "The value %s for flag %s is invalid.",
                flagValue, clientFlag.getKey().getLongForm()));
          }
        } else {
          throw new InvalidFlagException(
              "Flag " + clientFlag.getKey().getLongForm() + " must have a value.");
        }
      }
    }
  }

  /**
   * Sets a new Cloud SDK location, in case it isn't installed in the default user's home
   * folder.
   *
   * @throws InvalidDirectoryException If not directory is provided, or the provided directory
   * does not exist
   */
  public void setCloudSdkOverride(String cloudSdkOverride) throws InvalidDirectoryException {
    this.processCaller.setCloudSdkOverride(cloudSdkOverride);
  }

  @VisibleForTesting
  public void setProcessCaller(ProcessCaller processCaller) {
    this.processCaller = processCaller;
  }
}
