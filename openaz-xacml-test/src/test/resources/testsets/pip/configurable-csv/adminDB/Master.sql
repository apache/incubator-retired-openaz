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
-- Table structure for table `Master`
--

DROP TABLE IF EXISTS `Master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Master` (
  `lahmanID` int(9) NOT NULL auto_increment,
  `playerID` varchar(10) NOT NULL default '',
  `managerID` varchar(10) NOT NULL default '',
  `hofID` varchar(10) NOT NULL default '',
  `birthYear` int(4) default NULL,
  `birthMonth` int(2) default NULL,
  `birthDay` int(2) default NULL,
  `birthCountry` varchar(50) default NULL,
  `birthState` char(2) default NULL,
  `birthCity` varchar(50) default NULL,
  `deathYear` int(4) default NULL,
  `deathMonth` int(2) default NULL,
  `deathDay` int(2) default NULL,
  `deathCountry` varchar(50) default NULL,
  `deathState` char(2) default NULL,
  `deathCity` varchar(50) default NULL,
  `nameFirst` varchar(50) default NULL,
  `nameLast` varchar(50) NOT NULL default '',
  `nameNote` varchar(255) default NULL,
  `nameGiven` varchar(255) default NULL,
  `nameNick` varchar(255) default NULL,
  `weight` int(3) default NULL,
  `height` double(4,1) default NULL,
  `bats` enum('L','R','B') default NULL,
  `throws` enum('L','R','B') default NULL,
  `debut` date default NULL,
  `finalGame` date default NULL,
  `college` varchar(50) default NULL,
  `lahman40ID` varchar(9) default NULL,
  `lahman45ID` varchar(9) default NULL,
  `retroID` varchar(9) default NULL,
  `holtzID` varchar(9) default NULL,
  `bbrefID` varchar(9) default NULL,
  PRIMARY KEY  (`lahmanID`),
  KEY `playerID` (`playerID`),
  KEY `managerID` (`managerID`),
  KEY `hofID` (`hofID`),
  KEY `lahman40ID` (`lahman40ID`),
  KEY `lahman45ID` (`lahman45ID`),
  KEY `bbrefID` (`bbrefID`),
  KEY `bbrefID_2` (`bbrefID`),
  KEY `retroID` (`retroID`,`bbrefID`),
  KEY `holtzID` (`holtzID`),
  KEY `bbrefID_3` (`bbrefID`)
) ENGINE=MyISAM AUTO_INCREMENT=18968 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-03-28 17:12:25
