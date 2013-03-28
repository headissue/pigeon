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

import com.google.inject.Singleton;
import com.headissue.pigeon.util.ClassUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;

@Singleton
public class BeanService {

  private BeanUtilsBean bub = new BeanUtilsBean();
  private PropertyUtilsBean pub = bub.getPropertyUtils();

  public <T> T copy(Class<T> type, Object _source, String... _properties) {
     T _bean = ClassUtils.newInstance(type);
    return copy(_bean, _source, _properties);
  }

  public <T> T copy(T _bean, Object _source, String... _properties) {
    if (_properties == null || _properties.length == 0) {
      try {
        pub.copyProperties(_source, _bean);
        return _bean;
      } catch (Exception e) {
        throw new BeanException("merge complete bean failed", e);
      }
    }
    for (String _name : _properties) {
      Object _value = getPropertyValue(_source, _name);
      copyPropertyValue(_bean, _name, _value);
    }
    return _bean;
  }

  public Object getPropertyValue(Object _source, String _propertyName) {
    try {
      return pub.getProperty(_source, _propertyName);
    } catch (Exception e) {
      throw new BeanException("get property '" + _propertyName + "' failed", e);
    }
  }

  public void copyPropertyValue(Object _dest, String _propertyName, Object _value) {
    try {
      bub.copyProperty(_dest, _propertyName, _value);
    } catch (Exception e) {
      throw new BeanException("merge property '" + _propertyName + "' failed", e);
    }
  }
}
