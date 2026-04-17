/*
 Navicat Premium Dump SQL

 Source Server         : prj
 Source Server Type    : MySQL
 Source Server Version : 80407 (8.4.7)
 Source Host           : localhost:3306
 Source Schema         : hms_db

 Target Server Type    : MySQL
 Target Server Version : 80407 (8.4.7)
 File Encoding         : 65001

 Date: 19/12/2025 21:12:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bed
-- ----------------------------
DROP TABLE IF EXISTS `bed`;
CREATE TABLE `bed`  (
  `bed_id` int NOT NULL AUTO_INCREMENT COMMENT '床位ID',
  `bed_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '床号',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0=空闲, 1=占用, 2=维修',
  `room_id` int NOT NULL COMMENT '所属病房ID',
  PRIMARY KEY (`bed_id`) USING BTREE,
  INDEX `fk_bed_room`(`room_id` ASC) USING BTREE,
  INDEX `idx_bed_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_bed_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`room_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '床位资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bed
-- ----------------------------
INSERT INTO `bed` VALUES (1, '101-1', 1, 1);
INSERT INTO `bed` VALUES (2, '101-2', 1, 1);
INSERT INTO `bed` VALUES (3, '102-1', 0, 2);
INSERT INTO `bed` VALUES (4, '103-1', 0, 3);
INSERT INTO `bed` VALUES (5, '101-3', 0, 1);
INSERT INTO `bed` VALUES (6, '38', 2, 4);
INSERT INTO `bed` VALUES (7, '114', 0, 4);
INSERT INTO `bed` VALUES (8, '2801-1', 0, 5);
INSERT INTO `bed` VALUES (9, '2801-2', 0, 5);
INSERT INTO `bed` VALUES (10, '201-1', 0, 6);

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`  (
  `dept_id` int NOT NULL AUTO_INCREMENT COMMENT '科室ID',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '科室名称',
  `dept_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '科室信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of department
-- ----------------------------
INSERT INTO `department` VALUES (1, '内科', '010-12345678');
INSERT INTO `department` VALUES (2, '外科', '010-87654321');
INSERT INTO `department` VALUES (3, '精神科', '110');
INSERT INTO `department` VALUES (4, '儿科', '1201111');

-- ----------------------------
-- Table structure for patient
-- ----------------------------
DROP TABLE IF EXISTS `patient`;
CREATE TABLE `patient`  (
  `patient_id` int NOT NULL AUTO_INCREMENT COMMENT '病人ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `gender` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'M' COMMENT '性别: M/F',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '身份证号',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1=在院, 0=出院',
  `bed_id` int NULL DEFAULT NULL COMMENT '占用床位ID (1:1关系, 出院置NULL)',
  PRIMARY KEY (`patient_id`) USING BTREE,
  UNIQUE INDEX `id_card`(`id_card` ASC) USING BTREE,
  UNIQUE INDEX `bed_id`(`bed_id` ASC) USING BTREE,
  INDEX `idx_patient_idcard`(`id_card` ASC) USING BTREE,
  CONSTRAINT `fk_patient_bed` FOREIGN KEY (`bed_id`) REFERENCES `bed` (`bed_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '病人信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of patient
-- ----------------------------
INSERT INTO `patient` VALUES (40, '王嘉杰', 'M', 21, '362010200410100011', 1, 1);
INSERT INTO `patient` VALUES (41, '徐泽浩', 'F', 21, '114514200401010033', 1, 2);

-- ----------------------------
-- Table structure for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room`  (
  `room_id` int NOT NULL AUTO_INCREMENT COMMENT '病房ID',
  `room_number` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房号',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型: 单人间/双人间/ICU',
  `dept_id` int NOT NULL COMMENT '所属科室ID',
  PRIMARY KEY (`room_id`) USING BTREE,
  INDEX `idx_room_dept`(`dept_id` ASC) USING BTREE,
  CONSTRAINT `fk_room_dept` FOREIGN KEY (`dept_id`) REFERENCES `department` (`dept_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '病房信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room
-- ----------------------------
INSERT INTO `room` VALUES (1, '101', '普通', 1);
INSERT INTO `room` VALUES (2, '102', 'ICU', 2);
INSERT INTO `room` VALUES (3, '103', '单人间', 1);
INSERT INTO `room` VALUES (4, '520', 'ICU', 1);
INSERT INTO `room` VALUES (5, '2801', 'ICU', 3);
INSERT INTO `room` VALUES (6, '201', 'ICU', 3);

-- ----------------------------
-- Table structure for sysuser
-- ----------------------------
DROP TABLE IF EXISTS `sysuser`;
CREATE TABLE `sysuser`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `role` tinyint NOT NULL COMMENT '角色: 1=管理员, 2=医生, 3=护士',
  `dept_id` int NULL DEFAULT NULL COMMENT '所属科室ID',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `fk_user_dept`(`dept_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_dept` FOREIGN KEY (`dept_id`) REFERENCES `department` (`dept_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sysuser
-- ----------------------------
INSERT INTO `sysuser` VALUES (1, 'admin', '123456', '系统管理员', 1, 1);
INSERT INTO `sysuser` VALUES (2, 'doctor', '123456', '李医生', 2, 2);
INSERT INTO `sysuser` VALUES (3, 'nurse', '123456', '王护士', 3, 2);
INSERT INTO `sysuser` VALUES (4, 'alter', 'abc123', '泠', 1, 3);

-- ----------------------------
-- View structure for vw_patient_bed_info
-- ----------------------------
DROP VIEW IF EXISTS `vw_patient_bed_info`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `vw_patient_bed_info` AS select `p`.`patient_id` AS `patient_id`,`p`.`name` AS `patient_name`,`p`.`id_card` AS `id_card`,`d`.`dept_name` AS `dept_name`,`r`.`room_number` AS `room_number`,`b`.`bed_number` AS `bed_number` from (((`patient` `p` join `bed` `b` on((`p`.`bed_id` = `b`.`bed_id`))) join `room` `r` on((`b`.`room_id` = `r`.`room_id`))) join `department` `d` on((`r`.`dept_id` = `d`.`dept_id`))) where (`p`.`status` = 1);

-- ----------------------------
-- Procedure structure for proc_admit_patient
-- ----------------------------
DROP PROCEDURE IF EXISTS `proc_admit_patient`;
delimiter ;;
CREATE PROCEDURE `proc_admit_patient`(IN p_name VARCHAR(50),
    IN p_gender CHAR(1),
    IN p_age INT,
    IN p_id_card VARCHAR(18),
    IN p_bed_id INT,
    OUT p_result VARCHAR(100))
BEGIN
    DECLARE v_bed_status TINYINT;
    
    -- 1. 检查床位状态
    SELECT status INTO v_bed_status FROM Bed WHERE bed_id = p_bed_id;
    
    IF v_bed_status IS NULL THEN
        SET p_result = '错误：床位不存在';
    ELSEIF v_bed_status != 0 THEN
        SET p_result = '错误：该床位已被占用或维修中';
    ELSE
        -- 2. 开始事务
        START TRANSACTION;
        
        -- 更新床位状态为占用 (1)
        UPDATE Bed SET status = 1 WHERE bed_id = p_bed_id;
        
        -- 插入病人记录
        INSERT INTO Patient (name, gender, age, id_card, status, bed_id)
        VALUES (p_name, p_gender, p_age, p_id_card, 1, p_bed_id);
        
        -- 提交事务
        COMMIT;
        SET p_result = '成功：入院办理完成';
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table patient
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_release_bed`;
delimiter ;;
CREATE TRIGGER `trg_release_bed` AFTER UPDATE ON `patient` FOR EACH ROW BEGIN
    -- 检测条件：旧值为非空，新值为 NULL (即执行了出院解绑操作)
    IF OLD.bed_id IS NOT NULL AND NEW.bed_id IS NULL THEN
        UPDATE Bed 
        SET status = 0 
        WHERE bed_id = OLD.bed_id;
    END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
