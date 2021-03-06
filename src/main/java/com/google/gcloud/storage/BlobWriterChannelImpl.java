/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gcloud.storage;

import static com.google.gcloud.RetryHelper.runWithRetries;
import static java.util.concurrent.Executors.callable;

import com.google.api.services.storage.model.StorageObject;
import com.google.gcloud.spi.StorageRpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

/**
 * Default implementation for BlobWriteChannel.
 */
class BlobWriterChannelImpl implements BlobWriteChannel {

  private static final long serialVersionUID = 8675286882724938737L;
  private static final int CHUNK_SIZE = 256 * 1024;
  private static final int MIN_BUFFER_SIZE = 8 * CHUNK_SIZE;

  private final StorageServiceOptions options;
  private final Blob blob;
  private final String uploadId;
  private int position;
  private byte[] buffer = new byte[0];
  private int limit;
  private boolean isOpen = true;

  private transient StorageRpc storageRpc;
  private transient StorageObject storageObject;

  public BlobWriterChannelImpl(StorageServiceOptions options, Blob blob,
      Map<StorageRpc.Option, ?> optionsMap) {
    this.options = options;
    this.blob = blob;
    initTransients();
    uploadId = options.storageRpc().open(storageObject, optionsMap);
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    if (isOpen) {
      flush(true);
    }
    out.defaultWriteObject();
  }

  private void flush(boolean compact) {
    if (limit >= MIN_BUFFER_SIZE || compact && limit >= CHUNK_SIZE) {
      final int length = limit - limit % CHUNK_SIZE;
      runWithRetries(callable(new Runnable() {
        @Override
        public void run() {
          storageRpc.write(uploadId, buffer, 0, storageObject, position, length, false);
        }
      }), options.retryParams(), StorageServiceImpl.EXCEPTION_HANDLER);
      position += length;
      limit -= length;
      byte[] temp = new byte[compact ? limit : MIN_BUFFER_SIZE];
      System.arraycopy(buffer, length, temp, 0, limit);
      buffer = temp;
    }
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    if (isOpen) {
      initTransients();
    }
  }

  private void initTransients() {
    storageRpc = options.storageRpc();
    storageObject = blob.toPb();
  }

  private void validateOpen() throws IOException {
    if (!isOpen) {
      throw new IOException("stream is closed");
    }
  }

  @Override
  public int write(ByteBuffer byteBuffer) throws IOException {
    validateOpen();
    int toWrite = byteBuffer.remaining();
    int spaceInBuffer = buffer.length - limit;
    if (spaceInBuffer >= toWrite) {
      byteBuffer.get(buffer, limit, toWrite);
    } else {
      buffer = Arrays.copyOf(buffer,
          Math.max(MIN_BUFFER_SIZE, buffer.length + toWrite - spaceInBuffer));
      byteBuffer.get(buffer, limit, toWrite);
    }
    limit += toWrite;
    flush(false);
    return toWrite;
  }

  @Override
  public boolean isOpen() {
    return isOpen;
  }

  @Override
  public void close() throws IOException {
    if (isOpen) {
      runWithRetries(callable(new Runnable() {
        @Override
        public void run() {
          storageRpc.write(uploadId, buffer, 0, storageObject, position, limit, true);
        }
      }), options.retryParams(), StorageServiceImpl.EXCEPTION_HANDLER);
      position += buffer.length;
      isOpen = false;
      buffer = null;
    }
  }
}
