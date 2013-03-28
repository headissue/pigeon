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
import com.headissue.pigeon.PigeonModule;
import com.headissue.pigeon.service.JacksonService;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

/**
 * Reference to read an answer content from put or post.
 */
public class AnswerValuesTest {


  private Injector injector = Guice.createInjector(new PigeonModule());

  private JacksonService jacksonService;

  @Before
  public void setUp() {
    jacksonService = injector.getInstance(JacksonService.class);
  }

  @Test
  public void testReadAnswerPost() {
    String _content = readContent("testPost.json");
    assertNotNull(_content);
    UserAnswerValues _answer = jacksonService.fromString(_content, UserAnswerValues.class);
    assertNotNull(_answer);
    assertEquals(3, _answer.getAnswers().size());
    assertAnswerValue(_answer.getAnswers(), 12, "34");
    assertAnswerValue(_answer.getAnswers(), 14, "Mehr sport oder sowas");
    assertAnswerValue(_answer.getAnswers(), 35, "44", "23", "15");
  }

  @Test
  public void testReadAnswerPut() {
    String _content = readContent("testPut.json");
    assertNotNull(_content);
    UserAnswerValues _answer = jacksonService.fromString(_content, UserAnswerValues.class);
    assertNotNull(_answer);
    assertEquals("fussball", _answer.getPageKey());
    assertEquals("ski", _answer.getUserKey());
    assertEquals("bal-bla-äüöß", _answer.getUserData());
    assertEquals(3, _answer.getAnswers().size());
    assertAnswerValue(_answer.getAnswers(), 12, "34");
    assertAnswerValue(_answer.getAnswers(), 14, "Mehr sport oder sowas");
    assertAnswerValue(_answer.getAnswers(), 35, "44", "23", "15");

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

  void assertAnswerValue(List<UserAnswerValue> _values, int id, String... _exceptedValues) {
    UserAnswerValue _value = null;
    for (UserAnswerValue v: _values) {
      if (v.getQuestionId() == id) {
        _value = v;
        break;
      }
    }
    assertNotNull("answer must exist id=" + id, _value);
    assertThat(_value.getValues(), hasItems(_exceptedValues));
  }
}
