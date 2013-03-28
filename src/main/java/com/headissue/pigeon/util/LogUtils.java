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

import org.apache.commons.logging.Log;

/**
 *
 */
public final class LogUtils {

  public static void warn(Log log, String message, Object... args) {
    if (log.isWarnEnabled()) {
      if (args != null && args.length > 0) {
        message = String.format(message, args);
      }
      log.warn(message);
    }
  }

  public static void warn(Log log, Throwable t, String message, Object... args) {
    if (log.isWarnEnabled()) {
      if (args != null && args.length > 0) {
        message = String.format(message, args);
      }
      log.warn(message, t);
    }
  }

  public static void info(Log log, String message, Object... args) {
    if (log.isInfoEnabled()) {
      if (args != null && args.length > 0) {
        message = String.format(message, args);
      }
      log.info(message);
    }
  }

  public static void info(Log log, Throwable t, String message, Object... args) {
    if (log.isInfoEnabled()) {
      if (args != null && args.length > 0) {
        message = String.format(message, args);
      }
      log.info(message, t);
    }
  }

  public static void debug(Log log, String message, Object... args) {
    if (log.isDebugEnabled()) {
      if (args != null && args.length > 0) {
        message = String.format(message, args);
      }
      log.debug(message);
    }
  }

  public static void debug(Log log, Throwable t, String message, Object... args) {
    if (log.isDebugEnabled()) {
      if (args != null && args.length > 0) {
        message = String.format(message, args);
      }
      log.debug(message, t);
    }
  }

  public static void trace(Log log, String message, Object... args) {
    if (log.isTraceEnabled()) {
      if (args != null && args.length > 0) {
        message = String.format(message, args);
      }
      log.trace(message);
    }
  }

  public static void trace(Log log, Throwable t, String message, Object... args) {
    if (log.isTraceEnabled()) {
      if (args != null && args.length > 0) {
        message = String.format(message, args);
      }
      log.trace(message, t);
    }
  }


  private LogUtils() { }
}
