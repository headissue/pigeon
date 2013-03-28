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

import com.google.inject.Injector;
import com.headissue.pigeon.util.LogUtils;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

/**
 *
 */
@Singleton
public class PigeonContainer extends GuiceContainer {

  private static final Log log = LogFactory.getLog(PigeonContainer.class);


  /**
   * Creates a new Injector.
   *
   * @param injector the Guice injector
   */
  @Inject
  public PigeonContainer(Injector injector) {
    super(injector);
  }

  @Override
  public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
    long dtStart = System.currentTimeMillis();
    HttpServletRequest httpReq = (HttpServletRequest) req;
    if (log.isTraceEnabled()) {
      LogUtils.trace(log, "%s '%s'", httpReq.getMethod(), httpReq.getRequestURI());
      Enumeration<String> en = httpReq.getHeaderNames();
      while (en.hasMoreElements()) {
        String name = en.nextElement();
        String value = httpReq.getHeader(name);
        LogUtils.trace(log, "request header '%s=%s'", name, value);
      }
      LogUtils.trace(log, "locale=%s", req.getLocale());
    }
    super.service(req, res);
    // how long?
    LogUtils.debug(log, "%s '%s' in msecs %s", httpReq.getMethod(), httpReq.getRequestURI(),
      System.currentTimeMillis() - dtStart);
  }
}
