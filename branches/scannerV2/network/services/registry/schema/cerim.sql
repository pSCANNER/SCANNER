-- phpMyAdmin SQL Dump
-- version 3.5.8.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Aug 02, 2013 at 06:51 PM
-- Server version: 5.6.11-log
-- PHP Version: 5.4.14

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `cerim`
--

-- --------------------------------------------------------

--
-- Table structure for table `analysis tools`
--

CREATE TABLE IF NOT EXISTS `analysis tools` (
  `Tool ID` int(11) NOT NULL,
  `Tool Parent Library ID` int(11) NOT NULL,
  `Tool Description` text NOT NULL,
  `Input Format Specifications` text NOT NULL,
  `Output Format Specidications` text NOT NULL,
  `Curator UID` int(11) NOT NULL,
  `Information Email` text NOT NULL,
  PRIMARY KEY (`Tool ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `data set definition`
--

CREATE TABLE IF NOT EXISTS `data set definition` (
  `Data Set Definition ID` int(11) NOT NULL,
  `Data Description XML` text NOT NULL COMMENT 'Path to XML describing data',
  `Data Processing XML` text NOT NULL COMMENT 'Path to XML for data processing specifications',
  `Data Processing Program` text NOT NULL COMMENT 'Path to SQL or other data processing program',
  `Author UID` int(11) NOT NULL COMMENT 'UID of author',
  `Originating Study ID` int(11) NOT NULL COMMENT 'ID of study using this data set',
  `Data Set Regulation Type` int(11) NOT NULL COMMENT 'This is a key to the type of legal regulations this data set is subject to (safe harbor, limited data set, identified data)',
  PRIMARY KEY (`Data Set Definition ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `data set instance`
--

CREATE TABLE IF NOT EXISTS `data set instance` (
  `Data Set Instance ID` int(11) NOT NULL,
  `Data Set Definition ID` int(11) NOT NULL,
  `Data Set Instance Path` text NOT NULL,
  `Curator UID` int(11) NOT NULL,
  `Study ID` int(11) NOT NULL,
  `Source Data Warehouse ID` int(11) NOT NULL,
  `Data Slice ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`Data Set Instance ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `dua-study`
--

CREATE TABLE IF NOT EXISTS `dua-study` (
  `DUA ID` int(11) NOT NULL,
  `Study ID` int(11) NOT NULL,
  UNIQUE KEY `DUA ID` (`DUA ID`,`Study ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Table linking the DUA to the study';

-- --------------------------------------------------------

--
-- Table structure for table `investigator-study`
--

CREATE TABLE IF NOT EXISTS `investigator-study` (
  `Study ID` int(11) NOT NULL,
  `Investigator ID` int(11) NOT NULL,
  `Role` int(11) NOT NULL COMMENT 'PI, CO-I, PM, etc.',
  UNIQUE KEY `Study ID` (`Study ID`,`Investigator ID`,`Role`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `policy registry`
--

CREATE TABLE IF NOT EXISTS `policy registry` (
  `Policy ID` int(11) NOT NULL,
  `Authority Type` enum('LEGAL','STUDY','SITE ADMINISTRATION','NETWORK ADMINISTRATION') NOT NULL COMMENT 'There is a hierarchy of authorities, study authority most commonly',
  `Resource Type Governed` enum('DATA SET INSTANCE','METHOD') NOT NULL COMMENT 'Members of a study group have access to data set instances data set instances, data set instances must be approved for access by methods in a study protocol',
  `Assertion` text NOT NULL COMMENT 'e.g. "I as an authority or delegate for --this raw data source-- approve -this data set- to be accessed by -this method- for members of -this study/group-"',
  `Data Resource Instance Resource ID` int(11) NOT NULL,
  `Data Resource Definition ID` int(11) NOT NULL,
  `Data Resource Legal Type` enum('SAFE HARBOR','DUA-COVERED LIMITED DATA SET','IDENTIFIED DATA') NOT NULL,
  `Method Resource ID` int(11) NOT NULL,
  `Data Source ID` int(11) NOT NULL,
  `Study ID` int(11) NOT NULL,
  PRIMARY KEY (`Policy ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `source data warehouse`
--

CREATE TABLE IF NOT EXISTS `source data warehouse` (
  `Connectivity Manager ID` int(11) NOT NULL,
  `Data Manager ID` int(11) NOT NULL,
  `Policy` int(11) NOT NULL,
  `Schema Documentation` text NOT NULL,
  `ETL Documentation` text NOT NULL,
  `ETL Programs` text NOT NULL,
  PRIMARY KEY (`Connectivity Manager ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Documentation of the data source, points of contact and authorities';

-- --------------------------------------------------------

--
-- Table structure for table `study`
--

CREATE TABLE IF NOT EXISTS `study` (
  `Study ID` int(11) NOT NULL,
  `IRB ID` int(11) NOT NULL,
  `Protocol` text NOT NULL,
  `Principal Investigator UID` int(11) NOT NULL,
  `Start Date` int(11) NOT NULL,
  `End Date` int(11) NOT NULL,
  `Clinical Trials ID` int(11) NOT NULL,
  `Analysis Plan` text NOT NULL,
  `Grant IDs` text NOT NULL COMMENT 'actually need a table Grant ID-Study ID',
  `Data Set IDs` text NOT NULL COMMENT 'Need a table Data Set ID-Study ID',
  `DUA IDs` text NOT NULL COMMENT 'Need a table DUA-study ',
  PRIMARY KEY (`Study ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `UID` int(11) NOT NULL,
  `email` int(11) NOT NULL,
  `HSPC documents` text NOT NULL,
  `Primary Affliation` int(11) NOT NULL,
  `Secondary Affliliation` int(11) NOT NULL,
  `Phone` text NOT NULL,
  `Reports To` int(11) NOT NULL,
  `Active` int(11) NOT NULL,
  `Approved Roles` int(11) NOT NULL COMMENT 'not sure if this goes here, but would be investigator, PM, etc.',
  `First Name` text NOT NULL,
  `Middle Initial` text,
  `Last Name` text NOT NULL,
  `PubMed Author ID` text,
  PRIMARY KEY (`UID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
