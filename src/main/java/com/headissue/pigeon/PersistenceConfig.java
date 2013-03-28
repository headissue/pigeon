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

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

/**
 * JPA configuration. The abstract method "getUnitName()" returns the persistence unit. Additional
 * properties can be set to extend or override the configuration.
 *
 * <pre>
 *   public class SarahSetup extends PigeonModule {
 *    {
 *      setPersistenceConfig(new PersistenceConfig() {
 *        {
 *          // driver is configured in persistence.xml
 *          // addDriver("org.postgresql.Driver");
 *          addUser(System.getProperty("user.name"));
 *          addUrl("jdbc:postgresql://localhost/pigeon");
 *          addPassword("xy");
 *          addProperty("eclipselink.ddl-generation", "create-tables");
 *        }
 *        @Override
 *        public String getUnitName() {
 *          return "pigeon-webapp";
 *        }
 *      });
 *    }
 *  }
 * </pre>
 */
public abstract class PersistenceConfig {

  final Map<String, String> properties = new HashMap<String, String>();

  public abstract String getUnitName();

  public Map<String, String> getProperties() {
    return properties;
  }

  public void addUser(String _user) {
    addProperty(USER, _user);
  }

  public void addUrl(String url) {
    addProperty(URL, url);
  }

  public  void addDriver(String _driver) {
    addProperty(DRIVER, _driver);
  }

  public void addPassword(String _password) {
    addProperty(PASSWORD, _password);
  }

  public void addProperty(String name, String value) {
    properties.put(name, value);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("PersistenceConfig");
    sb.append("{name=").append(getUnitName());
    sb.append(", properties=").append(properties);
    sb.append('}');
    return sb.toString();
  }

  /**
   * Initailized the database befor using the database
   */
  public void initializeDatabase(EntityManager _manager) {
  }

  private static final String DRIVER = "javax.persistence.jdbc.driver";
  private static final String URL = "javax.persistence.jdbc.url";
  private static final String USER = "javax.persistence.jdbc.user";
  private static final String PASSWORD = "javax.persistence.jdbc.password";
}
