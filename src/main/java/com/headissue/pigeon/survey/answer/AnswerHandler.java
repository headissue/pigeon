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
import com.google.inject.Singleton;
import com.headissue.pigeon.service.AnswerTransform;
import com.headissue.pigeon.service.AnswerTransformFactory;
import com.headissue.pigeon.service.Session;
import com.headissue.pigeon.service.UserMapService;
import com.headissue.pigeon.survey.Question;
import com.headissue.pigeon.survey.Survey;
import com.headissue.pigeon.util.JPAUtils;
import com.headissue.pigeon.util.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.Date;

@Singleton
public class AnswerHandler {

  private static final Log log = LogFactory.getLog(AnswerHandler.class);

  @Inject
  private EntityManagerFactory factory;

  @Inject
  private UserMapService userMapService;

  @Inject
  private AnswerTransformFactory transformFactory;

  public void receiveAnswer(Session _session, AnswerParameter p) {
    checkPreCondition(_session, p);
    EntityManager _manager = factory.createEntityManager();
    try {
      _manager.getTransaction().begin();
      Survey _survey = _manager.find(Survey.class, p.getSurveyId());
      checkNotNull(_survey, "unknown survey '%s'", p.getSurveyId());
      int _mapId = userMapService.getUserMapId(_session);
      final UserMap map;
      if (_mapId <= 0 || _mapId != p.getMapId()) {
        map = createUserMap(_survey, p, _manager);
      } else {
        map = loadUserMap(_survey, _mapId, _manager);
      }
      checkNotNull(map, "unknown user map for survey '%s'", p.getSurveyId());
      Date _timestamp = new Date(p.getTimestamp());
      for (UserAnswerValue _value: p.getValues().getAnswers()) {
        Question _question = findQuestion(_survey, _value.getQuestionId());
        if (_question == null) {
          continue;
        }
        storeAnswer(_survey, _question, map, _value, _manager, _timestamp);
      }
      _manager.getTransaction().commit();
      checkTrue(map.getId() != 0, "user map can not created for survey '%s'", p.getSurveyId());
      userMapService.setUserMapId(_session, map.getId());
    } catch (Exception e) {
      LogUtils.warn(log, "receiveAnswer: processing is failed");
      throw new AnswerException("processing is failed", e);
    } finally {
      _manager.close();
    }
  }

  private void storeAnswer(Survey _survey, Question _question, UserMap map, UserAnswerValue _value,
    EntityManager _manager, Date _timestamp)
  {
    TypedQuery<Answer> q = _manager.createNamedQuery("answer.findAnswerBySurveyQuestionAndUserMap",
      Answer.class);
    q.setParameter("survey", _survey);
    q.setParameter("question", _question);
    q.setParameter("userMap", map);
    Answer _answer = JPAUtils.getSingleResult(q);
    if (_answer == null) {
      _answer = new Answer();
      _answer.setTimestamp(_timestamp);
      _answer.setSurvey(_survey);
      _answer.setQuestion(_question);
      _answer.setUserMap(map);
    } else {
      _answer.getAnswerValues().clear();
      _answer.getAnswerTexts().clear();
    }
    AnswerTransform _transform = transformFactory.get(_question.getType().toString());
    if (_transform.transfer(_answer, _value)) {
      _manager.persist(_answer);
    } else {
      LogUtils.warn(log,
        "store answer is failed (surveyId=%s, questionId=%s, questionType=%s) user answer '%s'",
        _survey.getId(), _question.getId(), _question.getType(), _value);
    }
  }

  private void checkPreCondition(Session _session, AnswerParameter p) {
    checkNotNull(_session, "session is null");
    checkNotNull(p, "parameter is null");

  }

  UserMap createUserMap(Survey _survey, AnswerParameter p, EntityManager _manager) {
    UserMap map = new UserMap();
    map.setSurvey(_survey);
    map.setPageKey(p.getValues().getPageKey());
    map.setUserKey(p.getValues().getUserKey());
    map.setUserData(p.getValues().getUserData());
    map.setTimestamp(new Date(p.getTimestamp()));
    _manager.persist(map);
    return map;
  }

  UserMap loadUserMap(Survey _survey, int _mapId, EntityManager _manager) {
    TypedQuery<UserMap> q = _manager.createNamedQuery("answerUserMan.findBySurveyAndId",
      UserMap.class);
    q.setParameter("survey", _survey);
    q.setParameter("mapId", _mapId);
    return JPAUtils.getSingleResult(q);
  }


  Question findQuestion(Survey _survey, int _questionId) {
    for (Question q : _survey.getQuestions()) {
      if (q.getId() == _questionId) {
        return q;
      }
    }
    return null;
  }


  public static class AnswerParameter {

    private Long timestamp;

    private int surveyId;

    private int mapId;

    private UserAnswerValues values = null;

    public AnswerParameter() {
    }

    public Long getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(Long timestamp) {
      this.timestamp = timestamp;
    }

    public int getSurveyId() {
      return surveyId;
    }

    public void setSurveyId(int surveyId) {
      this.surveyId = surveyId;
    }

    public int getMapId() {
      return mapId;
    }

    public void setMapId(int mapId) {
      this.mapId = mapId;
    }

    public UserAnswerValues getValues() {
      return values;
    }

    public void setValues(UserAnswerValues values) {
      this.values = values;
    }
  }


  static void checkNotNull(Object _value, String _message, Object... args) {
    if (_value == null) {
      if (args != null && args.length > 0) {
        _message = String.format(_message, args);
      }
      throw new AnswerException(_message);
    }
  }

  static void checkTrue(boolean _value, String _message, Object... args) {
    if (!_value) {
      if (args != null && args.length > 0) {
        _message = String.format(_message, args);
      }
      throw new AnswerException(_message);
    }
  }
}
