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

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.headissue.pigeon.PersistenceConfig;
import com.headissue.pigeon.PigeonModule;
import com.headissue.pigeon.SessionManager;
import com.headissue.pigeon.admin.json.SurveyValue;
import com.headissue.pigeon.survey.Question;
import com.headissue.pigeon.survey.QuestionText;
import com.headissue.pigeon.survey.Survey;
import com.headissue.pigeon.survey.answer.AnswerHandler;
import com.headissue.pigeon.survey.answer.AnswerHandler.AnswerParameter;
import com.headissue.pigeon.survey.answer.UserAnswerValue;
import com.headissue.pigeon.survey.answer.UserAnswerValues;
import com.headissue.pigeon.util.LogUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

/**
 *
 */
public class AdminSurveyServiceTest {

  private static final Log log = LogFactory.getLog(AdminSurveyServiceTest.class);


  @Test
  public void testChangeSurveyTitle() throws Exception {
    SurveyValue sv = getSurveyValue("changeSurveyTitle.json");
    EntityManager _manager = mergeSurvey(sv);
    _manager.close();
    assertEquals("Blabla Umfrage", survey.getName());
  }

  @Test
  public void testAddAnswerText() throws Exception {
    SurveyValue sv = getSurveyValue("addAnswerText.json");
    EntityManager _manager = mergeSurvey(sv);
    _manager.close();
    Question q = survey.getQuestions().get(0);
    assertNotNull(q);
    assertEquals(5, q.getAnswers().size());
  }

  @Test
  public void testLessAnswerText() throws Exception {
    SurveyValue sv = getSurveyValue("lessAnswerText.json");
    EntityManager _manager = mergeSurvey(sv);
    _manager.close();
    Question q = survey.getQuestions().get(0);
    assertNotNull(q);
    assertEquals(3, q.getAnswers().size());
  }

  @Test
  public void testAddQuestion() throws Exception {
    assertEquals(2, survey.getQuestions().size());
    SurveyValue sv = getSurveyValue("addQuestion.json");
    EntityManager _manager = mergeSurvey(sv);
    _manager.close();
    assertEquals(3, survey.getQuestions().size());
  }

  @Test
  public void testLessQuestion() throws Exception {
    assertEquals(2, survey.getQuestions().size());
    SurveyValue sv = getSurveyValue("lessQuestion.json");
    EntityManager _manager = mergeSurvey(sv);
    _manager.close();
    assertEquals(1, survey.getQuestions().size());
  }

  @Test
  public void testSwitchQuestion() throws Exception {
    SurveyValue sv = getSurveyValue("switchQuestion.json");
    EntityManager _manager = mergeSurvey(sv);
    _manager.close();
    assertEquals(2, survey.getQuestions().size());
    Question q1 = survey.getQuestions().get(0);
    assertEquals("Multiple Frage", q1.getText());
    Question q2 = survey.getQuestions().get(1);
    assertEquals("Choose Frage", q2.getText());
  }

  /** ----< Setup and helper methods >----------------------------------------------------------- */

  private static final Date CREATE_DATE = createDate(2013, 1, 10, 8, 22);
  private static final Date ANSWER_DATE = createDate(2013, 1, 10, 9, 18);
  private static final Date UPDATE_DATE = createDate(2013, 1, 10, 13, 34);

  protected Injector injector = Guice.createInjector(new PigeonModule() {
    {
      setPersistenceConfig(new PersistenceConfig() {
        @Override
        public String getUnitName() {
          return "pigeon-test";
        }
      });
    }
  });

  @Inject
  protected EntityManagerFactory factory;

  @Inject
  protected Survey survey;

  @Inject
  protected JacksonService jackson;

  @Inject
  protected AdminSurveyService adminSurveyService;

  @Before
  public void setUp() throws Exception {
    injector.injectMembers(this);

    SurveyValue _value = getSurveyValue("testSurvey.json");
    survey = adminSurveyService.createSurvey(_value, CREATE_DATE);
    EntityManager _manager = factory.createEntityManager();
    _manager.getTransaction().begin();
    _manager.persist(survey);
    _manager.getTransaction().commit();
    _manager.close();


    UserAnswerValues a = new UserAnswerValues();
    a.setPageKey("testSurvey");
    a.setUserData("gans");
    a.setUserKey("gustav");
    a.setAnswers(new ArrayList<UserAnswerValue>());
    AnswerParameter p = new AnswerParameter();
    p.setSurveyId(survey.getId());
    p.setTimestamp(ANSWER_DATE.getTime());
    p.setValues(a);
    LogUtils.debug(log, "survey id:    %s", survey.getId());
    for (Question q : survey.getQuestions()) {
      LogUtils.debug(log, "querstion id: %s", q.getId());
      UserAnswerValue v = new UserAnswerValue();
      v.setQuestionId(q.getId());
      v.setValues(Arrays.asList(q.getAnswers().get(0).getText()));
      a.getAnswers().add(v);
      for (QuestionText qt : q.getAnswers()) {
        LogUtils.debug(log, "answert id:   %s", qt.getId());
      }
    }

    SessionManager _sessionManager = new SessionManager();
    AnswerHandler _answerHandler = injector.getInstance(AnswerHandler.class);
    _answerHandler.receiveAnswer(_sessionManager.getCurrentSession(), p);
  }

  @After
  public void tearDown() {
    factory.close();
    factory = null;
  }

  SurveyValue getSurveyValue(String _resName) throws Exception {
    return jackson.fromString(getContent(_resName), SurveyValue.class);
  }

  EntityManager mergeSurvey(SurveyValue _value) throws Exception {
    EntityManager _manager = factory.createEntityManager();
    try {
      _manager.getTransaction().begin();
      Survey s = _manager.find(Survey.class, survey.getId());
      adminSurveyService.mergeSurvey(s, _value, UPDATE_DATE, _manager);
      _manager.persist(s);
      _manager.getTransaction().commit();
      survey = s;
    } catch (Exception e) {
      if (_manager.getTransaction().isActive()) {
        _manager.getTransaction().rollback();
      }
      throw e;
    }
    return _manager;
  }

  static String getContent(String _resName) throws Exception {
    InputStream in = AdminSurveyServiceTest.class.getResourceAsStream(_resName);
    try {
      return IOUtils.toString(in, "UTF-8");
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  static Date createDate(int year, int month, int day, int hour, int minute) {
    DateTime dt = new DateTime(year, month, day, hour, minute);
    return dt.toDate();
  }
}
