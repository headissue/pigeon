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
package com.headissue.pigeon.admin;

import com.google.inject.Inject;
import com.headissue.pigeon.PigeonMediaType;
import com.headissue.pigeon.service.JacksonService;
import com.headissue.pigeon.service.ResponseService;
import com.headissue.pigeon.admin.AdminSurveyHandler.SurveyParameter;
import com.headissue.pigeon.admin.json.SurveyValue;
import com.headissue.pigeon.util.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Path("/admin/survey")
public class AdminSurveyResource {

  private static final Log log = LogFactory.getLog(AdminSurveyResource.class);

  @Inject
  private ResponseService response;

  @Inject
  private AdminSurveyHandler surveyHandler;

  @GET
  public Response getAllSurveyAsJson(
    @Context UriInfo _uriInfo
  ) {
    String _path = _uriInfo.getPath();
    LogUtils.debug(log, "path: %s", _path);
    if (!_path.contains("admin")) {
      return response.bad();
    }
    try {
      List<SurveyOverviewValue> surveyList = surveyHandler.getAllSurveys();
      return response.ok(surveyList, _uriInfo);
    } catch (Exception e) {
      LogUtils.warn(log, e, "delivery of survey list is failed");
      return response.bad();
    }
  }

  @PUT
  @Consumes({PigeonMediaType.APPLICATION_JSON})
  @Produces({PigeonMediaType.APPLICATION_JSON, PigeonMediaType.APPLICATION_JAVASCRIPT})
  public Response receiveNewSurvey(
    @Context UriInfo _uriInfo,
    SurveyValue _survey
  ) {
    //LogUtils.debug(log, "survey json: \n%s\n\n", _surveyJson);
    String _path = _uriInfo.getPath();
    LogUtils.debug(log, "path: %s", _path);
    if (!_path.contains("admin")) {
      return response.bad();
    }
    try {
      // SurveyValue _survey = jacksonService.fromString(_surveyJson, SurveyValue.class);
      SurveyParameter p = new SurveyParameter();
      p.setTimestamp(System.currentTimeMillis());
      p.setSurvey(_survey);
      int _surveyId = surveyHandler.createSurvey(p);
      if (_surveyId > 0) {
        return response.ok(new SurveyResponse(_surveyId), _uriInfo);
      }
      return response.bad();
    } catch (Exception e) {
      LogUtils.warn(log, e, "create a new survey is failed");
      return response.bad();
    }
  }

  @POST
  @Path("/{surveyId}")
  @Consumes({PigeonMediaType.APPLICATION_JSON})
  @Produces({PigeonMediaType.APPLICATION_JSON, PigeonMediaType.APPLICATION_JAVASCRIPT})
  public Response receiveSurveyToOverride(
    @PathParam("surveyId") @DefaultValue("0") int _surveyId,
    @Context UriInfo _uriInfo,
    SurveyValue _survey
  ) {
    // LogUtils.debug(log, "survey json: \n%s\n\n", _surveyJson);
    if (_surveyId <= 0) {
      return receiveNewSurvey(_uriInfo, _survey);
    }
    try {
      // SurveyValue _survey = jacksonService.fromString(_surveyJson, SurveyValue.class);
      SurveyParameter p = new SurveyParameter();
      p.setTimestamp(System.currentTimeMillis());
      p.setSurvey(_survey);
      p.setSurveyId(_surveyId);
      _surveyId = surveyHandler.overwriteSurvey(p);
      if (_surveyId > 0) {
        return response.ok(new SurveyResponse(_surveyId), _uriInfo);
      }
      return response.bad();
    } catch (Exception e) {
      LogUtils.warn(log, e, "override an existed survey is failed");
      return response.bad();
    }
  }

  @GET
  @Path("/{surveyId}")
  @Produces({PigeonMediaType.APPLICATION_JSON, PigeonMediaType.APPLICATION_JAVASCRIPT})
  public Response getSurveyData(
    @Context UriInfo _uriInfo,
    @PathParam("surveyId") int _surveyId
  ) {
    LogUtils.debug(log, "get survey '%s'", _surveyId);
    SurveyValue _survey = surveyHandler.getSurvey(_surveyId);
    return response.ok(_survey, _uriInfo);
  }

  @DELETE
  @Path("/{surveyId}")
  @Produces({PigeonMediaType.APPLICATION_JSON, PigeonMediaType.APPLICATION_JAVASCRIPT})
  public Response deleteSurvey(
    @Context UriInfo _uriInfo,
    @PathParam("surveyId") int _surveyId
  ) {
    if (!surveyHandler.disableSurvey(_surveyId)) {
      return response.bad();
    }
    return response.ok(new SurveyResponse(_surveyId), _uriInfo);
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class SurveyResponse {

    @XmlElement(name = "id")
    int surveyId;

    public SurveyResponse(int surveyId) {
      this.surveyId = surveyId;
    }

    public int getSurveyId() {
      return surveyId;
    }

    public void setSurveyId(int surveyId) {
      this.surveyId = surveyId;
    }
  }
}
