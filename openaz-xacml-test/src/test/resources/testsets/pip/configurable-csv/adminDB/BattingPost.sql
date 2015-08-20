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
-- Table structure for table `BattingPost`
--

DROP TABLE IF EXISTS `BattingPost`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `BattingPost` (
  `yearID` smallint(4) unsigned NOT NULL default '0',
  `round` varchar(10) NOT NULL default '',
  `playerID` varchar(9) NOT NULL default '',
  `teamID` char(3) NOT NULL default '',
  `lgID` char(2) NOT NULL default '',
  `G` smallint(3) unsigned default NULL,
  `AB` smallint(3) NOT NULL default '0',
  `R` smallint(3) unsigned default NULL,
  `H` smallint(3) unsigned default NULL,
  `2B` smallint(3) unsigned default NULL,
  `3B` smallint(3) unsigned default NULL,
  `HR` smallint(3) unsigned NOT NULL default '0',
  `RBI` smallint(3) unsigned default NULL,
  `SB` smallint(3) unsigned default NULL,
  `CS` smallint(3) unsigned default NULL,
  `BB` smallint(3) unsigned default NULL,
  `SO` smallint(3) unsigned default NULL,
  `IBB` smallint(3) unsigned default NULL,
  `HBP` smallint(3) unsigned default NULL,
  `SH` smallint(3) unsigned default NULL,
  `SF` smallint(3) unsigned default NULL,
  `GIDP` smallint(3) unsigned default NULL,
  PRIMARY KEY  (`yearID`,`round`,`playerID`),
  KEY `playerID` (`playerID`),
  KEY `teamID` (`teamID`,`yearID`,`lgID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-03-28 17:12:27
