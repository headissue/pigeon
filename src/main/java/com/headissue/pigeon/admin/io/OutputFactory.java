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
package com.headissue.pigeon.admin.io;

import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class OutputFactory {

  private final Map<String, Class<? extends OutputWriter>> format2Writer =
    new HashMap<String, Class<? extends OutputWriter>>();

  public void addWriter(String _format, Class<? extends OutputWriter> _writerType) {
    format2Writer.put(_format, _writerType);
  }

  public OutputWriter getWriter(String _format, Writer _writer) {
    if (!format2Writer.containsKey(_format)) {
      throw new RuntimeException("Unknown format '" + _format + "'");
    }
    Class<? extends OutputWriter> type = format2Writer.get(_format);
    try {
      Constructor<? extends OutputWriter> con = type.getConstructor(Writer.class);
      return con.newInstance(_writer);
    } catch (Exception e) {
      throw new RuntimeException("", e);
    }
  }
}
