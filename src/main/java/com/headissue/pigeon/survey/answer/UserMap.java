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

import com.headissue.pigeon.survey.Survey;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "pigeon_answer_user_map")
@NamedQueries({
  @NamedQuery(name = "answerUserMan.findBySurveyAndId",
    query = "SELECT us FROM UserMap us WHERE us.survey = :survey AND us.id = :mapId"
  )
})
public class UserMap {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pigeon_sequence")
  @Column(name = "map_id")
  private int id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "survey_id", nullable = false, columnDefinition = "VARCHAR")
  private Survey survey;

  @Column(name = "page_key", nullable = false, columnDefinition = "VARCHAR")
  private String pageKey;

  @Column(name = "user_key", nullable = false, columnDefinition = "VARCHAR")
  private String userKey;

  @Column(name = "user_data", nullable = true)
  private String userData;

  @Temporal(TemporalType.TIMESTAMP)
  private Date timestamp;

  public UserMap() {
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

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserMap)) return false;

    UserMap userMap = (UserMap) o;

    if (id != userMap.id) return false;
    if (pageKey != null ? !pageKey.equals(userMap.pageKey) : userMap.pageKey != null) return false;
    if (!survey.equals(userMap.survey)) return false;
    if (timestamp != null ? !timestamp.equals(userMap.timestamp) : userMap.timestamp != null)
      return false;
    if (userData != null ? !userData.equals(userMap.userData) : userMap.userData != null)
      return false;
    if (userKey != null ? !userKey.equals(userMap.userKey) : userMap.userKey != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + survey.hashCode();
    result = 31 * result + (pageKey != null ? pageKey.hashCode() : 0);
    result = 31 * result + (userKey != null ? userKey.hashCode() : 0);
    result = 31 * result + (userData != null ? userData.hashCode() : 0);
    result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("UserMap");
    sb.append("{id=").append(id);
    sb.append(", survey=[").append(survey != null ? survey.getId() : -1).append("]");
    sb.append(", pageKey='").append(pageKey).append('\'');
    sb.append(", userKey='").append(userKey).append('\'');
    sb.append(", userData='").append(userData).append('\'');
    sb.append(", timestamp='").append(timestamp).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
