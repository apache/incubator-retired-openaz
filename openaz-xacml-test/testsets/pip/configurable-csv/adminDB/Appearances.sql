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
-- Table structure for table `Appearances`
--

DROP TABLE IF EXISTS `Appearances`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Appearances` (
  `yearID` smallint(4) NOT NULL default '0',
  `teamID` char(3) NOT NULL default '',
  `lgID` char(2) default NULL,
  `playerID` char(9) NOT NULL default '',
  `experience` tinyint(2) unsigned default NULL,
  `G_all` tinyint(3) unsigned default NULL,
  `GS` tinyint(3) unsigned default NULL,
  `G_batting` tinyint(3) unsigned default NULL,
  `G_defense` tinyint(3) unsigned default NULL,
  `G_p` tinyint(3) unsigned default NULL,
  `G_c` tinyint(3) unsigned default NULL,
  `G_1b` tinyint(3) unsigned default NULL,
  `G_2b` tinyint(3) unsigned default NULL,
  `G_3b` tinyint(3) unsigned default NULL,
  `G_ss` tinyint(3) unsigned default NULL,
  `G_lf` tinyint(3) unsigned default NULL,
  `G_cf` tinyint(3) unsigned default NULL,
  `G_rf` tinyint(3) unsigned default NULL,
  `G_of` tinyint(3) unsigned default NULL,
  `G_dh` tinyint(3) unsigned default NULL,
  `G_ph` tinyint(3) unsigned default NULL,
  `G_pr` tinyint(3) unsigned default NULL,
  PRIMARY KEY  (`yearID`,`teamID`,`playerID`),
  KEY `playerID` (`playerID`,`yearID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-03-28 17:12:27
