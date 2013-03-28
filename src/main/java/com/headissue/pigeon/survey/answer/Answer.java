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

import com.headissue.pigeon.survey.Question;
import com.headissue.pigeon.survey.Survey;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pigeon_answer")
@NamedQueries({
  @NamedQuery(
    name = "answer.findAnswerListBySurvey",
    query = "SELECT a FROM Answer a WHERE a.survey = :survey ORDER BY a.timestamp DESC"
  ),
  @NamedQuery(
    name = "answer.findAnswerBySurveyQuestionAndUserMap",
    query = "SELECT a FROM Answer a " +
      " WHERE a.survey = :survey AND a.question = :question AND a.userMap = :userMap"
  ),
  @NamedQuery(
    name = "answer.deleteAnswerByQuestion",
    query = "DELETE FROM Answer a WHERE a.question = :question"
  )
})
public class Answer {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pigeon_sequence")
  @Column(name = "answer_id")
  private int id;

  @ManyToOne
  @JoinColumn(name = "survey_id", nullable = false)
  private Survey survey;

  @ManyToOne(cascade = {CascadeType.ALL})
  @JoinColumn(name = "question_id", nullable = false)
  private Question question;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "map_id", nullable = false)
  private UserMap userMap;

  @Temporal(TemporalType.TIMESTAMP)
  private Date timestamp;

  @ElementCollection
  @CollectionTable(
    name = "pigeon_answer_value",
    joinColumns = {@JoinColumn(name = "answer_id")}
  )
  @Column(name = "question_text_id")
  private List<Integer> answerValues = new ArrayList<Integer>();

  @ElementCollection
  @CollectionTable(
    name = "pigeon_answer_text",
    joinColumns = {@JoinColumn(name = "answer_id")}
  )
  @Column(name = "text", columnDefinition = "VARCHAR")
  private List<String> answerTexts = new ArrayList<String>();

  public Answer() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public List<Integer> getAnswerValues() {
    return answerValues;
  }

  public void setAnswerValues(List<Integer> answerValues) {
    this.answerValues = answerValues;
  }

  public List<String> getAnswerTexts() {
    return answerTexts;
  }

  public void setAnswerTexts(List<String> answerTexts) {
    this.answerTexts = answerTexts;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Answer");
    sb.append("{id=").append(id);
    sb.append(", question=").append(question);
    sb.append(", userMap=").append(userMap);
    sb.append(", timestamp=").append(timestamp);
    sb.append(", answerValues=").append(answerValues);
    sb.append(", answerTexts=").append(answerTexts);
    sb.append('}');
    return sb.toString();
  }
}
