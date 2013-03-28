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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * the whole information sent by the client in order to answer a question
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAnswerValues {

  // Will never be used
  @XmlElement(name = "survey_id")
  private int dummyId;

  @XmlElement(name = "page_key")
  private String pageKey;

  @XmlElement(name = "user_key")
  private String userKey;

  @XmlElement(name = "user_data")
  private String userData;

  private List<UserAnswerValue> answers;

  public UserAnswerValues() {
  }

  public String getPageKey() {
    return pageKey;
  }

  public void setPageKey(String pageKey) {
    this.pageKey = pageKey;
  }

  public String getUserKey() {
    return userKey;
  }

  public void setUserKey(String userKey) {
    this.userKey = userKey;
  }

  public String getUserData() {
    return userData;
  }

  public void setUserData(String userData) {
    this.userData = userData;
  }

  public List<UserAnswerValue> getAnswers() {
    return answers;
  }

  public void setAnswers(List<UserAnswerValue> answers) {
    this.answers = answers;
  }
}
