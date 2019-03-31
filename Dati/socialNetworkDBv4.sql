-- MySQL dump 10.13  Distrib 5.7.25, for Linux (x86_64)
--
-- Host: localhost    Database: social_network_db
-- ------------------------------------------------------
-- Server version	5.7.25-0ubuntu0.18.04.2

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
-- Table structure for table `notifica`
--



--
-- Dumping data for table `notifica`
--



--
-- Table structure for table `partita_calcio`
--

DROP TABLE IF EXISTS `partita_calcio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partita_calcio` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_creatore` int(11) NOT NULL,
  `luogo` varchar(100) NOT NULL,
  `data_ora_termine_ultimo_iscrizione` timestamp NULL DEFAULT NULL,
  `data_ora_inizio_evento` timestamp NULL DEFAULT NULL,
  `partecipanti` int(11) NOT NULL,
  `costo` int(11) NOT NULL,
  `titolo` varchar(75) DEFAULT NULL,
  `note` varchar(1000) DEFAULT NULL,
  `benefici_quota` varchar(500) DEFAULT NULL,
  `data_ora_termine_evento` timestamp NULL DEFAULT NULL,
  `data_ora_termine_ritiro_iscrizione` timestamp NULL DEFAULT NULL,
  `tolleranza_max` int(11) DEFAULT NULL,
  `stato` varchar(45) NOT NULL,
  `eta_minima` int(11) NOT NULL,
  `eta_massima` int(11) NOT NULL,
  `genere` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_utente_idx` (`id_creatore`),
  CONSTRAINT `id_creatore` FOREIGN KEY (`id_creatore`) REFERENCES `utente` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `partita_calcio`
--



--
-- Table structure for table `relazione_utente_categoria`
--

DROP TABLE IF EXISTS `relazione_utente_categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `relazione_utente_categoria` (
  `id_u` int(11) NOT NULL,
  `nome_categoria` varchar(50) NOT NULL,
  PRIMARY KEY (`id_u`,`nome_categoria`),
  CONSTRAINT `id_u` FOREIGN KEY (`id_u`) REFERENCES `utente` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relazione_utente_categoria`
--



--
-- Table structure for table `relazione_utente_notifica`
--

DROP TABLE IF EXISTS `relazione_utente_notifica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `relazione_utente_notifica` (
  `id_user` int(11) NOT NULL,
  `id_notifica` int(11) NOT NULL,
  PRIMARY KEY (`id_user`,`id_notifica`),
  KEY `id_utente_idx` (`id_user`),
  KEY `id_notifica_idx` (`id_notifica`),
  CONSTRAINT `id_notifica` FOREIGN KEY (`id_notifica`) REFERENCES `notifica` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `id_user` FOREIGN KEY (`id_user`) REFERENCES `utente` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relazione_utente_notifica`
--



--
-- Table structure for table `relazione_utente_partita`
--

DROP TABLE IF EXISTS `relazione_utente_partita`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `relazione_utente_partita` (
  `id_utente` int(11) NOT NULL,
  `id_partita` int(11) NOT NULL,
  PRIMARY KEY (`id_utente`,`id_partita`),
  KEY `id_utente_idx` (`id_utente`),
  KEY `id_partita_idx` (`id_partita`),
  CONSTRAINT `id_partita` FOREIGN KEY (`id_partita`) REFERENCES `partita_calcio` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `id_utente` FOREIGN KEY (`id_utente`) REFERENCES `utente` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relazione_utente_partita`
--



--
-- Table structure for table `utente`
--

DROP TABLE IF EXISTS `utente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `utente` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `password` varchar(50) DEFAULT NULL,
  `eta_min` int(11) DEFAULT NULL,
  `eta_max` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utente`
--


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-03-31 17:44:54
