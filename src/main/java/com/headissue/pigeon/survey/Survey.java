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
package com.headissue.pigeon.survey;


import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pigeon_survey")
@SequenceGenerator(name = "pigeon_sequence", sequenceName = "pigeon_sequence")
@NamedQueries({
  @NamedQuery(name = "survey.findSurveyById",
    query = "SELECT s FROM Survey s WHERE s.id = :surveyId"),
  @NamedQuery(name = "survey.allSurveyOverview",
    query = "SELECT new com.headissue.pigeon.admin.SurveyOverviewValue(s.id, s.name, s.createAt," +
      "s.updateAt) FROM Survey s WHERE s.status = :status"),
  @NamedQuery(name = "survey.findSurveyByKey",
    query = "SELECT s FROM Survey  s WHERE s.key = :surveyKey")
})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "name", "questions"})
public class Survey {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pigeon_sequence")
  @Column(name = "survey_id")
  private int id;

  @Column(name = "survey_key", unique = true)
  private String key;

  @Column(name = "name", nullable = false, columnDefinition = "VARCHAR")
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  @XmlTransient
  private SurveyStatus status;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_at", nullable = false)
  @XmlTransient
  private Date createAt;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "update_at", nullable = false)
  @XmlTransient
  private Date updateAt;

  @OneToMany(mappedBy = "survey", cascade = {CascadeType.PERSIST}, orphanRemoval = true)
  @OrderBy("orderBy ASC")
  private List<Question> questions = new ArrayList<Question>();

  public Survey() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SurveyStatus getStatus() {
    return status;
  }

  public void setStatus(SurveyStatus status) {
    this.status = status;
  }

  public Date getCreateAt() {
    return createAt;
  }

  public void setCreateAt(Date createAt) {
    this.createAt = createAt;
  }

  public Date getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(Date updateAt) {
    this.updateAt = updateAt;
  }

  public List<Question> getQuestions() {
    return questions;
  }

  public void setQuestions(List<Question> questions) {
    this.questions = questions;
  }

  public void addQuestion(Question q) {
    addQuestion(q, 0);
  }

  public void addQuestion(Question q, int _orderBy) {
    questions.add(q);
    q.setSurvey(this);
    if (_orderBy > 0 && q.getOrderBy() <= 0) {
      q.setOrderBy(_orderBy);
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Survey");
    sb.append("{id=").append(id);
    sb.append(", name='").append(name).append('\'');
    sb.append(", status=").append(status);
    sb.append(", createAt=").append(createAt);
    sb.append(", updateAt=").append(updateAt);
    sb.append(", questions=[").append(questions != null ? questions.size() : -1).append("]");
    sb.append('}');
    return sb.toString();
  }
}
