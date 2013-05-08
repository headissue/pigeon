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
package com.headissue.pigeon.survey.answer;

import com.google.inject.Inject;
import com.headissue.pigeon.PigeonMediaType;
import com.headissue.pigeon.service.ResponseService;
import com.headissue.pigeon.service.Session;
import com.headissue.pigeon.service.UserMapService;
import com.headissue.pigeon.service.WebSession;
import com.headissue.pigeon.survey.Survey;
import com.headissue.pigeon.survey.SurveyHandler;
import com.headissue.pigeon.survey.answer.AnswerHandler.AnswerParameter;
import com.headissue.pigeon.util.LogUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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

@Path("/survey/{surveyId}/answers")
public class AnswerResource {


  private static final Log log = LogFactory.getLog(AnswerResource.class);

  @Inject
  private ResponseService response;

  @Inject
  private AnswerHandler answerHandler;

  @Inject
  private SurveyHandler surveyHandler;

  @Inject
  private UserMapService userMapService;

  /**
   * first answer received
   * responds with an userMapId to assign the following answers to this set
   */
  @PUT
  @Consumes({PigeonMediaType.APPLICATION_JSON})
  @Produces({PigeonMediaType.APPLICATION_JSON, PigeonMediaType.APPLICATION_JAVASCRIPT})
  public Response receiveFirstAnswer(
    @Context UriInfo _uriInfo,
    @Context HttpServletRequest request,
    @PathParam("surveyId") String _surveyValue,
    UserAnswerValues _answer)
  {
    if (StringUtils.isEmpty(_surveyValue)) {
      return response.bad();
    }
    HttpSession _webSession = request.getSession(true);
    Session _session = WebSession.session(_webSession);
    try {
      Survey _survey;
      if (NumberUtils.isNumber(_surveyValue)) {
        _survey = surveyHandler.findSurveyById(NumberUtils.toInt(_surveyValue));
      } else {
        _survey = surveyHandler.findSurveyByKey(_surveyValue);
      }
      if (_survey == null) {
        response.bad();
      }
      AnswerParameter p = new AnswerParameter();
      p.setSurveyId(_survey.getId());
      p.setMapId(0);
      p.setValues(_answer);
      p.setTimestamp(System.currentTimeMillis());
      answerHandler.receiveAnswer(_session, p);
      return response.ok(new AnswerResponse(userMapService.getUserMapId(_session)), _uriInfo);
    } catch (Exception e) {
      LogUtils.warn(log, e, "answer request failed (survey '%s')", _surveyValue);
      return response.bad();
    }
  }

  /**
   * further answers
   */
  @POST
  @Path("/{mapId}")
  @Consumes({PigeonMediaType.APPLICATION_JSON})
  @Produces({PigeonMediaType.APPLICATION_JSON, PigeonMediaType.APPLICATION_JAVASCRIPT})
  public Response receiveOtherAnswer(
    @Context UriInfo _uriInfo,
    @Context HttpServletRequest request,
    @PathParam("surveyId") String _surveyValue,
    @PathParam("mapId") @DefaultValue("0") int _mapId,
    UserAnswerValues _answer)
  {
    if (StringUtils.isEmpty(_surveyValue)) {
      return response.bad();
    }
    HttpSession _webSession = request.getSession(true);
    Session _session = WebSession.session(_webSession);
    if (_mapId <= 0 || userMapService.getUserMapId(_session) <= 0) {
      return response.bad();
    }
    _mapId = userMapService.getUserMapId(_session);
    try {
      Survey _survey;
      if (NumberUtils.isNumber(_surveyValue)) {
        _survey = surveyHandler.findSurveyById(NumberUtils.toInt(_surveyValue));
      } else {
        _survey = surveyHandler.findSurveyByKey(_surveyValue);
      }
      if (_survey == null) {
        response.bad();
      }
      AnswerParameter p = new AnswerParameter();
      p.setSurveyId(_survey.getId());
      p.setMapId(_mapId);
      p.setValues(_answer);
      p.setTimestamp(System.currentTimeMillis());
      answerHandler.receiveAnswer(_session, p);
      return response.ok(new AnswerResponse(userMapService.getUserMapId(_session)), _uriInfo);
    } catch (Exception e) {
      LogUtils.warn(log, e, "bad answer for survey '%s'", _surveyValue);
      return response.bad();
    }
  }


  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class AnswerResponse {

    @XmlElement(name = "id")
    private int mapId;

    public AnswerResponse() {
    }

    public AnswerResponse(int mapId) {
      this.mapId = mapId;
    }

    public int getMapId() {
      return mapId;
    }

    public void setMapId(int mapId) {
      this.mapId = mapId;
    }
  }
}
