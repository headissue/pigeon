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

import com.headissue.pigeon.util.ClassUtils;

import javax.servlet.http.HttpSession;

public final class WebSession extends Session {

  private static final String KEY = WebSession.class.getName();

  private final HttpSession session;

  private  WebSession(HttpSession session) {
    this.session = session;
  }

  @Override
  public void setAttribute(String _name, Object _value) {
     session.setAttribute(_name, _value);
  }

  @Override
  public <T> T getAttribute(String _name, Class<T> type) {
    Object o = session.getAttribute(_name);
    return (o == null) ? null : ClassUtils.cast(o, type);
  }

  @Override
  public void removeAttribute(String _name) {
    session.removeAttribute(_name);
  }

  public static Session session(HttpSession _session) {
    Object o = _session.getAttribute(KEY);
    if (o instanceof Session) {
      return ClassUtils.cast(o, Session.class);
    }
    WebSession s = new WebSession(_session);
    _session.setAttribute(KEY, s);
    return s;
  }
}
