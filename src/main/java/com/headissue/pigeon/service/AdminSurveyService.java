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
import com.google.inject.Singleton;
import com.headissue.pigeon.admin.json.QuestionValue;
import com.headissue.pigeon.admin.json.SurveyValue;
import com.headissue.pigeon.survey.Question;
import com.headissue.pigeon.survey.QuestionText;
import com.headissue.pigeon.survey.QuestionType;
import com.headissue.pigeon.survey.Survey;
import com.headissue.pigeon.survey.SurveyStatus;
import com.headissue.pigeon.util.LogUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * converts between entities and data objects
 */
@Singleton
public class AdminSurveyService {

  private static final Log log = LogFactory.getLog(AdminSurveyService.class);

  @Inject
  private BeanService beanService;

  @Inject
  private QuestionValidator validator;


  public Survey createSurvey(SurveyValue _surveyValue, Date _timestamp) {
    Survey _survey = beanService.copy(Survey.class, _surveyValue, SURVEY_PROPERTIES);
    _survey.setCreateAt(_timestamp);
    _survey.setUpdateAt(_timestamp);
    _survey.setStatus(SurveyStatus.ENABLED);
    return fillQuestion(_survey, _surveyValue);
  }

  public SurveyValue fromSurvey(Survey _survey) {
    SurveyValue s = new SurveyValue();
    s.setName(_survey.getName());
    s.setKey(_survey.getKey());
    s.setQuestions(new ArrayList<QuestionValue>());
    for (Question q : _survey.getQuestions()) {
      s.getQuestions().add(fromQuestion(q));
    }
    return s;
  }

  QuestionValue fromQuestion(Question q) {
    QuestionValue qv = beanService.copy(QuestionValue.class, q, QUESTION_PROPERTIES);
    qv.setAnswers(new ArrayList<String>());
    for (QuestionText t : q.getAnswers()) {
      qv.getAnswers().add(t.getText());
    }
    return qv;
  }

  Question fillQuestion(QuestionValue qv) {
    Question q = beanService.copy(Question.class, qv, QUESTION_PROPERTIES);
    int orderBy = 0;
    for (String _text : qv.getAnswers()) {
      if (StringUtils.isEmpty(_text)) {
        throw new BeanException("question text can not be null or empty");
      }
      q.addAnswer(_text, ++orderBy);
    }
    validator.verify(q);
    return q;
  }

  Survey fillQuestion(Survey _survey, SurveyValue _surveyValue) {
    if (_surveyValue.getQuestions() == null) {
      return _survey;
    }
    if (_survey.getQuestions() == null) {
      _survey.setQuestions(new ArrayList<Question>());
    }
    _survey.getQuestions().clear();
    int _orderBy = 0;
    for (QuestionValue _value : _surveyValue.getQuestions()) {
      _survey.addQuestion(fillQuestion(_value), ++_orderBy);
    }
    if (_survey.getQuestions().isEmpty()) {
      throw new BeanException("missing questions");
    }
    return _survey;
  }

  public void mergeSurvey(Survey _survey, SurveyValue _value, Date _timestamp,
    EntityManager _manager) {
    beanService.copy(_survey, _value, SURVEY_PROPERTIES);
    _survey.setUpdateAt(_timestamp);
    mergeQuestion(_survey, _value, _manager);
  }

  void mergeQuestion(Survey _survey, SurveyValue _value, EntityManager _manager) {
    List<Question> _items = new ArrayList<Question>(_survey.getQuestions());
    int _orderBy = 0;
    Question q = null;
    for (QuestionValue qv : _value.getQuestions()) {
      if (!_items.isEmpty()) {
        q = beanService.copy(_items.remove(0), qv, QUESTION_PROPERTIES);
        q.setOrderBy(++_orderBy);
        LogUtils.debug(log, "recycle question %s: '%s'", q.getId(), q.getText());
      } else {
        q = beanService.copy(Question.class, qv, QUESTION_PROPERTIES);
        _survey.addQuestion(q, ++_orderBy);
        LogUtils.debug(log, "add a new question:  '%s'", q.getText());
      }
      mergeQuestionText(q, qv, _manager);
    }
    if (!_items.isEmpty()) {
      for (Question qq : _items) {
        LogUtils.debug(log, "remove question %s", qq.getId());
        Query query = _manager.createNamedQuery("answer.deleteAnswerByQuestion");
        query.setParameter("question", qq);
        query.executeUpdate();
        _survey.getQuestions().remove(qq);
      }
    }
  }

  void mergeQuestionText(Question q, QuestionValue qv, EntityManager _manager) {
    List<QuestionText> _items = new ArrayList<QuestionText>(q.getAnswers());
    int _orderBy = 0;
    for (String _text : qv.getAnswers()) {
      if (!_items.isEmpty()) {
        QuestionText qt = _items.remove(0); // use the first qt
        qt.setOrderBy(++_orderBy);
        qt.setText(_text);
        LogUtils.debug(log, "recycle answer %s: '%s'", qt.getId(), _text);
      } else {
        LogUtils.debug(log, "add a answer text: '%s'", _text);
        q.addAnswer(_text, ++_orderBy);
      }
    }
    if (!_items.isEmpty()) {
      for (QuestionText qt : _items) {
        LogUtils.debug(log, "remove answer %s", qt.getId());
        q.getAnswers().remove(qt);
      }
    }
  }

  private static final String[] QUESTION_PROPERTIES = {"title", "text", "type"};

  private static final String[] SURVEY_PROPERTIES = {"name", "key"};
}
