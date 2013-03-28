/*
 * Copyright (C) 2013 headissue GmbH (www.headissue.com)
 *
 * Source repository: https://github.com/headissue/pigeon
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This patch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this patch.  If not, see <http://www.gnu.org/licenses/agpl.txt/>.
 */
package com.headissue.pigeon.util;

import com.headissue.pigeon.PigeonException;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class ClassUtils {


  public static <T> T cast(Object o, Class<T> type) {
    try {
      return type.cast(o);
    } catch (Exception e) {
      throw new PigeonException("cast is failed", e);
    }
  }

  public static Class<?> forName(String className) {
    try {
      return Class.forName(className);
    } catch (Exception e) {
      throw new PigeonException("missing class '" + className + "'", e);
    }
  }

  public static <T> T newInstance(String className, Class<T> superType) {
    Class<?> type = forName(className);
    try {
      Object o = type.newInstance();
      return cast(o, superType);
    } catch (Exception e) {
      throw new PigeonException("can not create an instance of '" + className + "'");
    }
  }

  public static <T> T newInstance(Class<T> type) {
    try {
      return type.newInstance();
     } catch (Exception e) {
      throw new PigeonException("can not create an instance of '" +
        (type != null ? type.getSimpleName() : "null") + "'");
    }
  }

  private ClassUtils() { }
}
