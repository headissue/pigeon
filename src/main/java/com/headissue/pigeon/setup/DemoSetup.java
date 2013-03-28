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
package com.headissue.pigeon.setup;

import com.headissue.pigeon.PersistenceConfig;
import com.headissue.pigeon.PigeonModule;
import com.headissue.pigeon.survey.Question;
import com.headissue.pigeon.survey.QuestionType;
import com.headissue.pigeon.survey.Survey;
import com.headissue.pigeon.survey.SurveyStatus;
import com.headissue.pigeon.util.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.EntityManager;
import java.util.Date;

public class DemoSetup extends PigeonModule {

  private static final Log log = LogFactory.getLog(DemoSetup.class);

  public DemoSetup() {
    setPersistenceConfig(new PersistenceConfig() {
      @Override
      public String getUnitName() {
        return "pigeon-demo";
      }

      @Override
      public void initializeDatabase(EntityManager _manager) {
        Survey _survey = new Survey();
        _survey.setName("Default Survey");
        _survey.setCreateAt(new Date());
        _survey.setUpdateAt(new Date());
        _survey.setStatus(SurveyStatus.ENABLED);
        Question q1 = new Question();
        q1.setTitle("Default");
        q1.setText("A Yes / No Question");
        q1.setType(QuestionType.BOOL);
        q1.addAnswer("Yes", 1);
        q1.addAnswer("No", 2);
        _survey.addQuestion(q1);
        Question q2 = new Question();
        q2.setTitle("Default");
        q2.setText("Choose Question");
        q2.setType(QuestionType.CHOICE);
        q2.addAnswer("A", 1);
        q2.addAnswer("B", 2);
        q2.addAnswer("C", 3);
        q2.addAnswer("D", 4);
        _survey.addQuestion(q2);
        Question q3 = new Question();
        q3.setTitle("Default");
        q3.setText("Multiple Question");
        q3.setType(QuestionType.MULTIPLE);
        q3.addAnswer("1", 1);
        q3.addAnswer("2", 2);
        q3.addAnswer("3", 3);
        q3.addAnswer("4", 4);
        _survey.addQuestion(q3);
        Question q4 = new Question();
        q4.setTitle("Default");
        q4.setText("Free Text Question");
        q4.setType(QuestionType.FREE);
        _survey.addQuestion(q4);
        _manager.getTransaction().begin();
        _manager.persist(_survey);
        _manager.getTransaction().commit();
        LogUtils.info(log, "Initialize the default survey (id=%s)", _survey.getId());
      }
    });
  }


}
