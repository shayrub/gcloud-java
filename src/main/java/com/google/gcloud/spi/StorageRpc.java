/* * Copyright 2015 Google Inc. All Rights Reserved. * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * *       http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */package com.google.gcloud.spi;import com.google.api.services.storage.model.Bucket;import com.google.api.services.storage.model.StorageObject;import com.google.gcloud.storage.BlobReadChannel;import com.google.gcloud.storage.BlobWriteChannel;import com.google.gcloud.storage.Option;import com.google.gcloud.storage.StorageServiceException;import java.util.Iterator;import java.util.List;public interface StorageRpc {  Bucket create(Bucket bucket, Option... options) throws StorageServiceException;  StorageObject create(StorageObject object, byte[] content, Option... options)      throws StorageServiceException;  Iterator<Bucket> list() throws StorageServiceException;  Iterator<StorageObject> list(String bucket, String prefix, String delimiter, String cursor,      boolean versions, int limit) throws StorageServiceException;  Bucket get(String bucket, Option... options) throws StorageServiceException;  StorageObject get(String bucket, String object, Option... options) throws StorageServiceException;  Bucket patch(Bucket bucket, Option... options) throws StorageServiceException;  StorageObject patch(StorageObject storageObject, Option... options)      throws StorageServiceException;  void delete(Bucket bucket, Option... options) throws  StorageServiceException;  void delete(StorageObject object, Option... options) throws StorageServiceException;  StorageObject compose(Iterable<StorageObject> src, StorageObject destination,      List<? extends Option> destinationOptions) throws StorageServiceException;  StorageObject copy(StorageObject from, List<? extends Option> blobSourceOptions,      StorageObject to, List<? extends Option> blobTargetOptions) throws StorageServiceException;  BlobReadChannel reader(StorageObject from, Option... options) throws StorageServiceException;  BlobWriteChannel writer(StorageObject to, Option... options) throws StorageServiceException;}