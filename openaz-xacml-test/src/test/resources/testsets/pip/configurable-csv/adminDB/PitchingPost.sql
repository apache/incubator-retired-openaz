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
-- Table structure for table `PitchingPost`
--

DROP TABLE IF EXISTS `PitchingPost`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PitchingPost` (
  `playerID` varchar(9) NOT NULL default '',
  `yearID` smallint(4) unsigned NOT NULL default '0',
  `round` varchar(10) NOT NULL default '',
  `teamID` char(3) NOT NULL default '',
  `lgID` char(2) NOT NULL default '',
  `W` smallint(2) unsigned default NULL,
  `L` smallint(2) unsigned default NULL,
  `G` smallint(3) unsigned default NULL,
  `GS` smallint(3) unsigned default NULL,
  `CG` smallint(3) unsigned default NULL,
  `SHO` smallint(3) unsigned default NULL,
  `SV` smallint(3) unsigned default NULL,
  `IPouts` int(5) unsigned default NULL,
  `H` smallint(3) unsigned default NULL,
  `ER` smallint(3) unsigned default NULL,
  `HR` smallint(3) unsigned default NULL,
  `BB` smallint(3) unsigned default NULL,
  `SO` smallint(3) unsigned default NULL,
  `BAOpp` decimal(5,3) unsigned default NULL,
  `ERA` decimal(5,2) unsigned default NULL,
  `IBB` smallint(3) unsigned default NULL,
  `WP` smallint(3) unsigned default NULL,
  `HBP` smallint(3) unsigned default NULL,
  `BK` smallint(3) unsigned default NULL,
  `BFP` smallint(6) unsigned default NULL,
  `GF` smallint(3) unsigned default NULL,
  `R` smallint(3) unsigned default NULL,
  `SH` smallint(3) unsigned default NULL,
  `SF` smallint(3) unsigned default NULL,
  `GIDP` smallint(3) unsigned default NULL,
  PRIMARY KEY  (`playerID`,`yearID`,`round`),
  KEY `player` (`playerID`),
  KEY `player2` (`playerID`,`yearID`,`teamID`,`lgID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-03-28 17:12:27
