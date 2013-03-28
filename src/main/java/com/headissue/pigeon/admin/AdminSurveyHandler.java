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
import com.headissue.pigeon.admin.json.QuestionValue;
import com.headissue.pigeon.service.AdminSurveyService;
import com.headissue.pigeon.survey.QuestionType;
import com.headissue.pigeon.survey.Survey;
import com.headissue.pigeon.admin.json.SurveyValue;
import com.headissue.pigeon.survey.SurveyStatus;
import com.headissue.pigeon.util.JPAUtils;
import com.headissue.pigeon.util.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Singleton
public class AdminSurveyHandler {

  private static final Log log = LogFactory.getLog(AdminSurveyHandler.class);

  @Inject
  private EntityManagerFactory factory;

  @Inject
  private AdminSurveyService adminSurveyService;

  public List<SurveyOverviewValue> getAllSurveys() {
    EntityManager _manager = factory.createEntityManager();
    try {
      TypedQuery<SurveyOverviewValue> q = _manager.createNamedQuery("survey.allSurveyOverview",
        SurveyOverviewValue.class);
      q.setParameter("status", SurveyStatus.ENABLED);
      return JPAUtils.getResultList(q);
    } finally {
      _manager.close();
    }
  }

  public int createSurvey(SurveyParameter p) {
    checkNotNull(p, "parameter is null");
    checkNotNull(p.getSurvey(), "survey values are null");
    Survey _survey = adminSurveyService.createSurvey(p.getSurvey(), new Date(p.getTimestamp()));
    EntityManager _manager = factory.createEntityManager();
    try {
      _manager.getTransaction().begin();
      _manager.persist(_survey);
      _manager.getTransaction().commit();
      return _survey.getId();
    } catch (Exception e) {
      LogUtils.warn(log, e, "save the survey failed");
      if (_manager.getTransaction().isActive()) {
        _manager.getTransaction().rollback();
      }
      return 0;
    } finally {
      _manager.close();
    }
  }

  public int overwriteSurvey(SurveyParameter p) {
    checkNotNull(p, "parameter is null");
    checkNotNull(p.getSurvey(), "survey values are null");
    EntityManager _manager = factory.createEntityManager();
    try {
      _manager.getTransaction().begin();
      Survey _survey = _manager.find(Survey.class, p.getSurveyId());
      adminSurveyService.mergeSurvey(_survey, p.getSurvey(), new Date(p.getTimestamp()), _manager);
      _manager.getTransaction().commit();
      return _survey.getId();
    } catch (Exception e) {
      LogUtils.warn(log, e, "save the survey failed");
      if (_manager.getTransaction().isActive()) {
        _manager.getTransaction().rollback();
      }
      return 0;
    } finally {
      _manager.close();
    }
  }

  public SurveyValue getSurvey(int _surveyId) {
    if (_surveyId <= 0) {
      return defaultSurvey;
    }
    EntityManager _manager = factory.createEntityManager();
    try {
      Survey _survey = _manager.find(Survey.class, _surveyId);
      if (_survey == null || _survey.getStatus() == SurveyStatus.DISABLED) {
        return defaultSurvey;
      }
      return adminSurveyService.fromSurvey(_survey);
    } catch (Exception e) {
      LogUtils.warn(log, e, "get the survey '%s' failed", _surveyId);
      return defaultSurvey;
    } finally {
      _manager.close();
    }
  }

  public boolean disableSurvey(int _surveyId) {
    if (_surveyId <= 0) {
      return false;
    }
    EntityManager _manager = factory.createEntityManager();
    try {
      Survey s = _manager.find(Survey.class, _surveyId);
      if (s == null) {
        return false;
      }
      _manager.getTransaction().begin();
      s.setStatus(SurveyStatus.DISABLED);
      _manager.getTransaction().commit();
      return true;
    } catch (Exception e) {
      LogUtils.warn(log, "set survey '%s' to disabled failed", _surveyId);
      _manager.getTransaction().rollback();
      return false;
    } finally {
      _manager.close();
    }
  }

  public  static class SurveyParameter {

    private long timestamp;

    private SurveyValue survey;

    private int surveyId = 0;

    public SurveyParameter() {
    }

    public int getSurveyId() {
      return surveyId;
    }

    public void setSurveyId(int surveyId) {
      this.surveyId = surveyId;
    }

    public long getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(long timestamp) {
      this.timestamp = timestamp;
    }

    public SurveyValue getSurvey() {
      return survey;
    }

    public void setSurvey(SurveyValue survey) {
      this.survey = survey;
    }
  }

  static void checkNotNull(Object _value, String _message, Object... args) {
    if (_value == null) {
      if (args != null && args.length > 0) {
        _message = String.format(_message, args);
      }
      throw new AdminException(_message);
    }
  }


  private static final SurveyValue defaultSurvey = new SurveyValue();

  static {
    defaultSurvey.setName("Default Survey");
    QuestionValue q1 = new QuestionValue();
    q1.setTitle("Default");
    q1.setText("Yes / No Question");
    q1.setType(QuestionType.BOOL);
    q1.setAnswers(Arrays.asList("Yes", "No"));
    QuestionValue q2 = new QuestionValue();
    q2.setTitle("Default");
    q2.setText("Choose Question");
    q2.setType(QuestionType.CHOICE);
    q2.setAnswers(Arrays.asList("A", "B", "C", "D"));
    QuestionValue q3 = new QuestionValue();
    q3.setTitle("Default");
    q3.setText("Multiple Question");
    q3.setType(QuestionType.MULTIPLE);
    q3.setAnswers(Arrays.asList("1", "2", "3", "4"));
    QuestionValue q4 = new QuestionValue();
    q4.setTitle("Default");
    q4.setText("Freitext Question");
    q4.setType(QuestionType.FREE);
    q4.setAnswers(new ArrayList<String>());
    defaultSurvey.setQuestions(Arrays.asList(q1, q2, q3, q4));
  }
}
