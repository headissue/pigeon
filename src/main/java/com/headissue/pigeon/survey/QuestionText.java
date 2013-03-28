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


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name = "pigeon_question_text")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "text"})
public class QuestionText {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pigeon_sequence")
  @Column(name = "answer_id")
  private int id;

  @Column(name = "order_by", nullable = false)
  @XmlTransient
  private int orderBy;

  @Column(name = "text", length = 1024, columnDefinition = "VARCHAR")
  private String text;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
  @JoinColumn(name = "question_id", nullable = false)
  @XmlTransient
  private Question question;

  public QuestionText() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(int orderBy) {
    this.orderBy = orderBy;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Question getQuestion() {
    return question;
  }

  public void setQuestion(Question question) {
    this.question = question;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("QuestionText");
    sb.append("{id=").append(id);
    sb.append(", orderBy=").append(orderBy);
    sb.append(", text='").append(text).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
