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
package com.headissue.pigeon;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.headissue.pigeon.service.JacksonService;
import com.headissue.pigeon.survey.Question;
import com.headissue.pigeon.survey.QuestionType;
import com.headissue.pigeon.survey.Survey;
import com.headissue.pigeon.survey.SurveyStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class SurveyTest {

  private Injector injector = Guice.createInjector(new PigeonModule() {
    {
     setPersistenceConfig(new PersistenceConfig() {
       @Override
       public String getUnitName() {
         return "pigeon-test";
       }
     });
    }
  });

  private EntityManagerFactory factory;
  private JacksonService jacksonService;

  @Before
  public void setUp() {
    factory = injector.getInstance(EntityManagerFactory.class);
    jacksonService = injector.getInstance(JacksonService.class);
    writeSurvey();
  }

  @After
  public void tearDown() {
    factory.close();
  }

  @Test
  public void testReadSurvey() {
    EntityManager manager = factory.createEntityManager();
    TypedQuery<Survey> q = manager.createQuery("SELECT s FROM Survey s", Survey.class);
    List<Survey> surveys = q.getResultList();
    assertNotNull(surveys);
    for (Survey survey : surveys) {
      System.out.println(survey);
    }
    manager.close();
  }

  @Test
  public void testSurveyToJson() {
    EntityManager manager = factory.createEntityManager();
    TypedQuery<Survey> q = manager.createQuery("SELECT s FROM Survey s", Survey.class);
    List<Survey> surveys = q.getResultList();
    assertNotNull(surveys);
    for (Survey survey : surveys) {
      System.out.println(jacksonService.toString(survey));
    }
    manager.close();
  }

  void writeSurvey() {
    EntityManager manager = factory.createEntityManager();
    try {
      Survey survey = new Survey();
      survey.setCreateAt(new Date());
      survey.setName("Test");
      survey.setStatus(SurveyStatus.ENABLED);
      survey.setUpdateAt(new Date());

      Question question = new Question();
      question.setText("Wie geht es?");
      question.setTitle("Befinden");
      question.setType(QuestionType.BOOL);
      question.addAnswer("mir geht es gut", 1);
      question.addAnswer("mir geht es nicht so gut", 2);
      survey.addQuestion(question);
      manager.getTransaction().begin();
      manager.persist(survey);
      manager.persist(question);
      manager.getTransaction().commit();
    } finally {
      manager.close();
    }
  }
}
