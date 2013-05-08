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

--  --
-- VVK (Test) Survey
--


INSERT INTO pigeon_survey VALUES(100,
  '2012-12-12 14:00:00',
  'VVK',
  'ENABLED',
  '2012-12-12 14:00:00',
  'test');
-- Question 1
INSERT INTO pigeon_question VALUES (101,
  1,
  'Dies ist eine Ja-Nein-Frage:',
  'allgemein',
  'BOOL',
  100);
INSERT INTO pigeon_question_text VALUES
  (102, 1, 'Ja', 101),
  (103, 2, 'Nein', 101);

-- Question 1
INSERT INTO pigeon_question VALUES (104,
  2,
  'Suchen Sie sich eine Zahl aus:',
  'allgemein',
  'CHOICE',
  100);
INSERT INTO pigeon_question_text VALUES
    (105, 1, 'Eins', 104),
    (106, 2, 'Zwei', 104),
    (107, 3, 'Drei', 104),
    (108, 4, 'Vier', 104),
    (109, 5, 'Fünf', 104),
    (110, 5, 'Sechs', 104)
;

-- Question 3
INSERT INTO pigeon_question VALUES (111,
    3,
    'Kreuzen Sie Zutreffendes an.',
    'allgemein',
    'MULTIPLE',
    100);
INSERT INTO pigeon_question_text VALUES
  (112, 1, 'Ich wünsche mir mehr Optionen', 111),
  (113, 2, 'Ich wünsche mir einen Share-Button', 111),
  (114, 3, 'Ich wünsche mir einen Drucken-Button', 111),
  (115, 4, 'Was ist das denn? Amnesiestaub! Fantastisch!', 111),
  (116, 5, 'Ich bin vollends zufrieden', 111)
;

-- Question 4
INSERT INTO pigeon_question VALUES (117,
    3,
    'Ihre Lieblingsfarbe?',
    'allgemein',
    'FREE',
    100);

