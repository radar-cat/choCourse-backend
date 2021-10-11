/*
 Navicat MySQL Data Transfer

 Source Server         : index_db
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : chocourse

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 09/10/2021 15:09:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '课程id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '课程名称',
  `counts` double NULL DEFAULT NULL COMMENT '课程学分',
  `teacher` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '授课教师',
  `details` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '课程详情',
  `start_date` datetime NULL DEFAULT NULL COMMENT '选课开始时间',
  `end_date` datetime NULL DEFAULT NULL COMMENT '选课结束时间',
  `version` int NULL DEFAULT NULL COMMENT '并发版本控制',
  `stock` int NULL DEFAULT NULL COMMENT '课程余量',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '课程信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, '水母捕捉课', 4, '派大星', '时间：每周三上午10点-12点 地点：水母田', '2021-10-01 15:00:00', '2221-10-01 17:00:00', 1, 49);
INSERT INTO `course` VALUES (2, '发呆课', 2, '派大星', '时间：每周三下午10点-12点 地点：家里', '2021-10-01 15:00:00', '2221-10-01 17:00:00', 0, 50);
INSERT INTO `course` VALUES (3, '加油课', 8, '海绵宝宝', '时间：任何时间 地点：任何地点', '2021-10-01 15:00:00', '2221-10-01 17:00:00', 1, 49);
INSERT INTO `course` VALUES (6, '省钱课', 1, '蟹老板', '以后有了钱就不用上了！！！', '2021-10-01 15:00:00', '2221-10-01 17:00:00', 0, 50);

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `course_id` int NULL DEFAULT NULL COMMENT '课程id',
  `course_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '课程名称',
  `create_date` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `course_id`(`course_id`) USING BTREE,
  INDEX `course_name`(`course_name`) USING BTREE,
  CONSTRAINT `order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `order_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `order_ibfk_3` FOREIGN KEY (`course_name`) REFERENCES `course` (`name`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '选课订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (3, 2020001000, 3, '加油课', '2021-10-06 22:06:29');
INSERT INTO `order` VALUES (5, 2020001000, 1, '水母捕捉课', '2021-10-08 15:12:59');

-- ----------------------------
-- Table structure for order_map
-- ----------------------------
DROP TABLE IF EXISTS `order_map`;
CREATE TABLE `order_map`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id编号',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `order_id` int NULL DEFAULT NULL COMMENT '订单id',
  `course_id` int NULL DEFAULT NULL COMMENT '课程id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `course_id`(`course_id`) USING BTREE,
  CONSTRAINT `order_map_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `order_map_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户信息-订单映射表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_map
-- ----------------------------
INSERT INTO `order_map` VALUES (3, 2020001000, 3, 3);
INSERT INTO `order_map` VALUES (5, 2020001000, 5, 1);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL COMMENT '用户id（学号）',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `salt` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '加密混淆盐',
  `register_date` datetime NULL DEFAULT NULL COMMENT '注册时间',
  `last_login_date` datetime NULL DEFAULT NULL COMMENT '最近登录时间',
  `login_count` int NULL DEFAULT NULL COMMENT '登录次数',
  `course_limit` int NULL DEFAULT NULL COMMENT '限选课程门数',
  `role` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户角色',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (2020001000, 'admin', 'd3b1294a61a07da9b49b6e22b2cbd7f9', '1a2b3c4d', NULL, NULL, NULL, 2, 'admin');
INSERT INTO `user` VALUES (2020001001, 'user', 'd3b1294a61a07da9b49b6e22b2cbd7f9', '1a2b3c4d', NULL, NULL, NULL, 2, 'user');

SET FOREIGN_KEY_CHECKS = 1;
