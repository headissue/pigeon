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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.headissue.pigeon.util.JPAUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

@Singleton
public class SurveyHandler {

  @Inject
  private EntityManagerFactory factory;

  public Survey findSurveyById(int _surveyId) {
    EntityManager _manager = factory.createEntityManager();
    try {
      Survey s = _manager.find(Survey.class, _surveyId);
      if (s == null || s.getStatus() == SurveyStatus.DISABLED) {
        return null;
      }
      return s;
    } catch (Exception e) {
      throw new SurveyException("survey '" + _surveyId + "' is unknown");
    } finally {
      _manager.close();
    }
  }

  public Survey findSurveyByKey(String _surveyKey) {
    EntityManager _manager = factory.createEntityManager();
    try {
      TypedQuery<Survey> q = _manager.createNamedQuery("survey.findSurveyByKey", Survey.class);
      q.setParameter("surveyKey", _surveyKey);
      Survey s = JPAUtils.getSingleResult(q);
      if (s == null || s.getStatus() == SurveyStatus.DISABLED) {
        return null;
      }
      return s;
    } catch (Exception e) {
      throw new SurveyException("survey '" + _surveyKey + "' is unknown");
    } finally {
      _manager.close();
    }
  }
}
