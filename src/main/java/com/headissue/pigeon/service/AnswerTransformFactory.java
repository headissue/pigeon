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

import com.headissue.pigeon.PigeonException;
import com.headissue.pigeon.survey.answer.Answer;
import com.headissue.pigeon.survey.answer.UserAnswerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * configuration: {@link QuestionAnswerTransformFactory}.
 */
public abstract class AnswerTransformFactory {

  private final Map<String, AnswerTransform> name2Transform =
    new HashMap<String, AnswerTransform>();

  public void add(String _name, AnswerTransform _transform) {
    name2Transform.put(_name, _transform);
  }

  /**
   * Returns the matching instance. If the name does not exist, throw an exception
   */
  public final AnswerTransform get(String _type) {
    _type = _type.toLowerCase();
    if (!name2Transform.containsKey(_type)) {
      throw new PigeonException("Unknown name '" + (_type != null ? _type : "null") + "'");
    }
    return name2Transform.get(_type);
  }

  /**
   * Converts the users answer value to an answer. A verification may happen.
   * Values for type 'CHOICE' have to be numerical.
   * Type "BOOL" must have a single answer.
   */
  static class IntAnswerTransform extends AnswerTransform {

    @Override
    public boolean transfer(Answer _answer, UserAnswerValue _value) {
      try {
        List<Integer> _values = new ArrayList<Integer>();
        for (String _key : _value.getValues()) {
          _values.add(Integer.parseInt(_key));
        }
        _answer.setAnswerValues(_values);
      } catch (Exception e) {
        return false;
      }
      return true;
    }
  }

  /**
   * expects a single answer value
   */
  static class OneKeyAnswerTransform extends IntAnswerTransform {
    @Override
    public boolean transfer(Answer _answer, UserAnswerValue _value) {
      if (_value.getValues().size() != 1) {
        return false;
      }
      return super.transfer(_answer, _value);
    }
  }

  /**
   * Expects the answer value to be a string
   */
  static class OneTextAnswerTransform extends AnswerTransform {

    @Override
    public boolean transfer(Answer _answer, UserAnswerValue _value) {
      if (_value.getValues().size() != 1) {
        return false;
      }
      _answer.setAnswerTexts(new ArrayList<String>(_value.getValues()));
      return true;
    }
  }

  static class MultipleOneFreeAnswerTransform extends AnswerTransform {

    @Override
    public boolean transfer(Answer _answer, UserAnswerValue _value) {
      List<Integer> _values = new ArrayList<Integer>();
      List<String> _texts = new ArrayList<String>();
      int _lastIndex = _value.getValues().size() - 1;
      for (int _index = 0; _index < _value.getValues().size(); _index++) {
        String _key = _value.getValues().get(_index);
        if (_index < _lastIndex) {
          try {
            _values.add(Integer.parseInt(_key));
          } catch (Exception e) {
            return false;
          }
        } else {
          _texts.add(_key);
        }
      }
      _answer.setAnswerValues(_values);
      _answer.setAnswerTexts(_texts);
      return true;
    }
  }
}
