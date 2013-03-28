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
package com.headissue.pigeon.service;

import com.google.inject.Singleton;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

@Singleton
public class CallbackNameVerifier {
  /*
  * Validating JavaScript method names is more complicated than it would seem.
  * See http://stackoverflow.com/questions/2008279/validate-a-javascript-function-name
  */
  private static final Pattern METHOD_NAME_PATTERN =
    Pattern.compile("^[_$a-zA-Z\\xA0-\\uFFFF][_$a-zA-Z0-9\\xA0-\\uFFFF]*$");

  /**
   * Verifies that the _callbackName is not empty and that it is a valid ECMAScript identifier.
   * @param _callbackName Name of the JavaScript function
   * @return {true}, if the given name is valid
   */
  public boolean verifyName(String _callbackName) {
    return StringUtils.isEmpty(_callbackName) || METHOD_NAME_PATTERN.matcher(_callbackName).matches();
  }
}
