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

import com.headissue.pigeon.service.JacksonService;
import com.headissue.pigeon.service.Adapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * used in  {@link AdminSurveyHandler#getAllSurveys()}
 * @see com.headissue.pigeon.survey.Survey
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SurveyOverviewValue {

  private int id;

  private String name;

  @XmlJavaTypeAdapter(Adapter.DateTimeXmlAdapter.class)
  private Date createAt;

  @XmlJavaTypeAdapter(Adapter.DateTimeXmlAdapter.class)
  private Date updateAt;

  public SurveyOverviewValue() {
  }

  public SurveyOverviewValue(int id, String name, Date createAt, Date updateAt) {
    this.id = id;
    this.name = name;
    this.createAt = createAt;
    this.updateAt = updateAt;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
}
