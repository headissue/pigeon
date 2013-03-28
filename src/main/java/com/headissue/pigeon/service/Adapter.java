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

import com.headissue.pigeon.survey.QuestionType;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Adapter {

  private static final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


  public static class DateTimeXmlAdapter extends XmlAdapter<String, Date> {

    @Override
    public Date unmarshal(String v) throws Exception {
      return dateTimeFormat.parse(v);
    }

    @Override
    public String marshal(Date v) throws Exception {
      return dateTimeFormat.format(v);
    }
  }


  public static class QuestionTypeAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String v) throws Exception {
      return (v == null) ? "" : v.toLowerCase();
    }

    @Override
    public String marshal(String v) throws Exception {
      return v == null ? null : v.toLowerCase();
    }
  }


  private Adapter() { }
}
