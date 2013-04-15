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


CREATE TABLE pigeon_survey (
	survey_id INTEGER NOT NULL,
	create_at TIMESTAMP NOT NULL,
	name VARCHAR,
	status VARCHAR(255) NOT NULL,
	update_at TIMESTAMP NOT NULL,
	PRIMARY KEY (survey_id));
CREATE TABLE pigeon_question (
	question_id INTEGER NOT NULL,
	order_by INTEGER NOT NULL,
	text VARCHAR,
	title VARCHAR,
	type VARCHAR(20),
	survey_id INTEGER NOT NULL,
	PRIMARY KEY (question_id));
CREATE TABLE pigeon_question_text (
	answer_id INTEGER NOT NULL,
	order_by INTEGER NOT NULL,
	text VARCHAR,
	question_id INTEGER NOT NULL,
	PRIMARY KEY (answer_id));
CREATE TABLE pigeon_answer (
	answer_id INTEGER NOT NULL,
	TIMESTAMP TIMESTAMP,
	question_id INTEGER NOT NULL,
	survey_id INTEGER NOT NULL,
	map_id INTEGER NOT NULL,
	PRIMARY KEY (answer_id));
CREATE TABLE pigeon_answer_user_map (map_id INTEGER NOT NULL,
	page_key VARCHAR,
	TIMESTAMP TIMESTAMP,
	user_data VARCHAR(255),
	user_key VARCHAR,
	survey_id INTEGER,
	PRIMARY KEY (map_id));
CREATE TABLE pigeon_answer_text (
	answer_id INTEGER,
	text TEXT);
CREATE TABLE pigeon_answer_value (
	answer_id INTEGER,
	question_text_id INTEGER);

CREATE SEQUENCE pigeon_sequence INCREMENT BY 50 START WITH 500000;

ALTER TABLE pigeon_question ADD CONSTRAINT FK_pigeon_question_survey_id FOREIGN KEY (survey_id) REFERENCES pigeon_survey (survey_id);
ALTER TABLE pigeon_question_text ADD CONSTRAINT FK_pigeon_question_text_question_id FOREIGN KEY (question_id) REFERENCES pigeon_question (question_id);
ALTER TABLE pigeon_answer ADD CONSTRAINT FK_pigeon_answer_survey_id FOREIGN KEY (survey_id) REFERENCES pigeon_survey (survey_id);
ALTER TABLE pigeon_answer ADD CONSTRAINT FK_pigeon_answer_map_id FOREIGN KEY (map_id) REFERENCES pigeon_answer_user_map (map_id);
ALTER TABLE pigeon_answer ADD CONSTRAINT FK_pigeon_answer_question_id FOREIGN KEY (question_id) REFERENCES pigeon_question (question_id);
ALTER TABLE pigeon_answer_user_map ADD CONSTRAINT FK_pigeon_answer_user_map_survey_id FOREIGN KEY (survey_id) REFERENCES pigeon_survey (survey_id);
ALTER TABLE pigeon_answer_text ADD CONSTRAINT FK_pigeon_answer_text_answer_id FOREIGN KEY (answer_id) REFERENCES pigeon_answer (answer_id);
ALTER TABLE pigeon_answer_value ADD CONSTRAINT FK_pigeon_answer_value_answer_id FOREIGN KEY (answer_id) REFERENCES pigeon_answer (answer_id);