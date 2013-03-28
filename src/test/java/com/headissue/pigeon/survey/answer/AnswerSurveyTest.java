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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.headissue.pigeon.PersistenceConfig;
import com.headissue.pigeon.PigeonModule;
import com.headissue.pigeon.SessionManager;
import com.headissue.pigeon.service.Session;
import com.headissue.pigeon.service.UserMapService;
import com.headissue.pigeon.survey.Question;
import com.headissue.pigeon.survey.QuestionText;
import com.headissue.pigeon.survey.QuestionType;
import com.headissue.pigeon.survey.Survey;
import com.headissue.pigeon.survey.SurveyHandler;
import com.headissue.pigeon.survey.SurveyStatus;
import com.headissue.pigeon.admin.AdminAnswer;
import com.headissue.pigeon.admin.AdminAnswerHandler;
import com.headissue.pigeon.survey.answer.AnswerHandler.AnswerParameter;
import com.headissue.pigeon.util.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

/**
 *
 */
public class AnswerSurveyTest {

  private static final Log log = LogFactory.getLog(AnswerSurveyTest.class);

  @Test
  public void testFindSurvey() {
    int _surveyId = survey.getId();
    Survey _survey = surveyHandler.findSurveyById(_surveyId);
    assertNotNull(_survey);
    assertNotNull(_survey.getQuestions());
    assertEquals(4, _survey.getQuestions().size());
  }

  @Test
  public void testSendFirstAnswer() {
    UserAnswerValues _values = createAnswerValues("test1", "mario", "dreirad",
      question1.questionId, question1.answers.get(0)); // "Ja"
    Session _session = sessionManager.getCurrentSession();
    int _mapId = callFirstAnswerAndReturnMapId(_session, _values, createDate(2012, 8, 28, 12, 4));

    UserMap map = find(UserMap.class, _mapId);
    assertNotNull(map);

    List<AdminAnswer> _answerList = adminAnswerHandler.getAnswerListFromSurvey(survey.getId());
    assertNotNull(_answerList);
    assertEquals(1, _answerList.size());
    AdminAnswer _answer = _answerList.get(0);
    assertEquals(question1.questionId, _answer.getQuestion().getId());
    assertQuestion(_answer.getAnswers(), question1.answers.get(0));
  }

  @Test
  public void testSendFirstAndSecondAnswer() {
    UserAnswerValues _values = createAnswerValues("test1", "mario", "dreirad",
      question1.questionId, question1.answers.get(0)); // "Ja"
    Session _session = sessionManager.getCurrentSession();
    int _mapId = callFirstAnswerAndReturnMapId(_session, _values, createDate(2012, 8, 28, 12, 4));
    assertTrue(_mapId > 0);

    // second answer
    _values = createAnserValues(question2.questionId, question2.answers.get(2)); // "drei"
    callOtherAnswer(_session, _values, createDate(2012, 8, 28, 12, 5));

    UserMap map = find(UserMap.class, _mapId);
    assertNotNull(map);

    List<AdminAnswer> _answerList = adminAnswerHandler.getAnswerListFromSurvey(survey.getId());
    assertNotNull(_answerList);
    assertEquals(2, _answerList.size());
    AdminAnswer _answer = _answerList.get(0);
    assertEquals(question2.questionId, _answer.getQuestion().getId());
    assertQuestion(_answer.getAnswers(), question2.answers.get(2));
  }

  @Test
  public void testSendFirstAndtheNext2Answers() {
    UserAnswerValues _values = createAnswerValues("test1", "mario", "dreirad",
      question1.questionId, question1.answers.get(0)); // "Ja"
    Session _session = sessionManager.getCurrentSession();
    int _mapId = callFirstAnswerAndReturnMapId(_session, _values, createDate(2012, 8, 28, 12, 4));
    assertTrue(_mapId > 0);

    // second answer
    _values = createAnserValues(question2.questionId, question2.answers.get(2)); // "drei"
    callOtherAnswer(_session, _values, createDate(2012, 8, 28, 12, 5));

    // Third answer
    //    3 -> "Ich wünsche mir einen Drucken-Button"
    //    4 -> "Was ist das denn? Amnesiestaub! Fantastisch!"
    _values = createAnserValues(question3.questionId, question3.answers.get(3), question3.answers.get(4));
    callOtherAnswer(_session, _values, createDate(2012, 8, 28, 12, 6));

    UserMap map = find(UserMap.class, _mapId);
    assertNotNull(map);

    List<AdminAnswer> _answerList = adminAnswerHandler.getAnswerListFromSurvey(survey.getId());
    assertNotNull(_answerList);
    assertEquals(3, _answerList.size());
    AdminAnswer _answer = _answerList.get(0);
    assertEquals(question3.questionId, _answer.getQuestion().getId());
    assertQuestion(_answer.getAnswers(), question3.answers.get(3));
    assertQuestion(_answer.getAnswers(), question3.answers.get(4));
  }

  @Test
  public void testAllQuestion() {
    UserAnswerValues _values = createAnswerValues("test1", "mario", "dreirad",
      question1.questionId, question1.answers.get(0)); // "Ja"
    Session _session = sessionManager.getCurrentSession();
    int _mapId = callFirstAnswerAndReturnMapId(_session, _values, createDate(2012, 8, 28, 12, 4));
    assertTrue(_mapId > 0);

    // second answer
    _values = createAnserValues(question2.questionId, question2.answers.get(2)); // "drei"
    callOtherAnswer(_session, _values, createDate(2012, 8, 28, 12, 5));

    // Third answer
    //    3 -> "Ich wünsche mir einen Drucken-Button"
    //    4 -> "Was ist das denn? Amnesiestaub! Fantastisch!"
    _values = createAnserValues(question3.questionId, question3.answers.get(3), question3.answers.get(4));
    callOtherAnswer(_session, _values, createDate(2012, 8, 28, 12, 6));

    // last answer (free text)
    _values = createAnserValues(question4.questionId, "Toll, was Ihr alles habt!");
    callOtherAnswer(_session, _values, createDate(2012, 8, 28, 12, 9));

    List<AdminAnswer> _answerList = adminAnswerHandler.getAnswerListFromSurvey(survey.getId());
    assertNotNull(_answerList);
    assertEquals(4, _answerList.size());
    AdminAnswer _answer = _answerList.get(0);
    assertEquals(question4.questionId, _answer.getQuestion().getId());
    assertThat(_answer.getTexts(), hasItem("Toll, was Ihr alles habt!"));
  }

  public void testAnswerAllQuestionInsurvey() {
    UserAnswerValues _values = createAnswerValues("test1", "mario", "dreirad",
      question1.questionId, question1.answers.get(0)); // "Ja"
    Session _session = sessionManager.getCurrentSession();
    int _mapId = callFirstAnswerAndReturnMapId(_session, _values, createDate(2012, 8, 28, 12, 4));
    assertTrue(_mapId > 0);

    // second answer
    _values = createAnserValues(question2.questionId, question2.answers.get(2)); // "drei"
    callOtherAnswer(_session, _values, createDate(2012, 8, 28, 12, 5));

    // Third answer
    //    3 -> "Ich wünsche mir einen Drucken-Button"
    //    4 -> "Was ist das denn? Amnesiestaub! Fantastisch!"
    _values = createAnserValues(question3.questionId, question3.answers.get(3), question3.answers.get(4));
    callOtherAnswer(_session, _values, createDate(2012, 8, 28, 12, 6));

    UserMap map = find(UserMap.class, _mapId);
    assertNotNull(map);

    // fourth answer
  }





  /** ----< Setup and helper methods >----------------------------------------------------------- */
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

  protected EntityManagerFactory factory;

  protected SurveyHandler surveyHandler;

  protected AnswerHandler answerHandler;

  protected UserMapService userMapService;

  protected AdminAnswerHandler adminAnswerHandler;

  protected SessionManager sessionManager = new SessionManager();

  protected Survey survey;

  protected QuestionValues question1;
  protected QuestionValues question2;
  protected QuestionValues question3;
  protected QuestionValues question4;

  @Before
  public void setUp() {
    factory = injector.getInstance(EntityManagerFactory.class);
    surveyHandler = injector.getInstance(SurveyHandler.class);
    answerHandler = injector.getInstance(AnswerHandler.class);
    userMapService = injector.getInstance(UserMapService.class);
    adminAnswerHandler = injector.getInstance(AdminAnswerHandler.class);

    setUpPersistence();
  }

  @After
  public void tearDown() {
    survey = null;
    question1 = null;
    question2 = null;
    question3 = null;
    question3 = null;
    adminAnswerHandler = null;
    userMapService = null;
    surveyHandler = null;
    answerHandler = null;
    factory.close();
    factory = null;
  }


  void setUpPersistence() {
    survey = new Survey();
    survey.setName("vvk");
    survey.setStatus(SurveyStatus.ENABLED);
    survey.setCreateAt(createDate(2012, 8, 23, 12, 33));
    survey.setUpdateAt(createDate(2012, 8, 23, 12, 34));

    Question q1 = new Question();
    q1.setType(QuestionType.BOOL);
    q1.setTitle("allgemein");
    q1.setText("Gefällt Ihnen die neue Suche nach Vorverkaufsstellen?");
    q1.addAnswer("Ja", 1);
    q1.addAnswer("Nein", 2);
    q1.setOrderBy(1);
    survey.addQuestion(q1);

    Question q2 = new Question();
    q2.setType(QuestionType.CHOICE);
    q2.setTitle("allgemein");
    q2.setText("Bewerten Sie das Buttondesign:");
    q2.addAnswer("Eins", 1);
    q2.addAnswer("Zwei", 2);
    q2.addAnswer("Drei", 3);
    q2.addAnswer("Vier", 4);
    q2.addAnswer("Fünf", 5);
    q2.addAnswer("Sechs", 6);
    q2.setOrderBy(2);
    survey.addQuestion(q2);

    Question q3 = new Question();
    q3.setType(QuestionType.MULTIPLE);
    q3.setTitle("allgemein");
    q3.setText("Kreuzen Sie Zutreffendes an.");
    q3.addAnswer("Ich wünsche mir mehr Optionen", 1);
    q3.addAnswer("Ich wünsche mir einen Share-Button", 2);
    q3.addAnswer("Ich wünsche mir einen Drucken-Button", 3);
    q3.addAnswer("Was ist das denn? Amnesiestaub! Fantastisch!", 4);
    q3.addAnswer("Ich bin vollends zufrieden", 5);
    q3.setOrderBy(3);
    survey.addQuestion(q3);

    Question q4 = new Question();
    q4.setType(QuestionType.FREE);
    q4.setTitle("allgemein");
    q4.setText("Haben Sie noch einen Verbesserungsvorschlag?");
    q4.setOrderBy(4);
    survey.addQuestion(q4);

    EntityManager manager = factory.createEntityManager();
    manager.getTransaction().begin();
    manager.persist(survey);
    manager.persist(q1);
    manager.persist(q2);
    manager.persist(q3);
    manager.persist(q4);
    manager.getTransaction().commit();
    //manager.close();

    question1 = createValues(q1);
    question2 = createValues(q2);
    question3 = createValues(q3);
    question4 = createValues(q4);

    LogUtils.debug(log, "question 1: %s", question1);
    LogUtils.debug(log, "question 2: %s", question2);
    LogUtils.debug(log, "question 3: %s", question3);
    LogUtils.debug(log, "question 4: %s", question4);
  }


  public static Date createDate(int year, int month, int day, int hour, int minute) {
    DateTime dt = new DateTime(year, month, day, hour, minute);
    return dt.toDate();
  }

  QuestionValues createValues(Question q) {
    QuestionValues v = new QuestionValues();
    v.questionId = q.getId();
    v.type = q.getType();
    for (QuestionText answer : q.getAnswers()) {
      v.answers.add(answer.getId());
    }
    return v;
  }

  static class QuestionValues {
    int questionId;
    String type;
    final List<Integer> answers = new ArrayList<Integer>();

    @Override
    public String toString() {
      return String.format("question [id=%s, answers=%s]", questionId, answers);
    }
  }

  UserAnswerValues createAnswerValues(String _pageKey, String _userKey, String _userData,
    int _questionId, String... _answerValues) {
    UserAnswerValues values = new UserAnswerValues();
    values.setPageKey(_pageKey);
    values.setUserKey(_userKey);
    values.setUserData(_userData);
    values.setAnswers(Arrays.asList(
      new UserAnswerValue(_questionId, Arrays.asList(_answerValues))
    ));
    return values;
  }

  UserAnswerValues createAnswerValues(String _pageKey, String _userKey, String _userData,
    int _questionId, int... _answerValues) {
    UserAnswerValues _values = new UserAnswerValues();
    _values.setPageKey(_pageKey);
    _values.setUserKey(_userKey);
    _values.setUserData(_userData);
    List<String> _list = new ArrayList<String>();
    for (int id : _answerValues) {
      _list.add(String.valueOf(id));
    }
    _values.setAnswers(Arrays.asList(
      new UserAnswerValue(_questionId, _list)
    ));
    return _values;
  }

  UserAnswerValues createAnserValues(int _questionId, String... _answerValues) {
    UserAnswerValues _values = new UserAnswerValues();
    _values.setAnswers(Arrays.asList(
      new UserAnswerValue(_questionId, Arrays.asList(_answerValues))
    ));
    return _values;
  }

  UserAnswerValues createAnserValues(int _questionId, int... _answerValues) {
    UserAnswerValues _values = new UserAnswerValues();
    List<String> _list = new ArrayList<String>();
    for (int id : _answerValues) {
      _list.add(String.valueOf(id));
    }
    _values.setAnswers(Arrays.asList(
      new UserAnswerValue(_questionId, _list)
    ));
    return _values;
  }

  <T> T find(Class<T> type, Object key) {
    EntityManager manager = factory.createEntityManager();
    try {
      return manager.find(type, key);
    } finally {
      manager.close();
    }
  }

  int callFirstAnswerAndReturnMapId(Session _session, UserAnswerValues _values, Date _timestamp) {
    AnswerParameter p = new AnswerParameter();
    p.setTimestamp(_timestamp.getTime());
    p.setValues(_values);
    p.setSurveyId(survey.getId());
    answerHandler.receiveAnswer(_session, p);
    int _mapId = userMapService.getUserMapId(_session);
    assertTrue(_mapId > 0);
    return _mapId;
  }

  void callOtherAnswer(Session _session, UserAnswerValues _values, Date _timestamp) {
    AnswerParameter p = new AnswerParameter();
    p.setTimestamp(_timestamp.getTime());
    p.setValues(_values);
    p.setSurveyId(survey.getId());
    answerHandler.receiveAnswer(_session, p);
    int _mapId = userMapService.getUserMapId(_session);
    assertTrue(_mapId > 0);
  }

  void assertQuestion(List<QuestionText> _list, int id) {
    QuestionText _text = null;
    for (QuestionText qt : _list) {
      if (qt.getId() == id) {
        _text = qt;
        break;
      }
    }
    assertNotNull(_text);
  }
}
