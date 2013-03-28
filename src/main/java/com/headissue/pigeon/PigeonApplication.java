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
package com.headissue.pigeon;

import com.headissue.pigeon.admin.AdminAnswerResource;
import com.headissue.pigeon.admin.AdminSurveyResource;
import com.headissue.pigeon.survey.SurveyResource;
import com.headissue.pigeon.survey.answer.AnswerResource;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class PigeonApplication extends Application {

  private final Set<Class<?>> classes = new HashSet<Class<?>>();

  public PigeonApplication() {
    classes.add(HelloResource.class);
    classes.add(SurveyResource.class);
    classes.add(AnswerResource.class);
    classes.add(AdminSurveyResource.class);
    classes.add(AdminAnswerResource.class);
  }

  @Override
  public Set<Class<?>> getClasses() {
    return classes;
  }
}
