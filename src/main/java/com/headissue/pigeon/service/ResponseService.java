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

import com.google.inject.Inject;
import com.headissue.pigeon.PigeonMediaType;
import com.headissue.pigeon.util.UriInfoUtils;
import com.sun.jersey.api.json.JSONWithPadding;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

/**
 * Prepares the response to be send as JSON or JSONP.
 */
public class ResponseService {

  @Inject
  private CallbackNameVerifier nameVerifier;

  /**
   * After verification, the "callback" parameter decides whether to respond with JSON or JSONP.
   */
  public Response ok(Object _value, UriInfo _uriInfo) {
    if (_value == null) {
      return bad();
    }
    String _callbackName = UriInfoUtils.getParameter(_uriInfo, "callback");
    if (!nameVerifier.verifyName(_callbackName)) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (StringUtils.isEmpty(_callbackName)) {
      return Response.ok(_value, PigeonMediaType.APPLICATION_JSON).build();
    }
    return Response.ok(new JSONWithPadding(_value, _callbackName),
      PigeonMediaType.APPLICATION_JAVASCRIPT).build();
  }

  public Response ok(String _content, String _mediaType) {
    return Response.ok(_content, _mediaType).build();
  }

  /**
   * Sends the content to the client and sets the filename in the header.
   * <a href="http://stackoverflow.com/q/9790817/1453095">http://stackoverflow.com/q/9790817/1453095</a>
   */
  public Response ok(Object _content, String _mediaType, String _filename) {
    String _headerValue = String.format("attachment;filename=%s;charset=UTF-8", _filename);
    return Response.ok(_content, _mediaType)
      .header("content-disposition", _headerValue).build();
  }

  public Response bad() {
    return Response.status(Status.BAD_REQUEST).build();
  }
}
