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
import com.google.inject.Singleton;
import com.headissue.pigeon.survey.Question;
import com.headissue.pigeon.survey.QuestionText;
import com.headissue.pigeon.survey.Survey;
import com.headissue.pigeon.survey.answer.Answer;
import com.headissue.pigeon.util.JPAUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class AdminAnswerHandler {

  @Inject
  private EntityManagerFactory factory;

  /**
   * Returns a list of all anwsers
   */
  public List<AdminAnswer> getAnswerListFromSurvey(int _surveyId) {
    EntityManager _manager = factory.createEntityManager();
    try {
      Survey _survey = _manager.find(Survey.class, _surveyId);
      if (_survey == null) {
        throw new AdminException("unknown survey '" + _surveyId + "'");
      }
      Map<Integer, QuestionText> _answerMap = getAnswerMap(_survey);
      TypedQuery<Answer> q = _manager.createNamedQuery("answer.findAnswerListBySurvey", Answer.class);
      q.setParameter("survey", _survey);
      List<Answer> _answerList = JPAUtils.getResultList(q);
      return calculateAdminAnswerList(_survey, _answerMap, _answerList);
    } catch (Exception e) {
      throw new AdminException("read answers from survey '" + _surveyId + "' failed", e);
    } finally {
      _manager.close();
    }
  }

  private List<AdminAnswer> calculateAdminAnswerList(Survey _survey,
    Map<Integer, QuestionText> _answerMap, List<Answer> _answerList)
  {
    List<AdminAnswer> _adminList = new ArrayList<AdminAnswer>();
    for (Answer _answer : _answerList) {
      AdminAnswer _adminAnswer = new AdminAnswer();
      _adminAnswer.setAnswer(_answer);
      _adminAnswer.setSurvey(_survey);
      _adminAnswer.setQuestion(_answer.getQuestion());
      _adminAnswer.setUserMap(_answer.getUserMap());
      for (Integer id : _answer.getAnswerValues()) {
        QuestionText qt = _answerMap.get(id);
        if (qt != null) {
          _adminAnswer.addAnswer(qt);
        }
      }
      _adminAnswer.setTexts(_answer.getAnswerTexts());
      _adminList.add(_adminAnswer);
    }
    return _adminList;
  }

  private Map<Integer, QuestionText> getAnswerMap(Survey _survey) {
    Map<Integer, QuestionText> _answerMap = new HashMap<Integer, QuestionText>();
    for (Question _question : _survey.getQuestions()) {
      for (QuestionText _text : _question.getAnswers()) {
        _answerMap.put(_text.getId(), _text);
      }
    }
    return _answerMap;
  }
}
