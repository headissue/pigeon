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
package com.headissue.pigeon.setup;

import com.headissue.pigeon.PersistenceConfig;
import com.headissue.pigeon.PigeonModule;

/**
 * Setup:
 *
 * - Persistence configuration
 */
public class DeveloperSetup extends PigeonModule {
  {
    setPersistenceConfig(new PersistenceConfig() {
      {
        // drive is configured in persistence.xml
        // addDriver("org.postgresql.Driver");
        addUser(System.getProperty("user.name"));
        addUrl("jdbc:postgresql://localhost/pigeon");
        addPassword("xy");
      }
      @Override
      public String getUnitName() {
        return "pigeon-webapp";
      }
    });
  }
}
