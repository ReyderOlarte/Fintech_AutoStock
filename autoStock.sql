-- MySQL dump 10.13  Distrib 5.5.35, for Linux (x86_64)
--
-- Host: localhost    Database: autoStock
-- ------------------------------------------------------
-- Server version	5.5.35-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `backtestResults`
--

DROP TABLE IF EXISTS `backtestResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `backtestResults` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dateStart` date NOT NULL,
  `dateEnd` date NOT NULL,
  `dateRun` datetime NOT NULL,
  `exchange` varchar(16) NOT NULL,
  `symbol` varchar(16) NOT NULL,
  `balanceInBand` double NOT NULL,
  `balanceOutBand` double NOT NULL,
  `percentGainInBand` double NOT NULL,
  `percentGainOutBand` double NOT NULL,
  `tradeEntry` int(11) NOT NULL,
  `tradeReentry` int(11) NOT NULL,
  `tradeExit` int(11) NOT NULL,
  `tradeWins` int(11) NOT NULL,
  `tradeLoss` int(11) NOT NULL,
  `gsonId` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=521 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `backtestValues`
--

DROP TABLE IF EXISTS `backtestValues`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `backtestValues` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `backtestId` int(11) NOT NULL,
  `field` varchar(64) NOT NULL,
  `value` varchar(32) NOT NULL,
  `adjusted` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `exchanges`
--

DROP TABLE IF EXISTS `exchanges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exchanges` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `exchange` varchar(8) NOT NULL,
  `currency` varchar(4) NOT NULL,
  `timeOpen` time NOT NULL,
  `timeClose` time NOT NULL,
  `timeOffset` varchar(9) NOT NULL,
  `timeZone` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gson`
--

DROP TABLE IF EXISTS `gson`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gson` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gsonString` longblob NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=524 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `marketOrders`
--

DROP TABLE IF EXISTS `marketOrders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marketOrders` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(16) NOT NULL,
  `orderType` varchar(16) NOT NULL,
  `quantity` int(11) NOT NULL,
  `priceLimit` float DEFAULT NULL,
  `priceStop` float DEFAULT NULL,
  `goodAfterDate` datetime DEFAULT NULL,
  `goodUntilDate` datetime DEFAULT NULL,
  `priceAverageFill` float NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `replay`
--

DROP TABLE IF EXISTS `replay`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `replay` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `exchange` varchar(8) NOT NULL,
  `symbol` varchar(16) NOT NULL,
  `dateTimeActivated` date NOT NULL,
  `dateTimeDeactivated` date NOT NULL,
  `profitLoss` decimal(10,3) NOT NULL DEFAULT '0.000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stockHistoricalPrices`
--

DROP TABLE IF EXISTS `stockHistoricalPrices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stockHistoricalPrices` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(16) NOT NULL,
  `exchange` varchar(8) NOT NULL,
  `resolution` int(8) NOT NULL,
  `priceOpen` float DEFAULT NULL,
  `priceHigh` float DEFAULT NULL,
  `priceLow` float DEFAULT NULL,
  `priceClose` float DEFAULT NULL,
  `sizeVolume` int(11) NOT NULL,
  `dateTime` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_symbol` (`symbol`(6)),
  KEY `index_dateTime` (`dateTime`),
  KEY `index_exchange` (`exchange`(6))
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `symbols`
--

DROP TABLE IF EXISTS `symbols`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `symbols` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(16) NOT NULL,
  `exchange` varchar(8) NOT NULL,
  `description` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_symbol` (`symbol`(8)),
  KEY `index_exchange` (`exchange`(2))
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `whitelist`
--

DROP TABLE IF EXISTS `whitelist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `whitelist` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(16) NOT NULL,
  `exchange` varchar(16) NOT NULL,
  `dateLastAdjustment` datetime DEFAULT NULL,
  `adjustmentId` int(11) DEFAULT NULL,
  `reason` varchar(16) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-01-01 22:04:52
