-- Copyright (C) 2013 headissue GmbH (www.headissue.com)
--
-- Source repository: https://github.com/headissue/pigeon
--
-- This program is free software; you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
--
-- This patch is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.
--
-- You should have received a copy of the GNU Affero General Public License
-- along with this patch.  If not, see <http://www.gnu.org/licenses/agpl.txt/>.

--
-- Uninstall pigeon? -> DROP
--
DROP TABLE pigeon_survey;
DROP TABLE pigeon_question;
DROP TABLE pigeon_question_text;
DROP TABLE pigeon_answer;
DROP TABLE pigeon_answer_user_map;
DROP TABLE pigeon_answer_text;
DROP TABLE pigeon_answer_value;
DROP SEQUENCE pigeon_sequence;