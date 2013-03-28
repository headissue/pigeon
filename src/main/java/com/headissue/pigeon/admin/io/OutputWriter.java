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

import com.headissue.pigeon.admin.AdminAnswer;

import java.io.IOException;
import java.io.Writer;

public abstract class OutputWriter {

  private boolean headWrite = false;

  protected final Writer writer;

  protected OutputWriter(Writer _writer) {
    this.writer = _writer;
  }

  public void write(AdminAnswer _answer) throws IOException {
    if (!headWrite) {
      writeHeader(_answer);
      headWrite = true;
    }
    writeBody(_answer);
  }

  protected abstract void writeHeader(AdminAnswer _answer) throws IOException;

  protected abstract void writeBody(AdminAnswer _answer) throws IOException;

  public void close() throws IOException {
    writer.close();
  }
}
