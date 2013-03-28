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

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.headissue.pigeon.service.AdminQuestionValidator;
import com.headissue.pigeon.service.AnswerTransformFactory;
import com.headissue.pigeon.service.QuestionAnswerTransformFactory;
import com.headissue.pigeon.admin.AdminAnswerHandler;
import com.headissue.pigeon.admin.AdminAnswerResource;
import com.headissue.pigeon.admin.AdminOutputFactory;
import com.headissue.pigeon.admin.AdminSurveyHandler;
import com.headissue.pigeon.admin.AdminSurveyResource;
import com.headissue.pigeon.admin.io.OutputFactory;
import com.headissue.pigeon.service.QuestionValidator;
import com.headissue.pigeon.survey.answer.AnswerHandler;
import com.headissue.pigeon.survey.answer.AnswerResource;
import com.headissue.pigeon.service.ResponseService;
import com.headissue.pigeon.survey.SurveyResource;
import com.headissue.pigeon.survey.SurveyHandler;
import com.sun.jersey.guice.JerseyServletModule;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * Guice module for configuration. Must be overridden, as no persistence parameters are present.
 * For example, {@link com.headissue.pigeon.setup.DeveloperSetup}.
 *
 * Instantiation is using the parameter "com.headissue.pigeon.guice.Module". Either set in the
 * "web.xml" as "context-param", or as environment variable "com_headissue_pigeon_guice_Module".
 *
 * @see PigeonBootstrapListener
 */
public class PigeonModule extends JerseyServletModule {

  private PersistenceConfig persistenceConfig = null;

  @Override
  protected void configureServlets() {
    Map<String, String> initParams = new HashMap<String, String>();
    initParams.put("javax.ws.rs.Application", PigeonApplication.class.getName());
    initParams.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
    serve("/api/*").with(PigeonContainer.class, initParams);

    bind(OutputFactory.class).to(AdminOutputFactory.class);
    bind(AnswerTransformFactory.class).to(QuestionAnswerTransformFactory.class);
    bind(QuestionValidator.class).to(AdminQuestionValidator.class);
  }

  @Provides
  @Singleton
  public EntityManagerFactory providerEntityManager() {
    PersistenceConfig rc = getPersistenceConfig();
    if (rc == null) {
      throw new PigeonException("missing the persistence configuration");
    }
    EntityManagerFactory factory = Persistence.createEntityManagerFactory(rc.getUnitName(),
      rc.getProperties());
    try {
      EntityManager _manager = factory.createEntityManager();
      try {
        rc.initializeDatabase(_manager);
      } finally {
        _manager.close();
      }
    } catch (Exception e) {
      throw new PigeonException("initializing of database is failed", e);
    }
    return factory;
  }

  public PersistenceConfig getPersistenceConfig() {
    return persistenceConfig;
  }

  public void setPersistenceConfig(PersistenceConfig persistenceConfig) {
    this.persistenceConfig = persistenceConfig;
  }
}
