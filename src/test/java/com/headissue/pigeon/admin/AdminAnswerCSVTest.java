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
import com.google.inject.Injector;
import com.headissue.pigeon.PigeonModule;
import com.headissue.pigeon.service.JacksonService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

@Ignore("Would only be working on a postgresql database!")
public class AdminAnswerCSVTest {

  private final Injector injector = Guice.createInjector(new PigeonModule());

  private AdminAnswerHandler handler;
  private JacksonService jacksonService;

  @Before
  public void setUp() {
    handler = injector.getInstance(AdminAnswerHandler.class);
    jacksonService = injector.getInstance(JacksonService.class);
  }

  @Test
  public void testWriteAnswerToJson() {
    List<AdminAnswer> answerList = handler.getAnswerListFromSurvey(1);
    assertNotNull(answerList);
    System.out.println(jacksonService.toString(answerList));
  }
}
