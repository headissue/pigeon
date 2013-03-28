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
package com.headissue.pigeon.service;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateTimeService {

  private DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd-HH-mm");

  public DateTimeService() {
  }

  public String format(long _timestamp) {
    return dateTimeFormatter.print(_timestamp);
  }

  public String format(Date _date) {
    return dateTimeFormatter.print(_date.getTime());
  }

  public Date parse(String _text) {
    return dateTimeFormatter.parseDateTime(_text).toDate();
  }
}
