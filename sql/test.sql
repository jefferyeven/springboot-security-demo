/*
Navicat MySQL Data Transfer

Source Server         : test
Source Server Version : 50730
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50730
File Encoding         : 65001

Date: 2022-04-05 16:12:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `authorities`
-- ----------------------------
DROP TABLE IF EXISTS `authorities`;
CREATE TABLE `authorities` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `roleid` bigint(20) NOT NULL,
  `userid` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ix_auth_username` (`roleid`,`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of authorities
-- ----------------------------
INSERT INTO `authorities` VALUES ('2', '1', '2');
INSERT INTO `authorities` VALUES ('3', '1', '3');
INSERT INTO `authorities` VALUES ('1', '2', '1');

-- ----------------------------
-- Table structure for `persistent_logins`
-- ----------------------------
DROP TABLE IF EXISTS `persistent_logins`;
CREATE TABLE `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of persistent_logins
-- ----------------------------

-- ----------------------------
-- Table structure for `roles`
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) DEFAULT NULL,
  `describe` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES ('1', 'ROLE_admin', '管理员');
INSERT INTO `roles` VALUES ('2', 'ROLE_user', '普通用户');

-- ----------------------------
-- Table structure for `role_permission`
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `roleid` bigint(20) DEFAULT NULL,
  `url` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES ('1', '1', '/sql/test');
INSERT INTO `role_permission` VALUES ('2', '2', '/sql/test');
INSERT INTO `role_permission` VALUES ('3', '2', '/sql/testUser');
INSERT INTO `role_permission` VALUES ('4', '2', '/**');
INSERT INTO `role_permission` VALUES ('5', '2', '/*');

-- ----------------------------
-- Table structure for `users`
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(500) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('1', 'javaboy', '$2a$10$yAdfuJmYi3eSSHU71DFjoeOtzMFEmy/Z.hSXiC1Zu6LLoE70DoRIm', '1');
INSERT INTO `users` VALUES ('2', 'admin', '$2a$10$vD67VBUe58J0RNROfYXm2uMb2Xg7YQ1/WjKfjyU1JCA3Mla3rQhZ2', '1');
INSERT INTO `users` VALUES ('3', 'test', '$2a$10$9kxNdhtwQm9DB5chTnrDWeXfY14rgGOLzAnwtAOr/8J2xOl1S9N2G', '1');
