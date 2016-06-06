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

package com.google.cloud.tools.app.impl.cloudsdk.internal.util;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;

/**
 * Internal file utilities.
 */
public class FileUtil {

  /**
   * We really want to preserve file attributes and permissions on linux and windows.
   *
   * @param source an existing source directory to copy from
   * @param destination an existing destination directory to copy to
   */
  public static void copyDirectory(final Path source, final Path destination) throws IOException {
    Preconditions.checkNotNull(source);
    Preconditions.checkNotNull(destination);
    Preconditions.checkArgument(Files.isDirectory(source));
    Preconditions.checkArgument(Files.isDirectory(destination));
    Preconditions.checkArgument(!source.equals(destination));

    Files.walkFileTree(source, new FileVisitor<Path>() {
      final CopyOption[] copyOptions = new CopyOption[] { StandardCopyOption.COPY_ATTRIBUTES };

      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
          throws IOException {

        if (dir.equals(source)) {
          return FileVisitResult.CONTINUE;
        }

        copyWithAttributesAndPermissions(dir, destination.resolve(source.relativize(dir)));
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        copyWithAttributesAndPermissions(file, destination.resolve(source.relativize(file)));
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        throw exc;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (exc != null) {
          throw exc;
        }
        return FileVisitResult.CONTINUE;
      }

      private void copyWithAttributesAndPermissions(Path src, Path dest) throws IOException {
        Path target = Files.copy(src, dest, copyOptions);

        // windows acl attributes
        AclFileAttributeView srcAcl = Files.getFileAttributeView(src, AclFileAttributeView.class);
        if (srcAcl != null) {
          Files.getFileAttributeView(target, AclFileAttributeView.class).setAcl(srcAcl.getAcl());
        }

        // posix attributes
        PosixFileAttributeView srcPosix = Files
            .getFileAttributeView(src, PosixFileAttributeView.class);
        if (srcPosix != null) {
          PosixFileAttributes srcPosixAttr = srcPosix.readAttributes();
          PosixFileAttributeView destPosix = Files
              .getFileAttributeView(target, PosixFileAttributeView.class);
          destPosix.setPermissions(srcPosixAttr.permissions());
          destPosix.setGroup(srcPosixAttr.group());
        }
      }
    });

  }

}
