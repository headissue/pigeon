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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.headissue.pigeon.util.ClassUtils;
import com.headissue.pigeon.util.LogUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * Creates the Guice Injector. The parameter "com.headissue.pigeon.guice.Module" stores
 * the module for the injector to load.
 *
 * @see PigeonModule
 */
public class PigeonBootstrapListener extends GuiceServletContextListener {

  private static final Log log = LogFactory.getLog(PigeonBootstrapListener.class);


  static final String INIT_PARAM_GUICE_MODULE = "com.headissue.pigeon.guice.Module";

  private ServletContext context;

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    LogUtils.debug(log, "Pigeon listener is starting");
    context = servletContextEvent.getServletContext();
    super.contextInitialized(servletContextEvent);
  }

  @Override
  protected Injector getInjector() {
    try {
      return Guice.createInjector(findAndCreateModule(this.context));
    } catch (Exception e) {
      LogUtils.warn(log, e, "create injector failed");
      if (e instanceof PigeonException) {
        throw (PigeonException) e;
      }
      throw new PigeonException("create injector failed", e);
    }
  }


  /**
   * try finding the classname of the Guice module.
   */
  static Module findAndCreateModule(ServletContext context) {
    String className = getParam(context, INIT_PARAM_GUICE_MODULE);
    if (StringUtils.isEmpty(className)) {
      throw new PigeonException("missing  parameter '" + INIT_PARAM_GUICE_MODULE + "'");
    }
    return ClassUtils.newInstance(className, Module.class);
  }

  static String getParam(ServletContext _context, String _name) {
    String s = _context.getInitParameter(_name);
    if (StringUtils.isNotEmpty(s)) {
      LogUtils.debug(log, "found in servlet:   '%s'=%s", _name, s);
      return s;
    }
    String _envName = _name.replaceAll("\\.", "_");
    s = System.getenv(_envName);
    if (StringUtils.isNotEmpty(s)) {
      LogUtils.debug(log, "found in env param: '%s'=%s", _envName, s);
      return s;
    }
    s = System.getProperty(_name);
    if (StringUtils.isNotEmpty(s)) {
      LogUtils.debug(log, "found in sys prop:  '%s'=%s", _name, s);
    }
    return s;
  }
}
