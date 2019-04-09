CREATE DATABASE  IF NOT EXISTS `social_network_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */;
USE `social_network_db`;
-- MySQL dump 10.13  Distrib 8.0.15, for Win64 (x86_64)
--
-- Host: localhost    Database: social_network_db
-- ------------------------------------------------------
-- Server version	8.0.15

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+01:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `notifica`
--

DROP TABLE IF EXISTS `notifica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `notifica` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `titolo` varchar(100) NOT NULL,
  `contenuto` text NOT NULL,
  `data` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `tipo_notifica` blob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7790 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `partita_calcio`
--

DROP TABLE IF EXISTS `partita_calcio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
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
  CONSTRAINT `id_creatore` FOREIGN KEY (`id_creatore`) REFERENCES `utente` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `relazione_utente_categoria`
--

DROP TABLE IF EXISTS `relazione_utente_categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `relazione_utente_categoria` (
  `id_u` int(11) NOT NULL,
  `nome_categoria` varchar(50) NOT NULL,
  PRIMARY KEY (`id_u`,`nome_categoria`),
  CONSTRAINT `id_u` FOREIGN KEY (`id_u`) REFERENCES `utente` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `relazione_utente_notifica`
--

DROP TABLE IF EXISTS `relazione_utente_notifica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `relazione_utente_notifica` (
  `id_user` int(11) NOT NULL,
  `id_notifica` int(11) NOT NULL,
  PRIMARY KEY (`id_user`,`id_notifica`),
  KEY `id_utente_idx` (`id_user`),
  KEY `id_notifica_idx` (`id_notifica`),
  CONSTRAINT `id_notifica` FOREIGN KEY (`id_notifica`) REFERENCES `notifica` (`id`) ON DELETE CASCADE,
  CONSTRAINT `id_user` FOREIGN KEY (`id_user`) REFERENCES `utente` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `relazione_utente_partita_calcio`
--

DROP TABLE IF EXISTS `relazione_utente_partita_calcio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `relazione_utente_partita_calcio` (
  `id_utente` int(11) NOT NULL,
  `id_evento` int(11) NOT NULL,
  PRIMARY KEY (`id_utente`,`id_evento`),
  KEY `id_utente_idx` (`id_utente`),
  KEY `id_partita_idx` (`id_evento`),
  CONSTRAINT `id_partita` FOREIGN KEY (`id_evento`) REFERENCES `partita_calcio` (`id`) ON DELETE CASCADE,
  CONSTRAINT `id_utente` FOREIGN KEY (`id_utente`) REFERENCES `utente` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `relazione_utente_scii`
--

DROP TABLE IF EXISTS `relazione_utente_scii`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `relazione_utente_scii` (
  `id_utente` int(11) NOT NULL,
  `id_evento` int(11) NOT NULL,
  `biglietto_bus` tinyint(1) NOT NULL,
  `pranzo` tinyint(1) NOT NULL,
  `affitto_scii` tinyint(1) NOT NULL,
  PRIMARY KEY (`id_utente`,`id_evento`),
  KEY `fk_relazione_utente_scii_2_idx` (`id_evento`),
  CONSTRAINT `fk_relazione_utente_scii_1` FOREIGN KEY (`id_utente`) REFERENCES `utente` (`id`),
  CONSTRAINT `fk_relazione_utente_scii_2` FOREIGN KEY (`id_evento`) REFERENCES `scii` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scii`
--

DROP TABLE IF EXISTS `scii`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `scii` (
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
  `biglietto_bus` int(11) DEFAULT NULL,
  `pranzo` int(11) DEFAULT NULL,
  `affitto_scii` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_creatore_idx` (`id_creatore`),
  CONSTRAINT `id` FOREIGN KEY (`id_creatore`) REFERENCES `utente` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `utente`
--

DROP TABLE IF EXISTS `utente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `utente` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `password` varchar(50) DEFAULT NULL,
  `eta_min` int(11) DEFAULT NULL,
  `eta_max` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-04-06 17:08:13
