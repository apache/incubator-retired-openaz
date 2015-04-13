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
-- Table structure for table `Teams`
--

DROP TABLE IF EXISTS `Teams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Teams` (
  `yearID` smallint(4) unsigned NOT NULL default '0',
  `lgID` char(2) NOT NULL default '',
  `teamID` char(3) NOT NULL default '',
  `franchID` char(3) NOT NULL default 'UNK',
  `divID` char(1) default NULL,
  `Rank` smallint(3) unsigned NOT NULL default '0',
  `G` smallint(3) unsigned default NULL,
  `Ghome` int(3) default NULL,
  `W` smallint(3) unsigned default NULL,
  `L` smallint(3) unsigned default NULL,
  `DivWin` enum('Y','N') default NULL,
  `WCWin` enum('Y','N') default NULL,
  `LgWin` enum('Y','N') default NULL,
  `WSWin` enum('Y','N') default NULL,
  `R` smallint(4) unsigned default NULL,
  `AB` smallint(4) unsigned default NULL,
  `H` smallint(4) unsigned default NULL,
  `2B` smallint(4) unsigned default NULL,
  `3B` smallint(3) unsigned default NULL,
  `HR` smallint(3) unsigned default NULL,
  `BB` smallint(4) unsigned default NULL,
  `SO` smallint(4) unsigned default NULL,
  `SB` smallint(3) unsigned default NULL,
  `CS` smallint(3) unsigned default NULL,
  `HBP` smallint(3) default NULL,
  `SF` smallint(3) default NULL,
  `RA` smallint(4) unsigned default NULL,
  `ER` smallint(4) default NULL,
  `ERA` decimal(4,2) default NULL,
  `CG` smallint(3) unsigned default NULL,
  `SHO` smallint(3) unsigned default NULL,
  `SV` smallint(3) unsigned default NULL,
  `IPouts` int(5) default NULL,
  `HA` smallint(4) unsigned default NULL,
  `HRA` smallint(4) unsigned default NULL,
  `BBA` smallint(4) unsigned default NULL,
  `SOA` smallint(4) unsigned default NULL,
  `E` int(5) default NULL,
  `DP` int(4) default NULL,
  `FP` decimal(5,3) default NULL,
  `name` varchar(50) NOT NULL default '',
  `park` varchar(255) default NULL,
  `attendance` int(7) default NULL,
  `BPF` int(3) default NULL,
  `PPF` int(3) default NULL,
  `teamIDBR` char(3) default NULL,
  `teamIDlahman45` char(3) default NULL,
  `teamIDretro` char(3) default NULL,
  PRIMARY KEY  (`yearID`,`lgID`,`teamID`),
  KEY `team` (`teamID`,`yearID`,`lgID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-03-28 17:12:25
