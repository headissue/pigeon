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

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 *
 */
public class DateTimeServiceTest {

  private final DateTimeService service = new DateTimeService();

  private final Calendar calendar = new GregorianCalendar();

  private Date timestamp;

  @Before
  public void setUp() {
    calendar.set(2012, Calendar.AUGUST, 23, 12, 4, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    timestamp = calendar.getTime();
  }

  @Test
  public void testDateToText() {
    assertEquals("2012-08-23-12-04", service.format(timestamp));
  }

  @Test
  public void testTextToDate() {
    assertEquals(timestamp.getTime(), service.parse("2012-08-23-12-04").getTime());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTooShortText() {
    service.parse("2012-08-23");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongFormat() {
    service.parse("2012-08-23 23:04");
  }
}
