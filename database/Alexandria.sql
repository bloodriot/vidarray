CREATE TABLE IF NOT EXISTS `files` (
	`id` INTEGER NOT NULL PRIMARY KEY /*!40101 AUTO_INCREMENT */,
	`name` varchar(255),
	`type` varchar(255),
	`duration` INTEGER,
	`date` varchar(255),
	`path` varchar(255),
	`preview` varchar(255),
	CONSTRAINT unique_path UNIQUE (`path`)
);

CREATE TABLE IF NOT EXISTS `file_tags` (
	`id` INTEGER NOT NULL PRIMARY KEY /*!40101 AUTO_INCREMENT */,
	`file_id` INTEGER NOT NULL,
	`tag_id` INTEGER NOT NULL,
	CONSTRAINT unique_tags UNIQUE (`file_id`, `tag_id`)
);

CREATE TABLE IF NOT EXISTS `tags` (
	`id` INTEGER NOT NULL PRIMARY KEY /*!40101 AUTO_INCREMENT */,
	`tag` varchar(255),
	CONSTRAINT unique_tag UNIQUE (`tag`)
);

INSERT INTO `files` (`name`, `type`, `duration`, `date`, `path`, `preview`) VALUES (
	'test.mp4',
	'video/mp4',
	1000,
	'11/21/2018',
	'path/test.mp4',
	'path/test-preview.mp4'
);

INSERT INTO `file_tags` (`file_id`, `tag_id`) VALUES (1, 1);
INSERT INTO `file_tags` (`file_id`, `tag_id`) VALUES (1, 2);
INSERT INTO `file_tags` (`file_id`, `tag_id`) VALUES (1, 3);

INSERT INTO `tags` (`tag`) VALUES ('test');
INSERT INTO `tags` (`tag`) VALUES ('test2');
INSERT INTO `tags` (`tag`) VALUES ('test3');

SELECT `file_id`, `name`, `type`, `duration`, `path`, `preview`, `tag` FROM `files`;
SELECT `tags`.`tag` FROM `tags` LEFT JOIN `file_tags` ON `tags`.`id` = `file_tags`.`tag_id` WHERE `file_tags`.`file_id` = '1';