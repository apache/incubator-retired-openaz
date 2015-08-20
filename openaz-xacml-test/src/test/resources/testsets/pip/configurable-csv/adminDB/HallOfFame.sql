-- MySQL dump 10.11
--
-- Host: dbserver    Database: brdev
-- ------------------------------------------------------
-- Server version	5.0.87-percona-highperf-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `HallOfFame`
--

DROP TABLE IF EXISTS `HallOfFame`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HallOfFame` (
  `hofID` varchar(10) NOT NULL default '',
  `yearID` smallint(4) NOT NULL default '0',
  `votedBy` varchar(64) NOT NULL default '',
  `ballots` smallint(5) default NULL,
  `needed` varchar(20) default NULL,
  `votes` float(5,1) default NULL,
  `inducted` enum('Y','N') default 'N',
  `category` varchar(20) default NULL,
  PRIMARY KEY  (`hofID`,`yearID`,`votedBy`),
  KEY `hofID` (`hofID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-03-28 17:12:27
