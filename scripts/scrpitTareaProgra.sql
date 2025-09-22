-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               11.8.2-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.10.0.7000
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Dumping structure for table rutinasdb.completedactivity
CREATE TABLE IF NOT EXISTS `completedactivity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `routine_id` bigint(20) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `completedAt` varchar(10) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `habit_id` bigint(20) DEFAULT NULL,
  `completed_at` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `routine_id` (`routine_id`),
  KEY `completedactivity_ibfk_3` (`habit_id`),
  KEY `idx_completed_date_id` (`date`,`id`),
  KEY `idx_completed_user_date_id` (`user_id`,`date`,`id`),
  KEY `idx_completed_habit_date_id` (`habit_id`,`date`,`id`),
  KEY `idx_completed_routine_date_id` (`routine_id`,`date`,`id`),
  CONSTRAINT `completedactivity_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `completedactivity_ibfk_2` FOREIGN KEY (`routine_id`) REFERENCES `routine` (`id`),
  CONSTRAINT `completedactivity_ibfk_3` FOREIGN KEY (`habit_id`) REFERENCES `habitactivity` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=524281 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table rutinasdb.favorite_habit
CREATE TABLE IF NOT EXISTS `favorite_habit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `habit_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKjddktt50kb1otesbvqkppkedn` (`user_id`,`habit_id`),
  KEY `FK7i1gaq7a3j5c7sqy15i9b5ahs` (`habit_id`),
  KEY `idx_fav_user_id` (`user_id`,`id`),
  CONSTRAINT `FK7i1gaq7a3j5c7sqy15i9b5ahs` FOREIGN KEY (`habit_id`) REFERENCES `habitactivity` (`id`),
  CONSTRAINT `FK7kjjey9bhxxn4srtdigv9aiy1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16384 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table rutinasdb.guide
CREATE TABLE IF NOT EXISTS `guide` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) DEFAULT NULL,
  `content` tinytext DEFAULT NULL,
  `category` tinytext DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `idx_guide_user_id` (`user_id`,`id`),
  CONSTRAINT `guide_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table rutinasdb.guide_habit
CREATE TABLE IF NOT EXISTS `guide_habit` (
  `guide_id` bigint(20) NOT NULL,
  `habit_id` bigint(20) NOT NULL,
  PRIMARY KEY (`guide_id`,`habit_id`),
  KEY `habit_id` (`habit_id`),
  KEY `idx_guide_habit_habit` (`habit_id`),
  CONSTRAINT `guide_habit_ibfk_1` FOREIGN KEY (`guide_id`) REFERENCES `guide` (`id`),
  CONSTRAINT `guide_habit_ibfk_2` FOREIGN KEY (`habit_id`) REFERENCES `habitactivity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table rutinasdb.habitactivity
CREATE TABLE IF NOT EXISTS `habitactivity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `targetTime` time DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `is_favorite` bit(1) DEFAULT NULL,
  `target_time` time(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table rutinasdb.password_reset_token
CREATE TABLE IF NOT EXISTS `password_reset_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `token` varchar(128) NOT NULL,
  `used` bit(1) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_password_reset_token` (`token`),
  KEY `idx_prt_user_expires` (`user_id`,`expires_at`),
  CONSTRAINT `FK5lwtbncug84d4ero33v3cfxvl` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table rutinasdb.reminder
CREATE TABLE IF NOT EXISTS `reminder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `habit_id` bigint(20) DEFAULT NULL,
  `time` varchar(10) DEFAULT NULL,
  `frequency` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `habit_id` (`habit_id`),
  KEY `idx_reminder_user_id` (`user_id`,`id`),
  KEY `idx_reminder_habit_id` (`habit_id`,`id`),
  CONSTRAINT `reminder_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `reminder_ibfk_2` FOREIGN KEY (`habit_id`) REFERENCES `habitactivity` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16384 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table rutinasdb.routine
CREATE TABLE IF NOT EXISTS `routine` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `daysOfWeek` varchar(20) DEFAULT NULL,
  `days_of_week` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `idx_routine_user_id` (`user_id`,`id`),
  CONSTRAINT `routine_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table rutinasdb.routine_habit
CREATE TABLE IF NOT EXISTS `routine_habit` (
  `routine_id` bigint(20) NOT NULL,
  `habit_id` bigint(20) NOT NULL,
  `order_in_routine` int(11) DEFAULT 1,
  `target_time_in_routine` time DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`routine_id`,`habit_id`),
  KEY `routine_id` (`routine_id`),
  KEY `habit_id` (`habit_id`),
  KEY `idx_routine_habit_order` (`routine_id`,`order_in_routine`,`habit_id`),
  CONSTRAINT `fk_routine_habit_habit` FOREIGN KEY (`habit_id`) REFERENCES `habitactivity` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_routine_habit_routine` FOREIGN KEY (`routine_id`) REFERENCES `routine` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table rutinasdb.user
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `is_auditor` tinyint(1) NOT NULL DEFAULT 0,
  `is_coach` tinyint(1) NOT NULL DEFAULT 0,
  `role` varchar(50) DEFAULT NULL,
  `assigned_coach_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `uq_user_email` (`email`),
  KEY `idx_user_assigned_coach` (`assigned_coach_id`),
  KEY `idx_user_name_id` (`name`,`id`),
  CONSTRAINT `fk_user_assigned_coach` FOREIGN KEY (`assigned_coach_id`) REFERENCES `user` (`id`) ON DELETE SET NULL,
  CONSTRAINT `chk_user_auditor_xor_coach` CHECK (`is_auditor` <> 1 or `is_coach` <> 1)
) ENGINE=InnoDB AUTO_INCREMENT=258 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table rutinasdb.user_module_permission
CREATE TABLE IF NOT EXISTS `user_module_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `module` enum('GUIDES','HABITS','PROGRESS','REMINDERS','ROUTINES','USERS') NOT NULL,
  `permission` enum('CONSULT','MUTATE') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_ump` (`user_id`,`module`),
  UNIQUE KEY `UK48y9xcxd0fq79cfla5y1yuhjq` (`user_id`,`module`),
  CONSTRAINT `fk_ump_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=644 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Data exporting was unselected.

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
