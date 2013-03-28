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
package com.headissue.pigeon.admin.io;

import com.headissue.pigeon.survey.QuestionText;
import com.headissue.pigeon.admin.AdminAnswer;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;

public class CsvOutputWriter extends OutputWriter {

  private static DateTimeFormatter dateTimeFormatter =
    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

  static final String NEW_LINE = "\r\n";

  static final String SEPARATOR = ";";

  public CsvOutputWriter(Writer _writer) {
    super(_writer);
  }

  @Override
  protected void writeHeader(AdminAnswer _answer) throws IOException {
    writeText("Id");
    writeText("Timestamp");
    writeText("Survey Id");
    writeText("Survey");
    writeText("Page Context");
    writeText("User Context");
    writeText("User Data");
    writeText("Question Id");
    writeText("Type");
    writeText("Question Title");
    writeText("Question");
    for (int i = 0; i < 5; i++) {
      writeText("Answer Id");
      writeText("Answer");
    }
    writeNewLine();
  }

  @Override
  protected void writeBody(AdminAnswer _answer) throws IOException {
    writeNumber(_answer.getAnswer().getId());
    writeDate(_answer.getAnswer().getTimestamp());
    writeNumber(_answer.getSurvey().getId());
    writeText(_answer.getSurvey().getName());
    writeText(_answer.getUserMap().getPageKey());
    writeText(_answer.getUserMap().getUserKey());
    writeText(_answer.getUserMap().getUserData());
    writeNumber(_answer.getQuestion().getId());
    writeText(_answer.getQuestion().getType().toString());
    writeText(_answer.getQuestion().getTitle());
    writeText(_answer.getQuestion().getText());
    List<QuestionText> _items = _answer.getAnswers();
    for (QuestionText qt : _items) {
      writeNumber(qt.getId());
      writeText(qt.getText());
    }
    for (String _text : _answer.getTexts()) {
      writeText("-");
      writeText(_text);
    }
    writeNewLine();
  }


  void writeText(String _text) throws IOException {
    writer.append('"').append(_text.replaceAll("\"", "\"\"")).append('"').append(SEPARATOR);
  }

  void writeDate(Date _date) throws IOException {
    if (_date == null) {
      writeText("");
      return;
    }
    String _text = dateTimeFormatter.print(_date.getTime());
    writeText(_text);
  }

  void writeNumber(int id) throws IOException {
    writeText(String.valueOf(id));
  }

  void writeNewLine() throws IOException {
    writer.append(NEW_LINE);
  }

}
