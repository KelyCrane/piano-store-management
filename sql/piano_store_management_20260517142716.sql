-- MySQL dump 10.13  Distrib 8.0.35, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: piano_store_management
-- ------------------------------------------------------
-- Server version	8.0.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `piano_store_management`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `piano_store_management` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `piano_store_management`;

--
-- Table structure for table `activity`
--

DROP TABLE IF EXISTS `activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '活动名称',
  `type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型：考级/比赛/演出/其他',
  `title` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '活动标题',
  `description` text COLLATE utf8mb4_general_ci COMMENT '描述',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `location` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '地点',
  `max_participants` int DEFAULT '50' COMMENT '人数上限',
  `fee` decimal(10,2) DEFAULT '0.00' COMMENT '报名费',
  `cover_image` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '封面图',
  `registration_condition` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报名条件',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-未开始 1-进行中 2-已结束',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='活动表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity`
--

LOCK TABLES `activity` WRITE;
/*!40000 ALTER TABLE `activity` DISABLE KEYS */;
INSERT INTO `activity` VALUES (1,'2026年钢琴考级春季','考级','2026年春季钢琴考级报名开始','中国音乐家协会2026年春季钢琴考级，考级级别1-10级，请提前报名。考级通过后可获得对应级别证书。','2026-04-15 09:00:00','2026-04-15 17:00:00','本琴行多功能厅',30,280.00,'/uploads/activity_exam.jpg','需要完成对应级别课程学习',0,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `activity` VALUES (2,'第三届校际钢琴比赛','比赛','“星耀李白湾”第三届校际钢琴比赛','本次比赛分初级组、中级组、高级组三个组别，设金银铜奖及优秀奖。欢迎各琴行学员参加！','2026-05-20 09:00:00','2026-05-21 17:00:00','市文化中心音乐厅',50,150.00,'/uploads/activity_competition.jpg','钢琴考级三级以上',0,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `activity` VALUES (3,'2026年春季音乐汇演','演出','2026年春季学员音乐汇报演出','琴行年度学员汇报演出，展示学员一年来的学习成果。家长可受邀观看，为孩子加油！','2026-06-01 14:00:00','2026-06-01 17:00:00','本琴行多功能厅',80,0.00,'/uploads/activity_concert.jpg','本琴行在学学员均可参加',0,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `activity` VALUES (4,'2026年吉他考级春季','考级','2026年春季吉他考级报名开始','中国音乐家协会2026年春季吉他考级，考级级别1-8级。','2026-04-20 09:00:00','2026-04-20 17:00:00','本琴行吉他教室',20,180.00,'/uploads/activity_exam.jpg','需要完成对应级别课程学习',0,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `activity` VALUES (5,'2026年古筝考级春季','考级','2026年春季古筝考级报名开始','中国音乐家协会2026年春季古筝考级，考级级别1-10级。','2026-04-22 09:00:00','2026-04-22 17:00:00','本琴行古筝教室',20,200.00,'/uploads/activity_exam.jpg','需要完成对应级别课程学习',0,'2026-04-11 09:35:48','2026-04-11 09:35:48');
/*!40000 ALTER TABLE `activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activity_registration`
--

DROP TABLE IF EXISTS `activity_registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_registration` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `status` tinyint DEFAULT '0' COMMENT '?????0-??? 1-??? 2-??? 3-???',
  `fee_paid` tinyint DEFAULT '0' COMMENT '是否缴费：0-否 1-是',
  `passed` tinyint DEFAULT NULL COMMENT '是否通过：0-否 1-是',
  `rank` int DEFAULT NULL COMMENT '名次',
  `award_level` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '获奖等级',
  `teacher_id` bigint DEFAULT NULL COMMENT '指导教师ID',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='活动报名表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_registration`
--

LOCK TABLES `activity_registration` WRITE;
/*!40000 ALTER TABLE `activity_registration` DISABLE KEYS */;
INSERT INTO `activity_registration` VALUES (1,1,7,1,1,NULL,NULL,NULL,2,'报名钢琴考级三级','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `activity_registration` VALUES (2,1,8,1,1,NULL,NULL,NULL,2,'报名钢琴考级五级','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `activity_registration` VALUES (3,1,13,1,1,NULL,NULL,NULL,2,'报名钢琴考级一级','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `activity_registration` VALUES (4,2,7,1,1,NULL,NULL,NULL,2,'报名钢琴比赛初级组','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `activity_registration` VALUES (5,2,8,1,1,NULL,NULL,NULL,2,'报名钢琴比赛中级组','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `activity_registration` VALUES (6,3,7,1,0,NULL,NULL,NULL,2,'参加汇报演出，演奏钢琴曲目','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `activity_registration` VALUES (7,3,9,1,0,NULL,NULL,NULL,3,'参加汇报演出，吉他弹唱','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `activity_registration` VALUES (8,3,11,1,0,NULL,NULL,NULL,4,'参加汇报演出，小提琴独奏','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `activity_registration` VALUES (9,3,12,1,0,NULL,NULL,NULL,5,'参加汇报演出，古筝演奏','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `activity_registration` VALUES (10,4,9,1,1,NULL,NULL,NULL,3,'报名吉他考级三级','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `activity_registration` VALUES (11,4,10,1,1,NULL,NULL,NULL,3,'报名吉他考级三级','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `activity_registration` VALUES (12,5,12,1,1,NULL,NULL,NULL,5,'报名古筝考级三级','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `activity_registration` VALUES (13,2,9,0,0,NULL,NULL,NULL,3,'2026-05业务测试数据：李思涵报名钢琴比赛，待审核待缴费','2026-05-09 09:30:00','2026-05-13 22:24:50');
INSERT INTO `activity_registration` VALUES (14,2,10,1,1,NULL,NULL,NULL,3,'2026-05业务测试数据：张浩然报名钢琴比赛，审核通过已缴费','2026-05-09 10:20:00','2026-05-13 22:24:50');
INSERT INTO `activity_registration` VALUES (15,2,11,2,0,NULL,NULL,NULL,4,'2026-05业务测试数据：刘雅琳报名钢琴比赛，材料不符已拒绝','2026-05-09 11:20:00','2026-05-13 22:24:50');
INSERT INTO `activity_registration` VALUES (16,2,12,3,0,NULL,NULL,NULL,5,'2026-05业务测试数据：陈子轩报名钢琴比赛，学生主动取消','2026-05-09 14:20:00','2026-05-13 22:24:50');
INSERT INTO `activity_registration` VALUES (17,2,13,1,1,NULL,NULL,NULL,2,'2026-05业务测试数据：周悦然报名钢琴比赛，审核通过已缴费','2026-05-09 15:20:00','2026-05-13 22:24:50');
INSERT INTO `activity_registration` VALUES (18,3,8,0,1,NULL,NULL,NULL,2,'2026-05业务测试数据：赵雨萱报名春季音乐汇演，免费活动待审核','2026-05-10 09:00:00','2026-05-13 22:24:50');
INSERT INTO `activity_registration` VALUES (19,3,10,1,1,NULL,NULL,NULL,3,'2026-05业务测试数据：张浩然报名春季音乐汇演，免费活动已通过','2026-05-10 09:30:00','2026-05-13 22:24:50');
INSERT INTO `activity_registration` VALUES (20,3,13,3,0,NULL,NULL,NULL,6,'2026-05业务测试数据：周悦然报名春季音乐汇演后取消','2026-05-10 10:00:00','2026-05-13 22:24:50');
/*!40000 ALTER TABLE `activity_registration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `banner`
--

DROP TABLE IF EXISTS `banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `banner` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '标题',
  `image_url` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '图片地址',
  `type` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '类型：TEACHER/ACTIVITY/EXAM/COURSE/INSTRUMENT',
  `link_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '链接地址',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='轮播图表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banner`
--

LOCK TABLES `banner` WRITE;
/*!40000 ALTER TABLE `banner` DISABLE KEYS */;
INSERT INTO `banner` VALUES (1,'春季课程报名中','/uploads/banner1.jpg','COURSE','/student/booking',1,1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `banner` VALUES (2,'全新乐器上架','/uploads/banner2.jpg','INSTRUMENT','/student/shop',2,1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `banner` VALUES (3,'2026春季考级报名开始','/uploads/banner3.jpg','EXAM','/student/activity',3,1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `banner` VALUES (4,'优秀师资团队','/uploads/banner4.jpg','TEACHER','/student/booking',4,1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `banner` VALUES (5,'学员汇报演出招募','/uploads/banner5.jpg','ACTIVITY','/student/activity',5,1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
/*!40000 ALTER TABLE `banner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型：INSTRUMENT/TEXTBOOK',
  `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用 1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'钢琴','INSTRUMENT','钢琴类课程与商品',1,1,'2026-04-11 09:35:48');
INSERT INTO `category` VALUES (2,'吉他','INSTRUMENT','吉他类课程与商品',2,1,'2026-04-11 09:35:48');
INSERT INTO `category` VALUES (3,'小提琴','INSTRUMENT','小提琴类课程与商品',3,1,'2026-04-11 09:35:48');
INSERT INTO `category` VALUES (4,'古筝','INSTRUMENT','古筝类课程与商品',4,1,'2026-04-11 09:35:48');
INSERT INTO `category` VALUES (5,'声乐','INSTRUMENT','声乐类课程',5,1,'2026-04-11 09:35:48');
INSERT INTO `category` VALUES (6,'乐理教材','TEXTBOOK','音乐理论教材',1,1,'2026-04-11 09:35:48');
INSERT INTO `category` VALUES (7,'考级教材','TEXTBOOK','考级专用教材',2,1,'2026-04-11 09:35:48');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `certificate`
--

DROP TABLE IF EXISTS `certificate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `certificate` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `activity_id` bigint DEFAULT NULL COMMENT '活动ID',
  `type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型：CERTIFICATE/AWARD',
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '证书/奖项名称',
  `image_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图片',
  `obtain_time` date DEFAULT NULL COMMENT '获得时间',
  `teacher_id` bigint DEFAULT NULL COMMENT '指导教师ID',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='证书获奖记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `certificate`
--

LOCK TABLES `certificate` WRITE;
/*!40000 ALTER TABLE `certificate` DISABLE KEYS */;
INSERT INTO `certificate` VALUES (1,7,NULL,'CERTIFICATE','钢琴演奏二级证书','/uploads/cert1.jpg','2025-06-15',2,'2025年春季考级通过','2026-04-11 09:35:48');
INSERT INTO `certificate` VALUES (2,8,NULL,'CERTIFICATE','钢琴演奏四级证书','/uploads/cert1.jpg','2025-06-15',2,'2025年春季考级通过','2026-04-11 09:35:48');
INSERT INTO `certificate` VALUES (3,9,NULL,'CERTIFICATE','吉他演奏二级证书','/uploads/cert1.jpg','2025-06-20',3,'2025年春季考级通过','2026-04-11 09:35:48');
INSERT INTO `certificate` VALUES (4,11,NULL,'CERTIFICATE','小提琴演奏一级证书','/uploads/cert1.jpg','2025-12-10',4,'2025年冬季考级通过','2026-04-11 09:35:48');
INSERT INTO `certificate` VALUES (5,7,NULL,'AWARD','第二届校际钢琴比赛初级组银奖','/uploads/cert2.jpg','2025-05-20',2,'初级组第二名','2026-04-11 09:35:48');
INSERT INTO `certificate` VALUES (6,8,NULL,'AWARD','第二届校际钢琴比赛中级组金奖','/uploads/cert2.jpg','2025-05-20',2,'中级组第一名','2026-04-11 09:35:48');
INSERT INTO `certificate` VALUES (7,12,NULL,'CERTIFICATE','古筝演奏二级证书','/uploads/cert1.jpg','2025-12-15',5,'2025年冬季考级通过','2026-04-11 09:35:48');
INSERT INTO `certificate` VALUES (8,10,NULL,'CERTIFICATE','吉他演奏二级证书','/uploads/cert1.jpg','2025-12-20',3,'2025年冬季考级通过','2026-04-11 09:35:48');
/*!40000 ALTER TABLE `certificate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class_record`
--

DROP TABLE IF EXISTS `class_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `booking_id` bigint DEFAULT NULL COMMENT '预约ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `classroom_id` bigint DEFAULT NULL COMMENT '教室ID',
  `class_time` datetime DEFAULT NULL COMMENT '上课时间',
  `duration` int DEFAULT NULL COMMENT '时长（分钟）',
  `learning_status` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学习情况',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `course_score` int DEFAULT NULL COMMENT '课程评分（1-5）',
  `teacher_score` int DEFAULT NULL COMMENT '教师评分（1-5）',
  `evaluation` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '评价内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='上课记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class_record`
--

LOCK TABLES `class_record` WRITE;
/*!40000 ALTER TABLE `class_record` DISABLE KEYS */;
INSERT INTO `class_record` VALUES (1,1,7,2,1,1,'2026-02-15 14:00:00',60,'掌握了基本坐姿和手型，学习了C大调音阶','进步明显，继续加强手指独立性练习',5,5,'张老师非常专业，讲解很耐心，很喜欢这节课！','2026-04-11 09:35:48');
INSERT INTO `class_record` VALUES (2,2,8,2,2,2,'2026-02-15 15:30:00',60,'练习车尔尼599第20-30首，节奏感有提升','需要加强左手跨度练习',4,5,'老师教得很好，尤其是节奏部分讲得很透彻','2026-04-11 09:35:48');
INSERT INTO `class_record` VALUES (3,3,9,3,4,3,'2026-02-16 10:00:00',60,'学习了C、Am、G、F和弦，能简单切换','手指力度需加强，回家多练琴',5,5,'李老师很有责任心，教学方式很生动','2026-04-11 09:35:48');
INSERT INTO `class_record` VALUES (4,4,10,3,5,3,'2026-02-16 14:00:00',60,'学习了《小星星》弹唱，基本能跟上节奏','唱歌时注意气息控制',4,4,'弹唱课很有意思，学到了很多','2026-04-11 09:35:48');
INSERT INTO `class_record` VALUES (5,5,11,4,6,4,'2026-02-17 10:00:00',60,'学习了持弓姿势和空弦拉奏，姿势基本正确','注意右手弓的平稳性',5,5,'王老师专业水平很高，教学很细致','2026-04-11 09:35:48');
INSERT INTO `class_record` VALUES (6,6,12,5,8,5,'2026-02-17 14:00:00',60,'学习了古筝基本指法：勾、托、抹，练习《沧海一声笑》片段','进度良好，注意手型',4,5,'陈老师很有耐心，古筝课很有音乐感','2026-04-11 09:35:48');
INSERT INTO `class_record` VALUES (7,7,13,6,9,6,'2026-02-18 10:00:00',45,'学习了腹式呼吸和基本发声，音准较好','可以尝试唱简单的练声曲',5,5,'周老师声音很好听，教学很专业！','2026-04-11 09:35:48');
INSERT INTO `class_record` VALUES (8,12,12,3,4,3,'2026-02-27 14:00:00',60,'学习了基本和弦扒弦，节奏感较好','继续加强和弦转换练习',4,5,'吉他课很有趣，老师教得好','2026-04-11 09:35:48');
INSERT INTO `class_record` VALUES (9,13,13,2,1,1,'2026-02-28 10:00:00',60,'学习了钢琴基本指法和简单练习曲','手指独立性需加强',5,5,'钢琴课很好，张老师很温柔专业','2026-04-11 09:35:48');
INSERT INTO `class_record` VALUES (10,14,7,6,9,6,'2026-02-28 15:00:00',45,'学习了简单的发声练习和一首儿歌','可以多听多唱，培养乐感',4,4,'声乐课很有意思，周老师很鼓励人','2026-04-11 09:35:48');
/*!40000 ALTER TABLE `class_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classroom`
--

DROP TABLE IF EXISTS `classroom`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classroom` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '教室名称',
  `capacity` int DEFAULT NULL COMMENT '容量',
  `location` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '位置',
  `equipment` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'equipment',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用 1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教室表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classroom`
--

LOCK TABLES `classroom` WRITE;
/*!40000 ALTER TABLE `classroom` DISABLE KEYS */;
INSERT INTO `classroom` VALUES (1,'钢琴教室A',1,'一楼101',NULL,1,'2026-04-11 09:35:48');
INSERT INTO `classroom` VALUES (2,'钢琴教室B',1,'一楼102',NULL,1,'2026-04-11 09:35:48');
INSERT INTO `classroom` VALUES (3,'吉他教室',5,'二楼201',NULL,1,'2026-04-11 09:35:48');
INSERT INTO `classroom` VALUES (4,'小提琴教室',3,'二楼202',NULL,1,'2026-04-11 09:35:48');
INSERT INTO `classroom` VALUES (5,'古筝教室',3,'二楼203',NULL,1,'2026-04-11 09:35:48');
INSERT INTO `classroom` VALUES (6,'声乐教室',1,'三楼301',NULL,1,'2026-04-11 09:35:48');
INSERT INTO `classroom` VALUES (7,'多功能厅',30,'三楼302',NULL,1,'2026-04-11 09:35:48');
/*!40000 ALTER TABLE `classroom` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consult_message`
--

DROP TABLE IF EXISTS `consult_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consult_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `sender_id` bigint NOT NULL COMMENT '发送人用户ID',
  `sender_role` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'STUDENT/TEACHER',
  `content` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`),
  KEY `idx_room` (`course_id`,`teacher_id`,`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='师生咨询留言';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consult_message`
--

LOCK TABLES `consult_message` WRITE;
/*!40000 ALTER TABLE `consult_message` DISABLE KEYS */;
/*!40000 ALTER TABLE `consult_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '课程名称',
  `category_id` bigint DEFAULT NULL COMMENT '乐器分类ID',
  `teacher_id` bigint DEFAULT NULL COMMENT '授课教师ID',
  `max_students` int DEFAULT '1' COMMENT '最大人数',
  `cover_image` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '封面图',
  `level` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '级别',
  `duration` int DEFAULT NULL COMMENT '时长（分钟）',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `discount` decimal(3,2) DEFAULT '1.00' COMMENT '折扣',
  `description` text COLLATE utf8mb4_general_ci COMMENT '描述',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用 1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,'钢琴入门课',1,2,1,'/uploads/course_piano1.jpg','初级',60,200.00,1.00,'零基础钢琴入门，学习基本指法与乐理知识，掌握简单曲目演奏',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course` VALUES (2,'钢琴进阶课',1,2,1,'/uploads/course_piano2.jpg','中级',60,300.00,0.90,'适合有一定基础的学员，卦习车尔尼练习曲与古典小品',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course` VALUES (3,'钢琴考级冲刺班',1,2,3,'/uploads/course_piano1.jpg','高级',90,500.00,0.85,'针对音协考级六级以上学员，精练考级曲目与乐理听力',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course` VALUES (4,'吉他入门课',2,3,5,'/uploads/course_guitar.jpg','初级',60,150.00,1.00,'民谣吉他零基础课程，学习和弦进行与扒弦技巧',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course` VALUES (5,'吉他弹唱课',2,3,5,'/uploads/course_guitar.jpg','中级',60,200.00,0.95,'学习流行歌曲弹唱，掌握常用和弦进行与节奏型',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course` VALUES (6,'小提琴入门课',3,4,3,'/uploads/course_violin.jpg','初级',60,250.00,1.00,'小提琴基础入门，学习持弓姿势、基本弓法与音阶',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course` VALUES (7,'小提琴进阶课',3,4,3,'/uploads/course_violin.jpg','中级',60,350.00,0.90,'提升演奏技巧，学习揉弦、跳弓等进阶技术',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course` VALUES (8,'古筝入门课',4,5,3,'/uploads/course_guzheng.jpg','初级',60,200.00,0.95,'古筝基础教学，学习基本指法和经典入门曲目',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course` VALUES (9,'声乐基础课',5,6,1,'/uploads/course_vocal.jpg','初级',45,180.00,1.00,'声乐基础训练，包含气息练习、发声技巧与简单歌曲',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course` VALUES (10,'声乐进阶课',5,6,1,'/uploads/course_vocal.jpg','中级',60,280.00,0.90,'声乐进阶训练，学习美声与民族唱法，提升演唱表现力',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_booking`
--

DROP TABLE IF EXISTS `course_booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_booking` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `student_package_id` bigint DEFAULT NULL COMMENT 'student course package id',
  `hours_cost` int DEFAULT '1' COMMENT 'hours cost',
  `classroom_id` bigint DEFAULT NULL COMMENT '教室ID',
  `booking_time` datetime DEFAULT NULL COMMENT '预约时间',
  `class_time` datetime DEFAULT NULL COMMENT '上课时间',
  `duration` int DEFAULT NULL COMMENT '时长（分钟）',
  `status` tinyint DEFAULT '0' COMMENT '???0-??? 1-??? 2-??? 3-???',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_student_package_id` (`student_package_id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课程预约表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_booking`
--

LOCK TABLES `course_booking` WRITE;
/*!40000 ALTER TABLE `course_booking` DISABLE KEYS */;
INSERT INTO `course_booking` VALUES (1,7,2,1,NULL,1,1,'2026-02-10 09:00:00','2026-02-15 14:00:00',60,1,'希望学习钢琴基础','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course_booking` VALUES (2,8,2,2,NULL,1,2,'2026-02-10 10:00:00','2026-02-15 15:30:00',60,1,'有一年基础，希望进阶','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course_booking` VALUES (3,9,3,4,NULL,1,3,'2026-02-11 09:00:00','2026-02-16 10:00:00',60,1,'想学民谣吉他','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course_booking` VALUES (4,10,3,5,NULL,1,3,'2026-02-11 14:00:00','2026-02-16 14:00:00',60,1,'想学弹唱','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course_booking` VALUES (5,11,4,6,NULL,1,4,'2026-02-12 09:00:00','2026-02-17 10:00:00',60,1,'小提琴零基础','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course_booking` VALUES (6,12,5,8,NULL,1,5,'2026-02-12 10:00:00','2026-02-17 14:00:00',60,1,'对古筝很感兴趣','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course_booking` VALUES (7,13,6,9,NULL,1,6,'2026-02-13 09:00:00','2026-02-18 10:00:00',45,1,'想提升唱歌技巧','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course_booking` VALUES (8,7,2,3,NULL,1,1,'2026-02-20 09:00:00','2026-02-25 14:00:00',90,0,'准备考级','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course_booking` VALUES (9,8,4,6,NULL,1,4,'2026-02-20 10:00:00','2026-02-25 15:30:00',60,0,'想学小提琴','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course_booking` VALUES (10,9,6,10,NULL,1,6,'2026-02-21 09:00:00','2026-02-26 10:00:00',60,0,'声乐进阶学习','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course_booking` VALUES (11,10,2,1,NULL,1,2,'2026-02-21 14:00:00','2026-02-26 14:00:00',60,2,'时间冲突取消','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course_booking` VALUES (12,11,5,8,NULL,1,5,'2026-02-22 09:00:00','2026-02-27 10:00:00',60,1,'古筝入门学习','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course_booking` VALUES (13,12,3,4,NULL,1,3,'2026-02-22 10:00:00','2026-02-27 14:00:00',60,1,'想学吉他','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course_booking` VALUES (14,13,2,1,NULL,1,1,'2026-02-23 09:00:00','2026-02-28 10:00:00',60,1,'钢琴入门','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course_booking` VALUES (15,7,6,9,NULL,1,6,'2026-02-23 14:00:00','2026-02-28 15:00:00',45,1,'想学声乐','2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `course_booking` VALUES (16,7,2,1,1,1,1,'2026-05-06 09:00:00','2026-05-14 17:30:00',60,1,'2026-05业务测试数据：王子豪钢琴入门复习，已通过','2026-05-06 09:00:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (17,8,2,2,4,1,2,'2026-05-06 09:10:00','2026-05-14 18:45:00',60,0,'2026-05业务测试数据：赵雨萱钢琴进阶新曲目，待审批','2026-05-06 09:10:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (18,7,2,3,2,2,1,'2026-05-06 09:20:00','2026-05-15 17:30:00',90,1,'2026-05业务测试数据：王子豪钢琴考级模拟，已通过','2026-05-06 09:20:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (19,10,2,1,10,1,1,'2026-05-06 09:30:00','2026-05-16 09:00:00',60,1,'2026-05业务测试数据：张浩然钢琴入门手型训练，已通过','2026-05-06 09:30:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (20,13,2,1,16,1,2,'2026-05-06 09:40:00','2026-05-16 10:15:00',60,1,'2026-05业务测试数据：周悦然钢琴入门节奏训练，已通过','2026-05-06 09:40:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (21,8,2,2,4,1,1,'2026-05-06 09:50:00','2026-05-17 14:00:00',60,1,'2026-05业务测试数据：赵雨萱钢琴进阶和弦，已通过','2026-05-06 09:50:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (22,7,2,1,1,1,2,'2026-05-06 10:00:00','2026-05-18 19:00:00',60,0,'2026-05业务测试数据：王子豪钢琴入门晚间课，待审批','2026-05-06 10:00:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (23,7,2,3,2,2,1,'2026-05-06 10:10:00','2026-05-20 17:30:00',90,2,'2026-05业务测试数据：王子豪临时冲刺课，教师拒绝','2026-05-06 10:10:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (24,10,2,1,10,1,2,'2026-05-06 10:20:00','2026-05-21 18:45:00',60,3,'2026-05业务测试数据：张浩然家长取消钢琴课','2026-05-06 10:20:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (25,13,2,1,16,1,2,'2026-05-06 10:30:00','2026-05-23 10:15:00',60,1,'2026-05业务测试数据：周悦然钢琴入门巩固，已通过','2026-05-06 10:30:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (26,7,2,3,2,2,1,'2026-05-06 10:40:00','2026-05-24 14:00:00',90,1,'2026-05业务测试数据：王子豪钢琴考级第二轮模拟，已通过','2026-05-06 10:40:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (27,8,2,2,4,1,1,'2026-05-06 10:50:00','2026-05-28 17:30:00',60,0,'2026-05业务测试数据：赵雨萱钢琴进阶复盘，待审批','2026-05-06 10:50:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (28,9,3,4,7,1,3,'2026-05-06 11:00:00','2026-05-14 19:00:00',60,1,'2026-05业务测试数据：李思涵吉他入门右手分解，已通过','2026-05-06 11:00:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (29,10,3,5,9,1,3,'2026-05-06 11:10:00','2026-05-15 18:45:00',60,1,'2026-05业务测试数据：张浩然吉他弹唱节奏型，已通过','2026-05-06 11:10:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (30,12,3,4,15,1,3,'2026-05-06 11:20:00','2026-05-16 11:30:00',60,1,'2026-05业务测试数据：陈子轩吉他入门和弦转换，已通过','2026-05-06 11:20:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (31,9,3,4,7,1,3,'2026-05-06 11:30:00','2026-05-17 10:00:00',60,0,'2026-05业务测试数据：李思涵吉他拨片练习，待审批','2026-05-06 11:30:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (32,10,3,5,9,1,3,'2026-05-06 11:40:00','2026-05-19 18:30:00',60,1,'2026-05业务测试数据：张浩然吉他弹唱完整曲目，已通过','2026-05-06 11:40:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (33,12,3,4,15,1,3,'2026-05-06 11:50:00','2026-05-20 19:00:00',60,2,'2026-05业务测试数据：陈子轩吉他课时段不合适，已拒绝','2026-05-06 11:50:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (34,9,3,4,7,1,3,'2026-05-06 12:00:00','2026-05-22 18:45:00',60,1,'2026-05业务测试数据：李思涵吉他入门扫弦，已通过','2026-05-06 12:00:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (35,10,3,5,9,1,3,'2026-05-06 12:10:00','2026-05-23 11:30:00',60,3,'2026-05业务测试数据：张浩然外出取消吉他课','2026-05-06 12:10:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (36,12,3,4,15,1,3,'2026-05-06 12:20:00','2026-05-27 19:00:00',60,0,'2026-05业务测试数据：陈子轩吉他入门复习，待审批','2026-05-06 12:20:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (37,9,3,4,7,1,3,'2026-05-06 12:30:00','2026-05-30 10:15:00',60,1,'2026-05业务测试数据：李思涵周末吉他巩固，已通过','2026-05-06 12:30:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (38,11,4,6,12,1,4,'2026-05-06 13:00:00','2026-05-14 17:00:00',60,1,'2026-05业务测试数据：刘雅琳小提琴空弦练习，已通过','2026-05-06 13:00:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (39,8,4,6,5,1,4,'2026-05-06 13:10:00','2026-05-15 19:15:00',60,1,'2026-05业务测试数据：赵雨萱小提琴入门持琴，已通过','2026-05-06 13:10:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (40,13,4,7,18,1,4,'2026-05-06 13:20:00','2026-05-16 14:00:00',60,1,'2026-05业务测试数据：周悦然小提琴进阶音阶，已通过','2026-05-06 13:20:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (41,11,4,6,12,1,4,'2026-05-06 13:30:00','2026-05-18 17:30:00',60,0,'2026-05业务测试数据：刘雅琳小提琴入门复习，待审批','2026-05-06 13:30:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (42,8,4,6,5,1,4,'2026-05-06 13:40:00','2026-05-19 19:45:00',60,2,'2026-05业务测试数据：赵雨萱时间冲突，小提琴课拒绝','2026-05-06 13:40:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (43,13,4,7,18,1,4,'2026-05-06 13:50:00','2026-05-21 17:30:00',60,1,'2026-05业务测试数据：周悦然小提琴进阶揉弦，已通过','2026-05-06 13:50:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (44,11,4,6,12,1,4,'2026-05-06 14:00:00','2026-05-23 14:00:00',60,1,'2026-05业务测试数据：刘雅琳周末小提琴入门，已通过','2026-05-06 14:00:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (45,8,4,6,5,1,4,'2026-05-06 14:10:00','2026-05-26 19:15:00',60,0,'2026-05业务测试数据：赵雨萱小提琴入门复盘，待审批','2026-05-06 14:10:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (46,13,4,7,18,1,4,'2026-05-06 14:20:00','2026-05-28 19:15:00',60,3,'2026-05业务测试数据：周悦然取消小提琴进阶课','2026-05-06 14:20:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (47,12,5,8,14,1,5,'2026-05-06 14:30:00','2026-05-14 18:15:00',60,1,'2026-05业务测试数据：陈子轩古筝入门托劈，已通过','2026-05-06 14:30:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (48,11,5,8,13,1,5,'2026-05-06 14:40:00','2026-05-16 15:30:00',60,1,'2026-05业务测试数据：刘雅琳古筝入门指法，已通过','2026-05-06 14:40:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (49,8,5,8,6,1,5,'2026-05-06 14:50:00','2026-05-17 11:30:00',60,1,'2026-05业务测试数据：赵雨萱古筝入门练习，已通过','2026-05-06 14:50:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (50,12,5,8,14,1,5,'2026-05-06 15:00:00','2026-05-19 17:30:00',60,0,'2026-05业务测试数据：陈子轩古筝入门复习，待审批','2026-05-06 15:00:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (51,11,5,8,13,1,5,'2026-05-06 15:10:00','2026-05-21 19:00:00',60,2,'2026-05业务测试数据：刘雅琳古筝课时段不合适，已拒绝','2026-05-06 15:10:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (52,8,5,8,6,1,5,'2026-05-06 15:20:00','2026-05-24 11:30:00',60,3,'2026-05业务测试数据：赵雨萱取消古筝课','2026-05-06 15:20:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (53,12,5,8,14,1,5,'2026-05-06 15:30:00','2026-05-25 18:15:00',60,1,'2026-05业务测试数据：陈子轩古筝入门连贯练习，已通过','2026-05-06 15:30:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (54,11,5,8,13,1,5,'2026-05-06 15:40:00','2026-05-29 19:00:00',60,0,'2026-05业务测试数据：刘雅琳古筝入门回课，待审批','2026-05-06 15:40:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (55,7,6,9,3,1,6,'2026-05-06 16:00:00','2026-05-14 20:00:00',45,1,'2026-05业务测试数据：王子豪声乐基础气息，已通过','2026-05-06 16:00:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (56,9,6,10,8,1,6,'2026-05-06 16:10:00','2026-05-15 20:00:00',60,1,'2026-05业务测试数据：李思涵声乐进阶咬字，已通过','2026-05-06 16:10:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (57,10,6,9,11,1,6,'2026-05-06 16:20:00','2026-05-16 16:45:00',45,1,'2026-05业务测试数据：张浩然声乐基础音准，已通过','2026-05-06 16:20:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (58,13,6,9,17,1,6,'2026-05-06 16:30:00','2026-05-18 20:00:00',45,0,'2026-05业务测试数据：周悦然声乐基础气息，待审批','2026-05-06 16:30:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (59,7,6,9,3,1,6,'2026-05-06 16:40:00','2026-05-20 20:00:00',45,1,'2026-05业务测试数据：王子豪声乐基础共鸣，已通过','2026-05-06 16:40:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (60,9,6,10,8,1,6,'2026-05-06 16:50:00','2026-05-22 20:00:00',60,3,'2026-05业务测试数据：李思涵取消声乐进阶课','2026-05-06 16:50:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (61,13,6,9,17,1,6,'2026-05-06 17:00:00','2026-05-23 16:00:00',45,1,'2026-05业务测试数据：周悦然声乐基础发声，已通过','2026-05-06 17:00:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (62,10,6,9,11,1,6,'2026-05-06 17:10:00','2026-05-24 16:45:00',45,2,'2026-05业务测试数据：张浩然声乐课时间不合适，已拒绝','2026-05-06 17:10:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (63,7,6,9,3,1,6,'2026-05-06 17:20:00','2026-05-26 20:00:00',45,0,'2026-05业务测试数据：王子豪声乐基础复盘，待审批','2026-05-06 17:20:00','2026-05-13 22:24:50');
INSERT INTO `course_booking` VALUES (64,9,6,10,8,1,6,'2026-05-06 17:30:00','2026-05-28 20:00:00',60,1,'2026-05业务测试数据：李思涵声乐进阶完整曲目，已通过','2026-05-06 17:30:00','2026-05-13 22:24:50');
/*!40000 ALTER TABLE `course_booking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_log`
--

DROP TABLE IF EXISTS `operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
  `module` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作模块',
  `operation` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作描述',
  `method` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求方法',
  `params` text COLLATE utf8mb4_general_ci COMMENT '请求参数',
  `ip` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'IP地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_log`
--

LOCK TABLES `operation_log` WRITE;
/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
INSERT INTO `operation_log` VALUES (1,1,'admin','用户管理','新增教师账号 teacher03','POST /api/user','{\"username\":\"teacher03\"}','127.0.0.1','2025-12-01 08:30:00');
INSERT INTO `operation_log` VALUES (2,1,'admin','商品管理','新增商品：雅马哈立式钢琴','POST /api/product','{\"name\":\"雅马哈立式钢琴 YU118\"}','127.0.0.1','2025-12-01 09:00:00');
INSERT INTO `operation_log` VALUES (3,1,'admin','库存管理','钢琴入库 5 台','POST /api/stockRecord','{\"productId\":1,\"quantity\":5}','127.0.0.1','2025-12-01 09:05:00');
INSERT INTO `operation_log` VALUES (4,1,'admin','课程管理','新增课程：钢琴考级冲刺班','POST /api/course','{\"name\":\"钢琴考级冲刺班\"}','127.0.0.1','2026-01-10 10:00:00');
INSERT INTO `operation_log` VALUES (5,1,'admin','活动管理','新增活动：2026年春季钢琴考级','POST /api/activity','{\"name\":\"2026年钢琴考级春季\"}','127.0.0.1','2026-02-01 08:00:00');
INSERT INTO `operation_log` VALUES (6,1,'admin','轮播图管理','更新轮播图：春季课程报名','PUT /api/banner','{\"id\":1}','127.0.0.1','2026-02-01 09:00:00');
INSERT INTO `operation_log` VALUES (7,2,'teacher01','预约审批','审批通过学员王子豪的钢琴预约','PUT /api/booking','{\"id\":1,\"status\":1}','127.0.0.1','2026-02-12 09:00:00');
INSERT INTO `operation_log` VALUES (8,3,'teacher02','预约审批','审批通过学员李思涵的吉他预约','PUT /api/booking','{\"id\":3,\"status\":1}','127.0.0.1','2026-02-13 09:00:00');
/*!40000 ALTER TABLE `operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_info`
--

DROP TABLE IF EXISTS `order_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单号',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `order_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型：COURSE/INSTRUMENT/TEXTBOOK',
  `product_id` bigint DEFAULT NULL COMMENT '商品/课程ID',
  `quantity` int DEFAULT '1' COMMENT '数量',
  `original_amount` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '折扣金额',
  `actual_amount` decimal(10,2) DEFAULT NULL COMMENT '实付金额',
  `status` tinyint DEFAULT '0' COMMENT '???0-??? 1-??? 2-???/???',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_order_type` (`order_type`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_info`
--

LOCK TABLES `order_info` WRITE;
/*!40000 ALTER TABLE `order_info` DISABLE KEYS */;
INSERT INTO `order_info` VALUES (1,'ORD20260201001',7,'COURSE',1,1,200.00,0.00,200.00,1,'2026-02-01 10:30:00',NULL,'钢琴入门课报名','2026-02-01 10:00:00','2026-04-11 09:35:48');
INSERT INTO `order_info` VALUES (2,'ORD20260201002',8,'COURSE',2,1,300.00,30.00,270.00,1,'2026-02-01 11:00:00',NULL,'钢琴进阶课报名','2026-02-01 10:30:00','2026-04-11 09:35:48');
INSERT INTO `order_info` VALUES (3,'ORD20260201003',9,'COURSE',4,1,150.00,0.00,150.00,1,'2026-02-01 14:30:00',NULL,'吉他入门课报名','2026-02-01 14:00:00','2026-04-11 09:35:48');
INSERT INTO `order_info` VALUES (4,'ORD20260201004',10,'COURSE',5,1,200.00,10.00,190.00,1,'2026-02-01 15:00:00',NULL,'吉他弹唱课报名','2026-02-01 14:30:00','2026-04-11 09:35:48');
INSERT INTO `order_info` VALUES (5,'ORD20260201005',11,'COURSE',6,1,250.00,0.00,250.00,1,'2026-02-02 10:30:00',NULL,'小提琴入门课报名','2026-02-02 10:00:00','2026-04-11 09:35:48');
INSERT INTO `order_info` VALUES (6,'ORD20260201006',12,'COURSE',8,1,200.00,10.00,190.00,1,'2026-02-02 11:00:00',NULL,'古筝入门课报名','2026-02-02 10:30:00','2026-04-11 09:35:48');
INSERT INTO `order_info` VALUES (7,'ORD20260201007',13,'COURSE',9,1,180.00,0.00,180.00,1,'2026-02-02 14:30:00',NULL,'声乐基础课报名','2026-02-02 14:00:00','2026-04-11 09:35:48');
INSERT INTO `order_info` VALUES (8,'ORD20260210001',7,'INSTRUMENT',3,1,1580.00,0.00,1580.00,1,'2026-02-10 15:00:00',NULL,'购买雅马哈民谣吉他','2026-02-10 14:30:00','2026-04-11 09:35:48');
INSERT INTO `order_info` VALUES (9,'ORD20260210002',9,'TEXTBOOK',9,2,90.00,0.00,90.00,1,'2026-02-10 16:00:00',NULL,'购买拜厄教程×2','2026-02-10 15:30:00','2026-04-11 09:35:48');
INSERT INTO `order_info` VALUES (10,'ORD20260215001',8,'TEXTBOOK',10,1,38.00,0.00,38.00,1,'2026-02-15 10:00:00',NULL,'购买车尔尼599','2026-02-15 09:30:00','2026-04-11 09:35:48');
INSERT INTO `order_info` VALUES (11,'ORD20260215002',10,'TEXTBOOK',12,1,42.00,0.00,42.00,1,'2026-02-15 11:00:00',NULL,'购买吉他自学三月通','2026-02-15 10:30:00','2026-04-11 09:35:48');
INSERT INTO `order_info` VALUES (12,'ORD20260220001',11,'INSTRUMENT',6,1,2200.00,0.00,2200.00,1,'2026-02-20 15:00:00',NULL,'购买红棉小提琴','2026-02-20 14:00:00','2026-04-11 09:35:48');
INSERT INTO `order_info` VALUES (13,'ORD20260220002',12,'INSTRUMENT',7,1,3800.00,0.00,3800.00,0,NULL,NULL,'古筝待付款','2026-02-20 15:00:00','2026-04-11 09:35:48');
INSERT INTO `order_info` VALUES (14,'ORD20260225001',7,'TEXTBOOK',14,1,35.00,0.00,35.00,1,'2026-02-25 10:00:00',NULL,'购买乐理教程','2026-02-25 09:30:00','2026-04-11 09:35:48');
INSERT INTO `order_info` VALUES (15,'ORD20260225002',13,'TEXTBOOK',11,1,68.00,0.00,68.00,2,NULL,NULL,'已取消','2026-02-25 10:30:00','2026-04-11 09:35:48');
INSERT INTO `order_info` VALUES (16,'ORD202605TD001',7,'COURSE',1,12,2400.00,0.00,2400.00,1,'2026-05-01 09:20:00','2026-05-01 09:20:00','2026-05业务测试数据：王子豪购买钢琴入门12节课时包','2026-05-01 09:05:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (17,'ORD202605TD002',7,'COURSE',3,8,4000.00,600.00,3400.00,1,'2026-05-01 09:35:00','2026-05-01 09:35:00','2026-05业务测试数据：王子豪购买钢琴考级冲刺8节课时包','2026-05-01 09:15:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (18,'ORD202605TD003',7,'COURSE',9,8,1440.00,0.00,1440.00,1,'2026-05-01 09:50:00','2026-05-01 09:50:00','2026-05业务测试数据：王子豪购买声乐基础8节课时包','2026-05-01 09:30:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (19,'ORD202605TD004',8,'COURSE',2,10,3000.00,300.00,2700.00,1,'2026-05-01 10:15:00','2026-05-01 10:15:00','2026-05业务测试数据：赵雨萱购买钢琴进阶10节课时包','2026-05-01 09:55:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (20,'ORD202605TD005',8,'COURSE',6,10,2500.00,0.00,2500.00,1,'2026-05-01 10:35:00','2026-05-01 10:35:00','2026-05业务测试数据：赵雨萱购买小提琴入门10节课时包','2026-05-01 10:20:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (21,'ORD202605TD006',8,'COURSE',8,8,1600.00,80.00,1520.00,1,'2026-05-01 10:55:00','2026-05-01 10:55:00','2026-05业务测试数据：赵雨萱购买古筝入门8节课时包','2026-05-01 10:40:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (22,'ORD202605TD007',9,'COURSE',4,12,1800.00,0.00,1800.00,1,'2026-05-02 09:15:00','2026-05-02 09:15:00','2026-05业务测试数据：李思涵购买吉他入门12节课时包','2026-05-02 09:00:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (23,'ORD202605TD008',9,'COURSE',10,8,2240.00,224.00,2016.00,1,'2026-05-02 09:35:00','2026-05-02 09:35:00','2026-05业务测试数据：李思涵购买声乐进阶8节课时包','2026-05-02 09:20:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (24,'ORD202605TD009',10,'COURSE',5,10,2000.00,100.00,1900.00,1,'2026-05-02 10:10:00','2026-05-02 10:10:00','2026-05业务测试数据：张浩然购买吉他弹唱10节课时包','2026-05-02 09:50:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (25,'ORD202605TD010',10,'COURSE',1,8,1600.00,0.00,1600.00,1,'2026-05-02 10:30:00','2026-05-02 10:30:00','2026-05业务测试数据：张浩然购买钢琴入门8节课时包','2026-05-02 10:15:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (26,'ORD202605TD011',10,'COURSE',9,8,1440.00,0.00,1440.00,1,'2026-05-02 10:50:00','2026-05-02 10:50:00','2026-05业务测试数据：张浩然购买声乐基础8节课时包','2026-05-02 10:35:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (27,'ORD202605TD012',11,'COURSE',6,12,3000.00,0.00,3000.00,1,'2026-05-03 09:10:00','2026-05-03 09:10:00','2026-05业务测试数据：刘雅琳购买小提琴入门12节课时包','2026-05-03 08:55:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (28,'ORD202605TD013',11,'COURSE',8,10,2000.00,100.00,1900.00,1,'2026-05-03 09:35:00','2026-05-03 09:35:00','2026-05业务测试数据：刘雅琳购买古筝入门10节课时包','2026-05-03 09:20:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (29,'ORD202605TD014',12,'COURSE',8,12,2400.00,120.00,2280.00,1,'2026-05-03 10:05:00','2026-05-03 10:05:00','2026-05业务测试数据：陈子轩购买古筝入门12节课时包','2026-05-03 09:45:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (30,'ORD202605TD015',12,'COURSE',4,10,1500.00,0.00,1500.00,1,'2026-05-03 10:25:00','2026-05-03 10:25:00','2026-05业务测试数据：陈子轩购买吉他入门10节课时包','2026-05-03 10:10:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (31,'ORD202605TD016',13,'COURSE',1,10,2000.00,0.00,2000.00,1,'2026-05-04 09:15:00','2026-05-04 09:15:00','2026-05业务测试数据：周悦然购买钢琴入门10节课时包','2026-05-04 09:00:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (32,'ORD202605TD017',13,'COURSE',9,10,1800.00,0.00,1800.00,1,'2026-05-04 09:40:00','2026-05-04 09:40:00','2026-05业务测试数据：周悦然购买声乐基础10节课时包','2026-05-04 09:25:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (33,'ORD202605TD018',13,'COURSE',7,8,2800.00,280.00,2520.00,1,'2026-05-04 10:05:00','2026-05-04 10:05:00','2026-05业务测试数据：周悦然购买小提琴进阶8节课时包','2026-05-04 09:50:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (34,'ORD202605TD019',7,'ACTIVITY',1,1,280.00,0.00,280.00,1,'2026-04-05 10:00:00','2026-04-05 10:00:00','2026-05业务测试数据：补齐王子豪钢琴考级报名缴费订单','2026-04-05 09:50:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (35,'ORD202605TD020',8,'ACTIVITY',1,1,280.00,0.00,280.00,1,'2026-04-05 10:20:00','2026-04-05 10:20:00','2026-05业务测试数据：补齐赵雨萱钢琴考级报名缴费订单','2026-04-05 10:05:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (36,'ORD202605TD021',13,'ACTIVITY',1,1,280.00,0.00,280.00,1,'2026-04-05 10:40:00','2026-04-05 10:40:00','2026-05业务测试数据：补齐周悦然钢琴考级报名缴费订单','2026-04-05 10:25:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (37,'ORD202605TD022',7,'ACTIVITY',2,1,150.00,0.00,150.00,1,'2026-05-06 15:00:00','2026-05-06 15:00:00','2026-05业务测试数据：补齐王子豪钢琴比赛报名缴费订单','2026-05-06 14:50:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (38,'ORD202605TD023',8,'ACTIVITY',2,1,150.00,0.00,150.00,1,'2026-05-06 15:20:00','2026-05-06 15:20:00','2026-05业务测试数据：补齐赵雨萱钢琴比赛报名缴费订单','2026-05-06 15:05:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (39,'ORD202605TD024',9,'ACTIVITY',4,1,180.00,0.00,180.00,1,'2026-04-10 09:30:00','2026-04-10 09:30:00','2026-05业务测试数据：补齐李思涵吉他考级报名缴费订单','2026-04-10 09:10:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (40,'ORD202605TD025',10,'ACTIVITY',4,1,180.00,0.00,180.00,1,'2026-04-10 09:50:00','2026-04-10 09:50:00','2026-05业务测试数据：补齐张浩然吉他考级报名缴费订单','2026-04-10 09:35:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (41,'ORD202605TD026',12,'ACTIVITY',5,1,200.00,0.00,200.00,1,'2026-04-12 10:00:00','2026-04-12 10:00:00','2026-05业务测试数据：补齐陈子轩古筝考级报名缴费订单','2026-04-12 09:45:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (42,'ORD202605TD027',9,'ACTIVITY',2,1,150.00,0.00,150.00,0,NULL,NULL,'2026-05业务测试数据：李思涵钢琴比赛报名待缴费订单','2026-05-09 09:30:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (43,'ORD202605TD028',10,'ACTIVITY',2,1,150.00,0.00,150.00,1,'2026-05-09 10:20:00','2026-05-09 10:20:00','2026-05业务测试数据：张浩然钢琴比赛报名已缴费订单','2026-05-09 10:05:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (44,'ORD202605TD029',11,'ACTIVITY',2,1,150.00,0.00,150.00,2,NULL,'2026-05-09 11:20:00','2026-05业务测试数据：刘雅琳钢琴比赛报名拒绝后订单关闭','2026-05-09 11:00:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (45,'ORD202605TD030',12,'ACTIVITY',2,1,150.00,0.00,150.00,2,NULL,'2026-05-09 14:20:00','2026-05业务测试数据：陈子轩钢琴比赛报名取消后订单关闭','2026-05-09 14:00:00','2026-05-13 22:24:50');
INSERT INTO `order_info` VALUES (46,'ORD202605TD031',13,'ACTIVITY',2,1,150.00,0.00,150.00,1,'2026-05-09 15:20:00','2026-05-09 15:20:00','2026-05业务测试数据：周悦然钢琴比赛报名已缴费订单','2026-05-09 15:05:00','2026-05-13 22:24:50');
/*!40000 ALTER TABLE `order_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型：INSTRUMENT/TEXTBOOK',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `brand` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '品牌（乐器）',
  `model` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '型号（乐器）',
  `color` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '颜色（乐器）',
  `author` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '作者（教材）',
  `price` decimal(10,2) DEFAULT NULL COMMENT '售价',
  `cost_price` decimal(10,2) DEFAULT NULL COMMENT '进价',
  `discount` decimal(3,2) DEFAULT '1.00' COMMENT '折扣',
  `stock` int DEFAULT '0' COMMENT '库存数量',
  `stock_warning` int DEFAULT '10' COMMENT '库存预警值',
  `image_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图片',
  `description` text COLLATE utf8mb4_general_ci COMMENT 'description',
  `detail_images` text COLLATE utf8mb4_general_ci COMMENT 'detail images',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-下架 1-上架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'雅马哈立式钢琴 YU118','INSTRUMENT',1,'雅马哈','YU118','胡桃木色',NULL,32800.00,25000.00,1.00,5,2,'/uploads/product_piano_upright.jpg',NULL,NULL,'日本雅马哈立式钢琴，88键，音色纯正，适合家用练习',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `product` VALUES (2,'珠江三角钢琴 GP148','INSTRUMENT',1,'珠江','GP148','亮光黑',NULL,68000.00,52000.00,0.95,2,1,'/uploads/product_piano_grand.jpg',NULL,NULL,'国产精品三角钢琴，演奏级音色，适合教学和演出',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `product` VALUES (3,'雅马哈民谣吉他 FG800','INSTRUMENT',2,'雅马哈','FG800','原木色',NULL,1580.00,1100.00,1.00,20,5,'/uploads/product_guitar_folk.jpg',NULL,NULL,'全单板民谣吉他，音色温暖饱满，新手首选',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `product` VALUES (4,'泰勒 Taylor 114ce 吉他','INSTRUMENT',2,'Taylor','114ce','原木色',NULL,5600.00,4200.00,1.00,8,3,'/uploads/product_guitar_folk.jpg',NULL,NULL,'美国泰勒吉他，电箱民谣吉他，音色明亮，适合演出',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `product` VALUES (5,'芬达 Fender 电吉他 Player','INSTRUMENT',2,'Fender','Player','奇迹白',NULL,6800.00,5200.00,0.95,3,2,'/uploads/product_guitar_electric.jpg',NULL,NULL,'Fender经典系列电吉他，音色多变，适合摇滚、布鲁斯风格',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `product` VALUES (6,'红棉小提琴 V008','INSTRUMENT',3,'红棉','V008','棕色',NULL,2200.00,1500.00,1.00,10,3,'/uploads/product_violin.jpg',NULL,NULL,'欧洲云杉面板，音色明亮，适合初学者和考级使用',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `product` VALUES (7,'金音古筝 163','INSTRUMENT',4,'金音','163','红木色',NULL,3800.00,2800.00,1.00,6,2,'/uploads/product_guzheng.jpg',NULL,NULL,'专业演奏级古筝，音色圆润通透，适合考级与演出',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `product` VALUES (8,'敦煌古筝 694KK','INSTRUMENT',4,'敦煌','694KK','红木色',NULL,5200.00,4000.00,0.90,4,2,'/uploads/product_guzheng.jpg',NULL,NULL,'敦煌经典型号，上海民族乐器一厂出品，专业演奏级',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `product` VALUES (9,'《拜厄钢琴基本教程》','TEXTBOOK',6,NULL,NULL,NULL,'拜厄',45.00,30.00,1.00,50,10,'/uploads/product_textbook1.jpg',NULL,NULL,'钢琴初学必备教材，系统学习基本指法与乐理',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `product` VALUES (10,'《车尔尼599钢琴初步教程》','TEXTBOOK',7,NULL,NULL,NULL,'车尔尼',38.00,25.00,1.00,40,10,'/uploads/product_textbook1.jpg',NULL,NULL,'钢琴进阶练习曲集，提升手指灵活度与技巧',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `product` VALUES (11,'《全国钢琴演奏考级作品集》','TEXTBOOK',7,NULL,NULL,NULL,'韩林申',68.00,48.00,1.00,30,5,'/uploads/product_textbook2.jpg',NULL,NULL,'音协考级官方指定教材，1-10级考级曲目',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `product` VALUES (12,'《吉他自学三月通》','TEXTBOOK',6,NULL,NULL,NULL,'刘传',42.00,28.00,1.00,35,10,'/uploads/product_textbook2.jpg',NULL,NULL,'吉他入门自学经典教材，图文并茂，通俗易懂',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `product` VALUES (13,'《小提琴基础教程》','TEXTBOOK',6,NULL,NULL,NULL,'赵薪',55.00,38.00,1.00,25,5,'/uploads/product_textbook1.jpg',NULL,NULL,'小提琴入门经典教材，包含基本弓法与练习曲',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `product` VALUES (14,'《乐理知识基础教程》','TEXTBOOK',6,NULL,NULL,NULL,'李重光',35.00,22.00,1.00,60,10,'/uploads/product_textbook2.jpg',NULL,NULL,'音乐理论基础知识，适合所有乐器学习者',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `product` VALUES (15,'《全国古筝演奏考级作品集》','TEXTBOOK',7,NULL,NULL,NULL,'王中山',58.00,40.00,1.00,20,5,'/uploads/product_textbook2.jpg',NULL,NULL,'音协古筝考级官方教材，1-10级曲目',1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_record`
--

DROP TABLE IF EXISTS `stock_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `type` varchar(10) COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型：IN/OUT',
  `quantity` int NOT NULL COMMENT '数量',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='出入库记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_record`
--

LOCK TABLES `stock_record` WRITE;
/*!40000 ALTER TABLE `stock_record` DISABLE KEYS */;
INSERT INTO `stock_record` VALUES (1,1,'IN',5,1,'初始入库','2025-12-01 09:00:00');
INSERT INTO `stock_record` VALUES (2,2,'IN',2,1,'初始入库','2025-12-01 09:00:00');
INSERT INTO `stock_record` VALUES (3,3,'IN',20,1,'初始入库','2025-12-01 09:30:00');
INSERT INTO `stock_record` VALUES (4,4,'IN',8,1,'初始入库','2025-12-01 09:30:00');
INSERT INTO `stock_record` VALUES (5,5,'IN',3,1,'初始入库','2025-12-01 10:00:00');
INSERT INTO `stock_record` VALUES (6,6,'IN',10,1,'初始入库','2025-12-01 10:00:00');
INSERT INTO `stock_record` VALUES (7,7,'IN',6,1,'初始入库','2025-12-01 10:30:00');
INSERT INTO `stock_record` VALUES (8,8,'IN',4,1,'初始入库','2025-12-01 10:30:00');
INSERT INTO `stock_record` VALUES (9,9,'IN',50,1,'教材入库','2025-12-05 14:00:00');
INSERT INTO `stock_record` VALUES (10,10,'IN',40,1,'教材入库','2025-12-05 14:00:00');
INSERT INTO `stock_record` VALUES (11,11,'IN',30,1,'教材入库','2025-12-05 14:30:00');
INSERT INTO `stock_record` VALUES (12,12,'IN',35,1,'教材入库','2025-12-05 14:30:00');
INSERT INTO `stock_record` VALUES (13,3,'OUT',2,1,'销售出库','2026-01-15 11:00:00');
INSERT INTO `stock_record` VALUES (14,6,'OUT',1,1,'销售出库','2026-01-20 14:00:00');
INSERT INTO `stock_record` VALUES (15,9,'OUT',5,1,'教材销售出库','2026-02-01 10:00:00');
/*!40000 ALTER TABLE `stock_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_course_package`
--

DROP TABLE IF EXISTS `student_course_package`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_course_package` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `student_id` bigint NOT NULL COMMENT 'student id',
  `course_id` bigint NOT NULL COMMENT 'course id',
  `teacher_id` bigint DEFAULT NULL COMMENT 'teacher id',
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'package name',
  `total_hours` int NOT NULL COMMENT 'total hours',
  `remaining_hours` int NOT NULL COMMENT 'remaining hours',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT 'price',
  `status` tinyint DEFAULT '1' COMMENT 'status: 1-enabled 0-disabled',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'remark',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='student course package';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_course_package`
--

LOCK TABLES `student_course_package` WRITE;
/*!40000 ALTER TABLE `student_course_package` DISABLE KEYS */;
INSERT INTO `student_course_package` VALUES (1,7,1,2,'钢琴入门12节测试包',12,12,2400.00,1,'2026-05业务测试数据：订单ORD202605TD001支付后生成','2026-05-01 09:20:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (2,7,3,2,'钢琴考级冲刺8节测试包',8,8,3400.00,1,'2026-05业务测试数据：订单ORD202605TD002支付后生成','2026-05-01 09:35:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (3,7,9,6,'声乐基础8节测试包',8,8,1440.00,1,'2026-05业务测试数据：订单ORD202605TD003支付后生成','2026-05-01 09:50:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (4,8,2,2,'钢琴进阶10节测试包',10,10,2700.00,1,'2026-05业务测试数据：订单ORD202605TD004支付后生成','2026-05-01 10:15:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (5,8,6,4,'小提琴入门10节测试包',10,10,2500.00,1,'2026-05业务测试数据：订单ORD202605TD005支付后生成','2026-05-01 10:35:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (6,8,8,5,'古筝入门8节测试包',8,8,1520.00,1,'2026-05业务测试数据：订单ORD202605TD006支付后生成','2026-05-01 10:55:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (7,9,4,3,'吉他入门12节测试包',12,12,1800.00,1,'2026-05业务测试数据：订单ORD202605TD007支付后生成','2026-05-02 09:15:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (8,9,10,6,'声乐进阶8节测试包',8,8,2016.00,1,'2026-05业务测试数据：订单ORD202605TD008支付后生成','2026-05-02 09:35:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (9,10,5,3,'吉他弹唱10节测试包',10,10,1900.00,1,'2026-05业务测试数据：订单ORD202605TD009支付后生成','2026-05-02 10:10:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (10,10,1,2,'钢琴入门8节测试包',8,8,1600.00,1,'2026-05业务测试数据：订单ORD202605TD010支付后生成','2026-05-02 10:30:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (11,10,9,6,'声乐基础8节测试包',8,8,1440.00,1,'2026-05业务测试数据：订单ORD202605TD011支付后生成','2026-05-02 10:50:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (12,11,6,4,'小提琴入门12节测试包',12,12,3000.00,1,'2026-05业务测试数据：订单ORD202605TD012支付后生成','2026-05-03 09:10:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (13,11,8,5,'古筝入门10节测试包',10,10,1900.00,1,'2026-05业务测试数据：订单ORD202605TD013支付后生成','2026-05-03 09:35:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (14,12,8,5,'古筝入门12节测试包',12,12,2280.00,1,'2026-05业务测试数据：订单ORD202605TD014支付后生成','2026-05-03 10:05:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (15,12,4,3,'吉他入门10节测试包',10,10,1500.00,1,'2026-05业务测试数据：订单ORD202605TD015支付后生成','2026-05-03 10:25:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (16,13,1,2,'钢琴入门10节测试包',10,10,2000.00,1,'2026-05业务测试数据：订单ORD202605TD016支付后生成','2026-05-04 09:15:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (17,13,9,6,'声乐基础10节测试包',10,10,1800.00,1,'2026-05业务测试数据：订单ORD202605TD017支付后生成','2026-05-04 09:40:00','2026-05-13 22:24:50');
INSERT INTO `student_course_package` VALUES (18,13,7,4,'小提琴进阶8节测试包',8,8,2520.00,1,'2026-05业务测试数据：订单ORD202605TD018支付后生成','2026-05-04 10:05:00','2026-05-13 22:24:50');
/*!40000 ALTER TABLE `student_course_package` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码（MD5）',
  `role` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色：STUDENT/TEACHER/ADMIN',
  `name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '姓名',
  `phone` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系方式',
  `gender` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '性别',
  `age` int DEFAULT NULL COMMENT '年龄',
  `avatar` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像路径',
  `teacher_student_limit` int DEFAULT '0' COMMENT '?????????0????',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用 1-正常 2-待审核',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','e10adc3949ba59abbe56e057f20f883e','ADMIN','系统管理员','13800000000','男',30,'/uploads/d08cfd24e91f439088b766ebe5c91ad2.jpg',0,1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `sys_user` VALUES (2,'teacher01','e10adc3949ba59abbe56e057f20f883e','TEACHER','张雅琴','13800000001','女',28,NULL,20,1,'2026-04-11 09:35:48','2026-05-13 21:56:18');
INSERT INTO `sys_user` VALUES (3,'teacher02','e10adc3949ba59abbe56e057f20f883e','TEACHER','李明轩','13800000002','男',35,NULL,20,1,'2026-04-11 09:35:48','2026-05-13 21:56:18');
INSERT INTO `sys_user` VALUES (4,'teacher03','e10adc3949ba59abbe56e057f20f883e','TEACHER','王婉清','13800000005','女',32,NULL,20,1,'2026-04-11 09:35:48','2026-05-13 21:56:18');
INSERT INTO `sys_user` VALUES (5,'teacher04','e10adc3949ba59abbe56e057f20f883e','TEACHER','陈志远','13800000006','男',40,NULL,20,1,'2026-04-11 09:35:48','2026-05-13 21:56:18');
INSERT INTO `sys_user` VALUES (6,'teacher05','e10adc3949ba59abbe56e057f20f883e','TEACHER','周佳音','13800000007','女',30,NULL,20,1,'2026-04-11 09:35:48','2026-05-13 21:56:18');
INSERT INTO `sys_user` VALUES (7,'student01','e10adc3949ba59abbe56e057f20f883e','STUDENT','王子豪','13800000003','男',18,NULL,0,1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `sys_user` VALUES (8,'student02','e10adc3949ba59abbe56e057f20f883e','STUDENT','赵雨萱','13800000004','女',20,NULL,0,1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `sys_user` VALUES (9,'student03','e10adc3949ba59abbe56e057f20f883e','STUDENT','李思涵','13800000008','女',16,NULL,0,1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `sys_user` VALUES (10,'student04','e10adc3949ba59abbe56e057f20f883e','STUDENT','张浩然','13800000009','男',19,NULL,0,1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `sys_user` VALUES (11,'student05','e10adc3949ba59abbe56e057f20f883e','STUDENT','刘雅琳','13800000010','女',17,NULL,0,1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `sys_user` VALUES (12,'student06','e10adc3949ba59abbe56e057f20f883e','STUDENT','陈子轩','13800000011','男',22,NULL,0,1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
INSERT INTO `sys_user` VALUES (13,'student07','e10adc3949ba59abbe56e057f20f883e','STUDENT','周悦然','13800000012','女',15,NULL,0,1,'2026-04-11 09:35:48','2026-04-11 09:35:48');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_qualification`
--

DROP TABLE IF EXISTS `teacher_qualification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_qualification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `type` varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型：SCHOOL/LEVEL/HONOR/COMPETITION',
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '资质名称',
  `image_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '证书图片',
  `description` text COLLATE utf8mb4_general_ci COMMENT '描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师资质表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_qualification`
--

LOCK TABLES `teacher_qualification` WRITE;
/*!40000 ALTER TABLE `teacher_qualification` DISABLE KEYS */;
INSERT INTO `teacher_qualification` VALUES (1,2,'SCHOOL','中央音乐学院钢琴演奏硕士','/uploads/qual1.jpg','中央音乐学院钢琴演奏专业硕士研究生，师从著名钢琴家张建民教授','2026-04-11 09:35:48');
INSERT INTO `teacher_qualification` VALUES (2,2,'LEVEL','钢琴演奏十级证书','/uploads/qual2.jpg','中国音乐家协会钢琴演奏十级，优秀成绩通过','2026-04-11 09:35:48');
INSERT INTO `teacher_qualification` VALUES (3,2,'HONOR','全国钢琴比赛金奖','/uploads/qual3.jpg','第十五届星海杯全国钢琴比赛专业组金奖','2026-04-11 09:35:48');
INSERT INTO `teacher_qualification` VALUES (4,3,'SCHOOL','武汉音乐学院吉他演奏本科','/uploads/qual1.jpg','武汉音乐学院吉他演奏专业本科毕业','2026-04-11 09:35:48');
INSERT INTO `teacher_qualification` VALUES (5,3,'LEVEL','吉他演奏八级证书','/uploads/qual2.jpg','中国音乐家协会吉他演奏八级','2026-04-11 09:35:48');
INSERT INTO `teacher_qualification` VALUES (6,4,'SCHOOL','上海音乐学院小提琴演奏硕士','/uploads/qual1.jpg','上海音乐学院小提琴演奏专业硕士','2026-04-11 09:35:48');
INSERT INTO `teacher_qualification` VALUES (7,4,'LEVEL','小提琴演奏十级证书','/uploads/qual2.jpg','中国音乐家协会小提琴演奏十级','2026-04-11 09:35:48');
INSERT INTO `teacher_qualification` VALUES (8,4,'HONOR','省级优秀音乐教师','/uploads/qual3.jpg','湖北省优秀音乐教师称号','2026-04-11 09:35:48');
INSERT INTO `teacher_qualification` VALUES (9,5,'SCHOOL','中国音乐学院古筝演奏本科','/uploads/qual1.jpg','中国音乐学院古筝演奏专业本科','2026-04-11 09:35:48');
INSERT INTO `teacher_qualification` VALUES (10,5,'LEVEL','古筝演奏九级证书','/uploads/qual2.jpg','中国音乐家协会古筝演奏九级','2026-04-11 09:35:48');
INSERT INTO `teacher_qualification` VALUES (11,6,'SCHOOL','四川音乐学院声乐表演硕士','/uploads/qual1.jpg','四川音乐学院声乐表演专业硕士','2026-04-11 09:35:48');
INSERT INTO `teacher_qualification` VALUES (12,6,'LEVEL','声乐演唱十级证书','/uploads/qual3.jpg','中国音乐家协会声乐演唱十级，优秀成绩通过','2026-04-11 09:35:48');
/*!40000 ALTER TABLE `teacher_qualification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_schedule_slot`
--

DROP TABLE IF EXISTS `teacher_schedule_slot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_schedule_slot` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `teacher_id` bigint NOT NULL COMMENT 'Teacher ID',
  `classroom_id` bigint DEFAULT NULL COMMENT 'Classroom ID',
  `slot_time` datetime NOT NULL COMMENT 'Slot start time',
  `duration` int NOT NULL DEFAULT '60' COMMENT 'Duration in minutes',
  `status` tinyint DEFAULT '1' COMMENT 'Status: 0-disabled 1-enabled',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Remark',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  PRIMARY KEY (`id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_slot_time` (`slot_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Teacher schedule slot';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_schedule_slot`
--

LOCK TABLES `teacher_schedule_slot` WRITE;
/*!40000 ALTER TABLE `teacher_schedule_slot` DISABLE KEYS */;
INSERT INTO `teacher_schedule_slot` VALUES (1,2,1,'2026-05-09 10:00:00',60,1,'钢琴基础周末上午时段','2026-04-26 23:17:14','2026-04-26 23:17:14');
INSERT INTO `teacher_schedule_slot` VALUES (2,2,1,'2026-05-09 11:15:00',60,1,'钢琴基础周末上午时段','2026-04-26 23:17:14','2026-04-26 23:17:14');
INSERT INTO `teacher_schedule_slot` VALUES (3,2,2,'2026-05-10 14:00:00',60,1,'钢琴进阶周日下午时段','2026-04-26 23:17:14','2026-04-26 23:17:14');
INSERT INTO `teacher_schedule_slot` VALUES (4,2,2,'2026-05-10 15:15:00',60,1,'钢琴进阶周日下午时段','2026-04-26 23:17:14','2026-04-26 23:17:14');
INSERT INTO `teacher_schedule_slot` VALUES (5,3,3,'2026-05-08 19:00:00',60,1,'吉他晚间常规课','2026-04-26 23:17:14','2026-04-26 23:17:14');
INSERT INTO `teacher_schedule_slot` VALUES (6,3,3,'2026-05-10 10:00:00',60,1,'吉他周末上午时段','2026-04-26 23:17:14','2026-04-26 23:17:14');
INSERT INTO `teacher_schedule_slot` VALUES (7,3,3,'2026-05-10 11:15:00',60,1,'吉他周末上午时段','2026-04-26 23:17:14','2026-04-26 23:17:14');
INSERT INTO `teacher_schedule_slot` VALUES (8,4,4,'2026-05-09 09:00:00',60,1,'小提琴周末上午时段','2026-04-26 23:17:14','2026-04-26 23:17:14');
INSERT INTO `teacher_schedule_slot` VALUES (9,4,4,'2026-05-09 10:15:00',60,1,'小提琴周末上午时段','2026-04-26 23:17:14','2026-04-26 23:17:14');
INSERT INTO `teacher_schedule_slot` VALUES (10,5,5,'2026-05-10 09:30:00',60,1,'古筝周末上午时段','2026-04-26 23:17:14','2026-04-26 23:17:14');
INSERT INTO `teacher_schedule_slot` VALUES (11,5,5,'2026-05-10 10:45:00',60,1,'古筝周末上午时段','2026-04-26 23:17:14','2026-04-26 23:17:14');
INSERT INTO `teacher_schedule_slot` VALUES (12,6,6,'2026-05-08 18:30:00',45,1,'声乐工作日晚间时段','2026-04-26 23:17:14','2026-04-26 23:17:14');
INSERT INTO `teacher_schedule_slot` VALUES (13,6,6,'2026-05-10 15:00:00',45,1,'声乐周日下午时段','2026-04-26 23:17:14','2026-04-26 23:17:14');
INSERT INTO `teacher_schedule_slot` VALUES (14,6,6,'2026-05-10 16:00:00',45,1,'声乐周日下午时段','2026-04-26 23:17:14','2026-04-26 23:17:14');
INSERT INTO `teacher_schedule_slot` VALUES (30,2,1,'2026-05-14 17:30:00',60,1,'2026-05业务测试数据：张雅琴钢琴课可约时段','2026-05-05 09:00:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (31,2,2,'2026-05-14 18:45:00',60,1,'2026-05业务测试数据：张雅琴钢琴课可约时段','2026-05-05 09:00:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (32,2,1,'2026-05-15 17:30:00',90,1,'2026-05业务测试数据：张雅琴钢琴考级课时段','2026-05-05 09:00:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (33,2,1,'2026-05-16 09:00:00',60,1,'2026-05业务测试数据：张雅琴周末上午钢琴时段','2026-05-05 09:00:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (34,2,2,'2026-05-16 10:15:00',60,1,'2026-05业务测试数据：张雅琴周末上午钢琴时段','2026-05-05 09:00:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (35,2,1,'2026-05-17 14:00:00',60,1,'2026-05业务测试数据：张雅琴周日下午钢琴时段','2026-05-05 09:00:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (36,2,2,'2026-05-18 19:00:00',60,1,'2026-05业务测试数据：张雅琴晚间钢琴时段','2026-05-05 09:00:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (37,2,1,'2026-05-20 17:30:00',90,1,'2026-05业务测试数据：张雅琴钢琴考级课时段','2026-05-05 09:00:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (38,2,2,'2026-05-21 18:45:00',60,1,'2026-05业务测试数据：张雅琴钢琴课可约时段','2026-05-05 09:00:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (39,2,1,'2026-05-23 09:00:00',60,1,'2026-05业务测试数据：张雅琴周末上午钢琴时段','2026-05-05 09:00:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (40,2,2,'2026-05-23 10:15:00',60,1,'2026-05业务测试数据：张雅琴周末上午钢琴时段','2026-05-05 09:00:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (41,2,1,'2026-05-24 14:00:00',90,1,'2026-05业务测试数据：张雅琴钢琴考级课时段','2026-05-05 09:00:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (42,2,2,'2026-05-26 18:45:00',60,1,'2026-05业务测试数据：张雅琴钢琴课可约时段','2026-05-05 09:00:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (43,2,1,'2026-05-28 17:30:00',60,1,'2026-05业务测试数据：张雅琴钢琴课可约时段','2026-05-05 09:00:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (44,2,1,'2026-05-30 09:00:00',60,1,'2026-05业务测试数据：张雅琴周末上午钢琴时段','2026-05-05 09:00:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (45,3,3,'2026-05-14 19:00:00',60,1,'2026-05业务测试数据：李明轩吉他课可约时段','2026-05-05 09:05:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (46,3,3,'2026-05-15 18:45:00',60,1,'2026-05业务测试数据：李明轩吉他弹唱时段','2026-05-05 09:05:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (47,3,3,'2026-05-16 11:30:00',60,1,'2026-05业务测试数据：李明轩周末吉他时段','2026-05-05 09:05:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (48,3,3,'2026-05-17 10:00:00',60,1,'2026-05业务测试数据：李明轩周末吉他时段','2026-05-05 09:05:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (49,3,3,'2026-05-19 18:30:00',60,1,'2026-05业务测试数据：李明轩吉他弹唱时段','2026-05-05 09:05:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (50,3,3,'2026-05-20 19:00:00',60,1,'2026-05业务测试数据：李明轩吉他课可约时段','2026-05-05 09:05:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (51,3,3,'2026-05-22 18:45:00',60,1,'2026-05业务测试数据：李明轩吉他课可约时段','2026-05-05 09:05:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (52,3,3,'2026-05-23 11:30:00',60,1,'2026-05业务测试数据：李明轩周末吉他时段','2026-05-05 09:05:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (53,3,3,'2026-05-24 10:00:00',60,1,'2026-05业务测试数据：李明轩周末吉他时段','2026-05-05 09:05:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (54,3,3,'2026-05-27 19:00:00',60,1,'2026-05业务测试数据：李明轩吉他课可约时段','2026-05-05 09:05:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (55,3,3,'2026-05-29 18:45:00',60,1,'2026-05业务测试数据：李明轩吉他课可约时段','2026-05-05 09:05:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (56,3,3,'2026-05-30 10:15:00',60,1,'2026-05业务测试数据：李明轩周末吉他时段','2026-05-05 09:05:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (57,4,4,'2026-05-14 17:00:00',60,1,'2026-05业务测试数据：王婉清小提琴课可约时段','2026-05-05 09:10:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (58,4,4,'2026-05-15 19:15:00',60,1,'2026-05业务测试数据：王婉清小提琴课可约时段','2026-05-05 09:10:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (59,4,4,'2026-05-16 14:00:00',60,1,'2026-05业务测试数据：王婉清周末小提琴时段','2026-05-05 09:10:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (60,4,4,'2026-05-18 17:30:00',60,1,'2026-05业务测试数据：王婉清小提琴课可约时段','2026-05-05 09:10:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (61,4,4,'2026-05-19 19:45:00',60,1,'2026-05业务测试数据：王婉清小提琴课可约时段','2026-05-05 09:10:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (62,4,4,'2026-05-21 17:30:00',60,1,'2026-05业务测试数据：王婉清小提琴进阶时段','2026-05-05 09:10:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (63,4,4,'2026-05-23 14:00:00',60,1,'2026-05业务测试数据：王婉清周末小提琴时段','2026-05-05 09:10:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (64,4,4,'2026-05-24 15:30:00',60,1,'2026-05业务测试数据：王婉清周末小提琴时段','2026-05-05 09:10:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (65,4,4,'2026-05-26 19:15:00',60,1,'2026-05业务测试数据：王婉清小提琴课可约时段','2026-05-05 09:10:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (66,4,4,'2026-05-28 19:15:00',60,1,'2026-05业务测试数据：王婉清小提琴进阶时段','2026-05-05 09:10:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (67,4,4,'2026-05-30 14:00:00',60,1,'2026-05业务测试数据：王婉清周末小提琴时段','2026-05-05 09:10:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (68,5,5,'2026-05-14 18:15:00',60,1,'2026-05业务测试数据：陈志远古筝课可约时段','2026-05-05 09:15:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (69,5,5,'2026-05-16 15:30:00',60,1,'2026-05业务测试数据：陈志远周末古筝时段','2026-05-05 09:15:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (70,5,5,'2026-05-17 11:30:00',60,1,'2026-05业务测试数据：陈志远周末古筝时段','2026-05-05 09:15:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (71,5,5,'2026-05-19 17:30:00',60,1,'2026-05业务测试数据：陈志远古筝课可约时段','2026-05-05 09:15:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (72,5,5,'2026-05-21 19:00:00',60,1,'2026-05业务测试数据：陈志远古筝课可约时段','2026-05-05 09:15:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (73,5,5,'2026-05-24 11:30:00',60,1,'2026-05业务测试数据：陈志远周末古筝时段','2026-05-05 09:15:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (74,5,5,'2026-05-25 18:15:00',60,1,'2026-05业务测试数据：陈志远古筝课可约时段','2026-05-05 09:15:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (75,5,5,'2026-05-27 17:30:00',60,1,'2026-05业务测试数据：陈志远古筝课可约时段','2026-05-05 09:15:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (76,5,5,'2026-05-29 19:00:00',60,1,'2026-05业务测试数据：陈志远古筝课可约时段','2026-05-05 09:15:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (77,5,5,'2026-05-30 15:30:00',60,1,'2026-05业务测试数据：陈志远周末古筝时段','2026-05-05 09:15:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (78,6,6,'2026-05-14 20:00:00',45,1,'2026-05业务测试数据：周佳音声乐基础时段','2026-05-05 09:20:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (79,6,6,'2026-05-15 20:00:00',60,1,'2026-05业务测试数据：周佳音声乐进阶时段','2026-05-05 09:20:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (80,6,6,'2026-05-16 16:45:00',45,1,'2026-05业务测试数据：周佳音周末声乐时段','2026-05-05 09:20:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (81,6,6,'2026-05-18 20:00:00',45,1,'2026-05业务测试数据：周佳音声乐基础时段','2026-05-05 09:20:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (82,6,6,'2026-05-20 20:00:00',45,1,'2026-05业务测试数据：周佳音声乐基础时段','2026-05-05 09:20:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (83,6,6,'2026-05-22 20:00:00',60,1,'2026-05业务测试数据：周佳音声乐进阶时段','2026-05-05 09:20:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (84,6,6,'2026-05-23 16:00:00',45,1,'2026-05业务测试数据：周佳音周末声乐时段','2026-05-05 09:20:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (85,6,6,'2026-05-24 16:45:00',45,1,'2026-05业务测试数据：周佳音周末声乐时段','2026-05-05 09:20:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (86,6,6,'2026-05-26 20:00:00',45,1,'2026-05业务测试数据：周佳音声乐基础时段','2026-05-05 09:20:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (87,6,6,'2026-05-28 20:00:00',60,1,'2026-05业务测试数据：周佳音声乐进阶时段','2026-05-05 09:20:00','2026-05-13 22:24:50');
INSERT INTO `teacher_schedule_slot` VALUES (88,6,6,'2026-05-30 16:45:00',45,1,'2026-05业务测试数据：周佳音周末声乐时段','2026-05-05 09:20:00','2026-05-13 22:24:50');
/*!40000 ALTER TABLE `teacher_schedule_slot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'piano_store_management'
--

--
-- Dumping routines for database 'piano_store_management'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-17 14:27:17
