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
package com.headissue.pigeon;

import com.headissue.pigeon.service.Session;
import com.headissue.pigeon.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class SessionManager {

  private Session currentSession = null;

  public Session getCurrentSession() {
    if (currentSession == null) {
      newSession();
    }
    return currentSession;
  }

  public void newSession() {
    currentSession = new TestSession();
  }


  static class TestSession extends Session {

    private Map<String, Object> attributes = new HashMap<String, Object>();

    @Override
    public void setAttribute(String _name, Object _value) {
      attributes.put(_name, _value);
    }

    @Override
    public <T> T getAttribute(String _name, Class<T> type) {
      Object o = attributes.get(_name);
      if (o == null) {
        return null;
      }
      return ClassUtils.cast(o, type);
    }

    @Override
    public void removeAttribute(String _name) {
      attributes.remove(_name);
    }
  }
}
