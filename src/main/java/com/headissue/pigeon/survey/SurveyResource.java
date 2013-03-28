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
package com.headissue.pigeon.survey;


import com.google.inject.Inject;
import com.headissue.pigeon.PigeonMediaType;
import com.headissue.pigeon.service.ResponseService;
import com.headissue.pigeon.util.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Searches and creates a survey by id and - if found - sends it to the client.
 */
@Path("/survey")
public class SurveyResource {

  private static final Log log = LogFactory.getLog(SurveyResource.class);

  @Inject
  private ResponseService response;

  @Inject
  private SurveyHandler surveyHandler;

  @Path("/{surveyId}")
  @GET
  public Response getSurvey(
    @Context UriInfo _uriInfo,
    @PathParam("surveyId") @DefaultValue("0") int _surveyId) {
    if (_surveyId <= 0) {
      return response.bad();
    }
    try {
      Survey _survey = surveyHandler.findSurveyById(_surveyId);
      if (_survey == null) {
        response.bad();
      }
      return response.ok(_survey, _uriInfo);
    } catch (Exception e) {
      LogUtils.warn(log, e, "delivery of survey '%s' failed", _surveyId);
      return response.bad();
    }
  }
}
