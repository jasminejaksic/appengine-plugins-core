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

import com.google.cloud.tools.Option.Type;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Contains common members and methods to all {@link Action} implementations.
 */
// TODO(joaomartins): Come up with a better name for this. Action too overloaded.
public abstract class Action {

  /**
   * Flags sent by the client.
   */
  protected Map<Option, String> flags;

  /**
   * Flags accepted by an action.
   */
  protected Option[] acceptedFlags;
  /**
   * Makes system calls to invoke processes.
   */
  protected ProcessCaller processCaller;

  /**
   * Executes the logic implemented by this {@link Action}.
   *
   * <p>For most actions, this is simply calling gcloud for the command that was built in the
   * {@link Action} constructor.
   *
   * @return {@code true} if execution was successful, {@code false} if it was unsuccessful but no
   * exception was thrown.
   * @throws GCloudErrorException if any error invoking or running gcloud occurred.
   */
  public boolean execute() throws GCloudErrorException {
    return processCaller.call();
  }

  /**
   * Flags accepted by an {@link Action}.
   *
   * <p>Every {@link Action} implementation has an {@code acceptedFlags} static array with its
   * accepted flags. This is a utility method that transforms that array into an iterable
   * {@link Set}.
   */
  @VisibleForTesting
  public Set<Option> getAcceptedFlags() {
    return new HashSet<>(Arrays.asList(acceptedFlags));
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
  protected void checkFlags(Map<Option, String> clientProvidedFlags) {
    Set<Option> acceptedFlags = getAcceptedFlags();

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
          if (!flagValue.toLowerCase().equals("true") && !flagValue.toLowerCase().equals("false")) {
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
          throw new InvalidFlagException(String.format(
              "Flag %s must have a value.", clientFlag.getKey().getLongForm()));
        }
      }
    }
  }

  /**
   * Sets a new Cloud SDK location, in case the is isn't installed in the default user's home
   * folder.
   *
   * @throws InvalidDirectoryException If not directory is provided, or the provided directory
   * does not exist.
   */
  public void setCloudSdkOverride(String cloudSdkOverride) throws InvalidDirectoryException {
    this.processCaller.setCloudSdkOverride(cloudSdkOverride);
  }

  /**
   * To be used in unit tests.
   */
  @VisibleForTesting
  public void setProcessCaller(ProcessCaller processCaller) {
    this.processCaller = processCaller;
  }
}
