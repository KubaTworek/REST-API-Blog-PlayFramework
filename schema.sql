CREATE DATABASE IF NOT EXISTS `blog-db`;
USE `blog-db`;

CREATE TABLE IF NOT EXISTS `User` (
                          `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          `firstName` varchar(20) NOT NULL,
                          `lastName` varchar(20) NOT NULL
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Post` (
                           `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
                           `title` varchar(50) NOT NULL,
                           `text` LONGTEXT NOT NULL,
                           `user_id` int NOT NULL,
                           FOREIGN KEY(`user_id`) REFERENCES `User`(`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `Comment` (
                            `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
                            `content` LONGTEXT NOT NULL,
                            `post_id` int NOT NULL,
                            FOREIGN KEY(`post_id`) REFERENCES `Post`(`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `PostComment` (
                           `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
                           `post_id` int NOT NULL,
                           `comment_id` int NOT NULL,
                           FOREIGN KEY(`post_id`) REFERENCES `Post`(`id`),
                           FOREIGN KEY(`comment_id`) REFERENCES `Comment`(`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;