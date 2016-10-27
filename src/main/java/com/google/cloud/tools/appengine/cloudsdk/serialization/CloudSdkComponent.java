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

package com.google.cloud.tools.appengine.cloudsdk.serialization;

import com.google.gson.annotations.SerializedName;

/**
 * Representation of a CloudSdkComponent. Used for json serialzation/deserialization.
 */
public class CloudSdkComponent {

  private String id;
  private String name;
  @SerializedName("current_version_string")
  private String currentVersionString;
  @SerializedName("latest_version_string")
  private String latestVersionString;
  @SerializedName("size")
  private Integer sizeInBytes;
  private State state;
  @SerializedName("is_configuration")
  private Boolean isConfiguration;
  @SerializedName("is_hidden")
  private Boolean isHidden;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCurrentVersionString() {
    return currentVersionString;
  }

  public void setCurrentVersionString(String currentVersionString) {
    this.currentVersionString = currentVersionString;
  }

  public String getLatestVersionString() {
    return latestVersionString;
  }

  public void setLatestVersionString(String latestVersionString) {
    this.latestVersionString = latestVersionString;
  }

  public Integer getSizeInBytes() {
    return sizeInBytes;
  }

  public void setSizeInBytes(Integer sizeInBytes) {
    this.sizeInBytes = sizeInBytes;
  }

  public Boolean getIsConfiguration() {
    return isConfiguration;
  }

  public void setIsConfiguration(Boolean isConfiguration) {
    this.isConfiguration = isConfiguration;
  }

  public Boolean getIsHidden() {
    return isHidden;
  }

  public void setIsHidden(Boolean isHidden) {
    this.isHidden = isHidden;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  static class State {
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

  }
}
