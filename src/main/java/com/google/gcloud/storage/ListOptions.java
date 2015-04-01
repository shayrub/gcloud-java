/* * Copyright 2015 Google Inc. All Rights Reserved. * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * *       http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */package com.google.gcloud.storage;import java.io.Serializable;public class ListOptions implements Serializable {  private final boolean recursive;  private final String prefix;  private final String cursor;  private final Integer maxResults;  public static class Builder {    private boolean recursive;    private String prefix;    private String cursor;    private int maxResults;    public Builder() {    }    public Builder recursive(boolean recursive) {      this.recursive = recursive;      return this;    }    public Builder prefix(String prefix) {      this.prefix = prefix;      return this;    }    public Builder cursor(String cursor) {      this.cursor = cursor;      return this;    }    public Builder maxResults(Integer maxResults) {      this.maxResults = maxResults;      return this;    }  }  private ListOptions(Builder builder) {    recursive = builder.recursive;    prefix = builder.prefix;    cursor = builder.cursor;    maxResults = builder.maxResults;  }  public boolean recursive() {    return recursive;  }  public String prefix() {    return prefix;  }  public String cursor() {    return cursor;  }  public Integer maxResults() {    return maxResults;  }}