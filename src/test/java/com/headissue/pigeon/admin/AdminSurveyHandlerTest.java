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

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.headissue.pigeon.PersistenceConfig;
import com.headissue.pigeon.PigeonException;
import com.headissue.pigeon.PigeonModule;
import com.headissue.pigeon.service.BeanException;
import com.headissue.pigeon.service.JacksonService;
import com.headissue.pigeon.service.AdminSurveyService;
import com.headissue.pigeon.service.QuestionValidator;
import com.headissue.pigeon.survey.QuestionType;
import com.headissue.pigeon.survey.Survey;
import com.headissue.pigeon.survey.SurveyHandler;
import com.headissue.pigeon.admin.AdminSurveyHandler.SurveyParameter;
import com.headissue.pigeon.admin.json.QuestionValue;
import com.headissue.pigeon.admin.json.SurveyValue;
import com.headissue.pigeon.survey.answer.AnswerSurveyTest;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

public class AdminSurveyHandlerTest {

  private final Injector injector = Guice.createInjector(new PigeonModule() {
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
  private JacksonService jacksonService;

  @Inject
  private AdminSurveyHandler adminSurveyHandler;

  @Inject
  private AdminSurveyService convertFactory;

  @Inject
  private SurveyHandler surveyHandler;

  @Before
  public void setUp() {
    injector.injectMembers(this);
  }

  @Test
  public void testReadJsonFromFile() {
    String _content = readContent("testSurveyOkay.json");
    SurveyValue _survey = jacksonService.fromString(_content, SurveyValue.class);
    assertNotNull(_survey);
    assertNotNull(_survey.getQuestions());
    assertEquals(5, _survey.getQuestions().size());
    assertEquals("Test Survey", _survey.getName());
    for (QuestionValue _value: _survey.getQuestions()) {
      if (QuestionType.BOOL.equals(_value.getType())) {
        assertEquals(2, _value.getAnswers().size());
      } else if (QuestionType.CHOICE.equals(_value.getType()) ||
        QuestionType.MULTIPLE.equals(_value.getType()))
      {
        assertTrue(_value.getAnswers().size() > 1);
      } else if (QuestionType.FREE.equals(_value.getType())) {
        assertTrue(_value.getAnswers() == null || _value.getAnswers().size() == 0);;
      }
    }
    assertEquals(QuestionType.CHOICE, _survey.getQuestions().get(0).getType());
    assertEquals(QuestionType.BOOL, _survey.getQuestions().get(1).getType());
    assertEquals(QuestionType.FREE, _survey.getQuestions().get(2).getType());
    assertEquals(QuestionType.MULTIPLE, _survey.getQuestions().get(3).getType());
    assertEquals(QuestionType.BOOL, _survey.getQuestions().get(4).getType());
    QuestionValue _value = _survey.getQuestions().get(0);
    assertThat(_value.getAnswers(), hasItems("Answer 1", "Answer 2", "Answer 3", "Answer 4"));
    _value = _survey.getQuestions().get(1);
    assertThat(_value.getAnswers(), hasItems("Ja", "Nein"));
    // 2 is free text
    _value = _survey.getQuestions().get(3);
    assertThat(_value.getAnswers(), hasItems("A", "b", "C", "D", "E", "F"));
    _value = _survey.getQuestions().get(4);
    assertThat(_value.getAnswers(), hasItems("Toll", "Schlecht"));
  }

  @Test(expected = AdminException.class)
  public void testCreateSurveyNullParameter() {
    adminSurveyHandler.createSurvey(null);
  }

  @Test(expected = AdminException.class)
  public void testCreateSurveyNullValue() {
    SurveyParameter p = new SurveyParameter();
    adminSurveyHandler.createSurvey(p);
  }

  @Test
  public void testCreateSurveySuccess() {
    String _content = readContent("testSurveyOkay.json");
    SurveyValue _survey = jacksonService.fromString(_content, SurveyValue.class);
    assertNotNull(_survey);

    callInsertSurvey(_survey);
  }

  @Test
  public void testOverwriteSurveySuccess() {
    String _content = readContent("testSurveyOkay.json");
    SurveyValue _survey = jacksonService.fromString(_content, SurveyValue.class);
    assertNotNull(_survey);

    int _surveyId = callInsertSurvey(_survey);

    _content = readContent("testSurveyOkay2.json");
    _survey = jacksonService.fromString(_content, SurveyValue.class);
    assertNotNull(_survey);

    SurveyParameter p = new SurveyParameter();
    p.setTimestamp(AnswerSurveyTest.createDate(2012, 8, 23, 12, 8).getTime());
    p.setSurvey(_survey);
    p.setSurveyId(_surveyId);
    _surveyId = adminSurveyHandler.overwriteSurvey(p);
    assertTrue(_surveyId > 0);

    Survey s = surveyHandler.findSurveyById(_surveyId);
    assertNotNull(s);
    assertNotNull(s.getQuestions());
    assertEquals(4, s.getQuestions().size());

    assertEquals(QuestionType.CHOICE, s.getQuestions().get(0).getType());
    assertEquals(QuestionType.FREE, s.getQuestions().get(1).getType());
    assertEquals(QuestionType.BOOL, s.getQuestions().get(2).getType());
    assertEquals(QuestionType.MULTIPLE, s.getQuestions().get(3).getType());
  }

  int callInsertSurvey(SurveyValue _survey) {
    SurveyParameter p = new SurveyParameter();
    p.setTimestamp(AnswerSurveyTest.createDate(2012, 8, 23, 12, 4).getTime());
    p.setSurvey(_survey);
    int _surveyId = adminSurveyHandler.createSurvey(p);
    assertTrue(_surveyId > 0);
    return _surveyId;
  }

  @Test(expected = BeanException.class)
  public void testBoolQuestionWrong1() {
    String _content = readContent("testSurveyBoolWrong1.json");
    SurveyValue _survey = jacksonService.fromString(_content, SurveyValue.class);
    convertFactory.createSurvey(_survey, AnswerSurveyTest.createDate(2012, 8, 23, 12, 4));
  }

  @Test(expected = BeanException.class)
  public void testBoolQuestionWrong2() {
    String _content = readContent("testSurveyBoolWrong2.json");
    SurveyValue _survey = jacksonService.fromString(_content, SurveyValue.class);
    convertFactory.createSurvey(_survey, AnswerSurveyTest.createDate(2012, 8, 23, 12, 4));
  }

  @Test(expected = BeanException.class)
  public void testMultipleQuestionWrong() {
    String _content = readContent("testSurveyMultipleWrong.json");
    SurveyValue _survey = jacksonService.fromString(_content, SurveyValue.class);
    convertFactory.createSurvey(_survey, AnswerSurveyTest.createDate(2012, 8, 23, 12, 4));
  }

  @Test(expected = BeanException.class)
  public void testChoiceQuestionWrong() {
    String _content = readContent("testSurveyChoiceWrong.json");
    SurveyValue _survey = jacksonService.fromString(_content, SurveyValue.class);
    convertFactory.createSurvey(_survey, AnswerSurveyTest.createDate(2012, 8, 23, 12, 4));
  }

  @Test(expected = BeanException.class)
  public void testEmptyQuestion1() {
    String _content = readContent("testSurveyEmptyQuestion1.json");
    SurveyValue _survey = jacksonService.fromString(_content, SurveyValue.class);
    convertFactory.createSurvey(_survey, AnswerSurveyTest.createDate(2012, 8, 23, 12, 4));
  }

  @Test
  public void testEmptyQuestion2() {
    String _content = readContent("testSurveyEmptyQuestion2.json");
    SurveyValue _survey = jacksonService.fromString(_content, SurveyValue.class);
    Survey s = convertFactory.createSurvey(_survey, AnswerSurveyTest.createDate(2012, 8, 23, 12, 4));
    assertNotNull(s);
    assertNotNull(s.getQuestions());
    assertTrue(s.getQuestions().isEmpty());
  }

  @Test(expected = BeanException.class)
  public void testEmptyQuestionText() {
    String _content = readContent("testSurveyEmptyQuestionText.json");
    SurveyValue _survey = jacksonService.fromString(_content, SurveyValue.class);
    convertFactory.createSurvey(_survey, AnswerSurveyTest.createDate(2012, 8, 23, 12, 4));
  }

  String readContent(String _resName) {
    InputStream in = getClass().getResourceAsStream(_resName);
    assertNotNull(in);
    try {
      return IOUtils.toString(in, "UTF-8");
    } catch (IOException e) {
      return null;
    } finally {
      IOUtils.closeQuietly(in);
    }
  }
}
