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

import com.headissue.pigeon.survey.Question;
import com.headissue.pigeon.survey.QuestionType;

/**
 * Checks whether the number of answers fit the question type
 */
public class AdminQuestionValidator extends QuestionValidator {

  public AdminQuestionValidator() {
    super();
    addValidation(QuestionType.BOOL, new QuestionValidate() {
      @Override
      public void check(Question q) {
        if (q.getAnswers().size() != 2) {
          throw new BeanException(q.getType() + " question must always have 2 answers");
        }
      }
    });
    QuestionValidate choiceMultiple = new QuestionValidate() {
      @Override
      public void check(Question q) {
        if (q.getAnswers().size() < 2) {
          throw new BeanException(q.getType() + " question must have more then 1 answers");
        }
      }
    };
    addValidation(QuestionType.CHOICE, choiceMultiple);
    addValidation(QuestionType.MULTIPLE, choiceMultiple);
    addValidation(QuestionType.MULTIPLE_FREE, choiceMultiple);
    addValidation(QuestionType.FREE, new QuestionValidate() {
      @Override
      public void check(Question q) {
        if (q.getAnswers() != null && q.getAnswers().size() > 0) {
          throw new BeanException(q.getType() + " question must have no answers");
        }
      }
    });

  }
}
