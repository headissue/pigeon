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

import java.util.HashMap;
import java.util.Map;

public class QuestionValidator {

  public static interface QuestionValidate {
    void check(Question q);
  }

  private final Map<String, QuestionValidate> type2Validate = new HashMap<String, QuestionValidate>();

  public void addValidation(String questionType, QuestionValidate validate) {
    type2Validate.put(questionType, validate);
  }

  public void verify(Question q) {
    if (!type2Validate.containsKey(q.getType())) {
      throw new RuntimeException(q.getType() + ": unknown question type");
    }
    type2Validate.get(q.getType()).check(q); // no exception is good
  }
}
