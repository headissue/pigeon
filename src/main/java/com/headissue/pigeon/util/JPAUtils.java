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
import org.apache.commons.logging.LogFactory;

import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class JPAUtils {

  private static final Log log = LogFactory.getLog(JPAUtils.class);

  /**
   * Returns a single entity. Note: if the entity does not exist then return null
   */
  public static <T> T getSingleResult(TypedQuery<T> q) {
    try {
      return q.getSingleResult();
    } catch (Exception e) {
      LogUtils.trace(log, e, "access to a single entity is failed");
      return null;
    }
  }

  /**
   * Returns a list of entities. Note: it returns always a list, never null.
   */
 public static <T> List<T> getResultList(TypedQuery<T> q) {
   try {
     return q.getResultList();
   } catch (Exception e) {
     LogUtils.trace(log, e, "access to a list of entities is failed");
     return Collections.emptyList();
   }
 }
}
