-- MariaDB dump 10.19  Distrib 10.9.2-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: jiccns
-- ------------------------------------------------------
-- Server version	10.9.2-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `exp_summery`
--

DROP TABLE IF EXISTS `exp_summery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exp_summery` (
  `total_network_time` int(11) DEFAULT NULL,
  `total_network_usage` int(11) DEFAULT NULL,
  `total_answer_first_time` int(11) DEFAULT NULL,
  `total_asks` int(11) DEFAULT NULL,
  `total_minimum_hopcount` int(11) DEFAULT NULL,
  `epoch` int(11) DEFAULT NULL,
  `exp_id` int(11) DEFAULT NULL,
  KEY `exp_id` (`exp_id`),
  CONSTRAINT `exp_summery_ibfk_1` FOREIGN KEY (`exp_id`) REFERENCES `experiments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exp_summery`
--

LOCK TABLES `exp_summery` WRITE;
/*!40000 ALTER TABLE `exp_summery` DISABLE KEYS */;
/*!40000 ALTER TABLE `exp_summery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `experiments`
--

DROP TABLE IF EXISTS `experiments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `experiments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `hashed` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `epochtime` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hashed` (`hashed`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `experiments`
--

LOCK TABLES `experiments` WRITE;
/*!40000 ALTER TABLE `experiments` DISABLE KEYS */;
/*!40000 ALTER TABLE `experiments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nodesummary`
--

DROP TABLE IF EXISTS `nodesummary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nodesummary` (
  `nodename` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nodetype` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `exp_id` int(11) DEFAULT NULL,
  `numberofrequestshandled` int(11) DEFAULT NULL,
  `numberofrequestsansweredBYME` int(11) DEFAULT NULL,
  `numberofrequestsforwarded` int(11) DEFAULT NULL,
  `numberofcachehits` int(11) DEFAULT NULL,
  `numberofcachemiss` int(11) DEFAULT NULL,
  `numberofcachelookups` int(11) DEFAULT NULL,
  `numberofhddhits` int(11) DEFAULT NULL,
  `numberofhddmiss` int(11) DEFAULT NULL,
  `numberofhddlookups` int(11) DEFAULT NULL,
  `cache_enq_count` int(11) DEFAULT NULL,
  `cache_dq_count` int(11) DEFAULT NULL,
  `req_answer_forwarded_count` int(11) DEFAULT NULL,
  `pc` float DEFAULT NULL,
  `epochtime` int(11) DEFAULT NULL,
  KEY `exp_id` (`exp_id`),
  CONSTRAINT `nodesummary_ibfk_1` FOREIGN KEY (`exp_id`) REFERENCES `experiments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nodesummary`
--

LOCK TABLES `nodesummary` WRITE;
/*!40000 ALTER TABLE `nodesummary` DISABLE KEYS */;
/*!40000 ALTER TABLE `nodesummary` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-12-18 17:07:05
