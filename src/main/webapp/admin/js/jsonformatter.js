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

/**
 * Manual formatter taken straight from https://github.com/umbrae/jsonlintdotcom
 **/

/*jslint white: true, devel: true, onevar: true, browser: true, undef: true, nomen: true, regexp: true, plusplus: false, bitwise: true, newcap: true, maxerr: 50, indent: 4 */

/**
 * jsl.format - Provide json reformatting in a character-by-character approach, so that even invalid JSON may be reformatted (to the best of its ability).
 *
 **/
var formatter = (function () {

  function repeat(s, count) {
    return new Array(count + 1).join(s);
  }

  function formatJson(json, indentChars) {
    var i           = 0,
      il          = 0,
      tab         = (typeof indentChars !== "undefined") ? indentChars : "    ",
      newJson     = "",
      indentLevel = 0,
      inString    = false,
      currentChar = null;

    for (i = 0, il = json.length; i < il; i += 1) {
      currentChar = json.charAt(i);

      switch (currentChar) {
        case '{':
        case '[':
          if (!inString) {
            newJson += currentChar + "\n" + repeat(tab, indentLevel + 1);
            indentLevel += 1;
          } else {
            newJson += currentChar;
          }
          break;
        case '}':
        case ']':
          if (!inString) {
            indentLevel -= 1;
            newJson += "\n" + repeat(tab, indentLevel) + currentChar;
          } else {
            newJson += currentChar;
          }
          break;
        case ',':
          if (!inString) {
            newJson += ",\n" + repeat(tab, indentLevel);
          } else {
            newJson += currentChar;
          }
          break;
        case ':':
          if (!inString) {
            newJson += ": ";
          } else {
            newJson += currentChar;
          }
          break;
        case ' ':
        case "\n":
        case "\t":
          if (inString) {
            newJson += currentChar;
          }
          break;
        case '"':
          if (i > 0 && json.charAt(i - 1) !== '\\') {
            inString = !inString;
          }
          newJson += currentChar;
          break;
        default:
          newJson += currentChar;
          break;
      }
    }

    return newJson;
  }

  return { "formatJson": formatJson };

}());

if (typeof require !== 'undefined' && typeof exports !== 'undefined') {
  exports.formatter = formatter;
}