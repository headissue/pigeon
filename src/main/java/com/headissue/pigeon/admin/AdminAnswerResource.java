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
import com.headissue.pigeon.service.DateTimeService;
import com.headissue.pigeon.service.ResponseService;
import com.headissue.pigeon.survey.Survey;
import com.headissue.pigeon.admin.io.OutputWriter;
import com.headissue.pigeon.util.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.StringWriter;
import java.util.List;

@Path("/admin/survey/{surveyId}/export")
public class AdminAnswerResource {

  private static final Log log = LogFactory.getLog(AdminAnswerResource.class);

  @Inject
  private AdminAnswerHandler adminAnswerHandler;

  @Inject
  private AdminOutputFactory outputFactory;

  @Inject
  private ResponseService response;

  @Inject
  private DateTimeService dateTimeService;

  @GET
  @Path("/csv")
  public Response sendSurveyReportAsCSV(
    @PathParam("surveyId") @DefaultValue("0") int _surveyId,
    @Context UriInfo _uriInfo
  ) {
    String _path = _uriInfo.getAbsolutePath().toString();
    LogUtils.debug(log, "path: '%s'", _path);
    if (_surveyId <= 0 || !_path.contains("/admin")) {
      return response.bad();
    }
    try {
      List<AdminAnswer> _answerList = adminAnswerHandler.getAnswerListFromSurvey(_surveyId);
      if (_answerList.isEmpty()) {
        return Response.noContent().build();
      }
      String _filename = createDownloadFilenameFromAnswer(_answerList.get(0), ".csv");
      StringWriter _buffer = new StringWriter();
      OutputWriter _writer = outputFactory.getWriter("csv", _buffer);
      for (AdminAnswer _answer : _answerList) {
        _writer.write(_answer);
      }
      _writer.close();
      return response.ok(_buffer.toString(), PigeonMediaType.TEXT_CSV, _filename);
    } catch (Exception e) {
      LogUtils.warn(log, e, "survey '%s' reporting as csv failed", _surveyId);
    }
    return response.bad();
  }

  String createDownloadFilenameFromAnswer(AdminAnswer _answer, String _fileExtension) {
    String _filename = dateTimeService.format(System.currentTimeMillis());
    if (_answer == null) {
      return _filename + _fileExtension;
    }
    Survey _survey = _answer.getSurvey();
    return _survey.getName() + "-" + _filename + _fileExtension;
  }
}
