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

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author tobi
 */
public class CallbackNameVerifierTest {
  static CallbackNameVerifier VERIFIER = new CallbackNameVerifier();

  /**
   * A javascript name starts with a letter, a $ or an underscore and can be followed by these letters, plus numbers,
   * accent and some unicde characters.
   */
  @Test
  public void testVerifyNameTrue() {
    String _valid = "$";
    assertTrue(VERIFIER.verifyName(_valid));
    _valid = "_";
    assertTrue(VERIFIER.verifyName(_valid));
    _valid = "abc";
    assertTrue(VERIFIER.verifyName(_valid));
    _valid = "ಠ_ಠ"; // See http://mothereff.in/js-variables
    assertTrue(VERIFIER.verifyName(_valid));
    // test all
    _valid = "abc$ಠ_ಠÄ"; //
    assertTrue(VERIFIER.verifyName(_valid));
  }

  @Test
  public void testVerifyNameFalse() {
    // does not start with $,_ or a letter
    String _invalid = "!";
    assertFalse(VERIFIER.verifyName(_invalid));
    // ! is not allowed in a method name
    _invalid = "abc!";
    assertFalse(VERIFIER.verifyName(_invalid));
    // does not start with $,_ or a letter either
    _invalid = "<script";
    assertFalse(VERIFIER.verifyName(_invalid));

  }
}
