-- Adminer 5.4.2 MySQL 8.4.6-6 dump

SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

USE `dbcbwkixgjgvv2`;

SET NAMES utf8mb4;

DROP TABLE IF EXISTS `almacenes`;
CREATE TABLE `almacenes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `checklist_items`;
CREATE TABLE `checklist_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_maquina` int DEFAULT NULL,
  `descripcion` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_maquina` (`id_maquina`),
  CONSTRAINT `checklist_items_ibfk_1` FOREIGN KEY (`id_maquina`) REFERENCES `maquinas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `detalles`;
CREATE TABLE `detalles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_revision` int DEFAULT NULL,
  `id_item` int DEFAULT NULL,
  `resultado` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_revision` (`id_revision`),
  KEY `id_item` (`id_item`),
  CONSTRAINT `detalles_ibfk_1` FOREIGN KEY (`id_revision`) REFERENCES `revisiones` (`id`),
  CONSTRAINT `detalles_ibfk_2` FOREIGN KEY (`id_item`) REFERENCES `checklist_items` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `maquinas`;
CREATE TABLE `maquinas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `id_almacen` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_almacen` (`id_almacen`),
  CONSTRAINT `maquinas_ibfk_1` FOREIGN KEY (`id_almacen`) REFERENCES `almacenes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `revisiones`;
CREATE TABLE `revisiones` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int DEFAULT NULL,
  `comentario` text,
  `id_maquina` int DEFAULT NULL,
  `fecha_hora` datetime DEFAULT CURRENT_TIMESTAMP,
  `firma` text,
  `tiene_fallos` tinyint(1) DEFAULT '0',
  `preguntas_fallidas` json DEFAULT (json_array()),
  PRIMARY KEY (`id`),
  KEY `id_usuario` (`id_usuario`),
  KEY `id_maquina` (`id_maquina`),
  CONSTRAINT `revisiones_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `revisiones_ibfk_2` FOREIGN KEY (`id_maquina`) REFERENCES `maquinas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `rol` varchar(50) DEFAULT NULL,
  `id_almacen` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_almacen` (`id_almacen`),
  CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`id_almacen`) REFERENCES `almacenes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 2026-04-16 08:55:53 UTC
