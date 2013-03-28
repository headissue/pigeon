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

import com.headissue.pigeon.survey.Question;
import com.headissue.pigeon.survey.QuestionText;
import com.headissue.pigeon.survey.Survey;
import com.headissue.pigeon.survey.answer.Answer;
import com.headissue.pigeon.survey.answer.UserMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class to support the survey export.
 * Used in {@link AdminAnswerHandler#getAnswerListFromSurvey(int)}
 */
public class AdminAnswer {

  private Answer answer;

  private Survey survey;

  private Question question;

  private UserMap userMap;

  private final List<QuestionText> answers = new ArrayList<QuestionText>();

  private final List<String> texts = new ArrayList<String>();

  public AdminAnswer() {
  }

  public Answer getAnswer() {
    return answer;
  }

  public void setAnswer(Answer answer) {
    this.answer = answer;
  }

  public Survey getSurvey() {
    return survey;
  }

  public void setSurvey(Survey survey) {
    this.survey = survey;
  }

  public Question getQuestion() {
    return question;
  }

  public void setQuestion(Question question) {
    this.question = question;
  }

  public UserMap getUserMap() {
    return userMap;
  }

  public void setUserMap(UserMap userMap) {
    this.userMap = userMap;
  }

  public List<QuestionText> getAnswers() {
    Collections.sort(answers, qt);
    return Collections.unmodifiableList(answers);
  }

  public void setAnswers(List<QuestionText> answers) {
    this.answers.clear();
    if (answers != null && !answers.isEmpty()) {
      this.answers.addAll(answers);
    }
  }

  public List<String> getTexts() {
    return Collections.unmodifiableList(texts);
  }

  public void setTexts(List<String> texts) {
    this.texts.clear();
    if (texts != null && !texts.isEmpty()) {
      this.texts.addAll(texts);
    }
  }

  void addAnswer(QuestionText answer) {
    answers.add(answer);
  }

  void addText(String text) {
    texts.add(text);
  }

  static final Comparator<QuestionText> qt = new Comparator<QuestionText>() {
    @Override
    public int compare(QuestionText o1, QuestionText o2) {
      if (o1.getOrderBy() > o2.getOrderBy()) {
        return 1;
      } else if (o2.getOrderBy() > o1.getOrderBy()) {
        return -1;
      }
      return 0;
    }
  };
}
