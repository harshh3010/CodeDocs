-- phpMyAdmin SQL Dump
-- version 5.0.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 27, 2021 at 04:23 PM
-- Server version: 10.4.14-MariaDB
-- PHP Version: 7.2.34

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `codedocs`
--

-- --------------------------------------------------------

--
-- Table structure for table `active_editors`
--

CREATE TABLE `active_editors` (
  `codedoc_id` varchar(100) NOT NULL,
  `user_in_control` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `active_editors`
--

INSERT INTO `active_editors` (`codedoc_id`, `user_in_control`) VALUES
('999f2fa6-dbfa-4833-bce0-f7fb1b39bfe8', '4d4de057-b67d-44f8-99d8-b7bc5bdeb6e0');

-- --------------------------------------------------------

--
-- Table structure for table `codedoc`
--

CREATE TABLE `codedoc` (
  `codedoc_id` varchar(100) NOT NULL,
  `title` varchar(20) NOT NULL,
  `description` varchar(200) NOT NULL,
  `language` enum('JAVA','CPP','PYTHON','C') NOT NULL,
  `owner_id` varchar(100) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `codedoc`
--

INSERT INTO `codedoc` (`codedoc_id`, `title`, `description`, `language`, `owner_id`, `created_at`, `updated_at`) VALUES
('999f2fa6-dbfa-4833-bce0-f7fb1b39bfe8', 'Fibonacci Series', 'Code to generate fibonacci series. Help me with the recursive code :(', 'JAVA', 'aacc45f5-2057-481e-a83e-322abea26af7', '2021-12-26 14:12:16', '2021-12-26 14:12:16');

-- --------------------------------------------------------

--
-- Table structure for table `codedoc_accessor`
--

CREATE TABLE `codedoc_accessor` (
  `user_id` varchar(100) NOT NULL,
  `codedoc_id` varchar(100) NOT NULL,
  `access_right` enum('OWNER','COLLABORATOR','PENDING','') NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 0,
  `has_write_permissions` tinyint(1) NOT NULL DEFAULT 0,
  `ip_address` varchar(50) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `audio_port` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `codedoc_accessor`
--

INSERT INTO `codedoc_accessor` (`user_id`, `codedoc_id`, `access_right`, `is_active`, `has_write_permissions`, `ip_address`, `port`, `audio_port`) VALUES
('4d4de057-b67d-44f8-99d8-b7bc5bdeb6e0', '999f2fa6-dbfa-4833-bce0-f7fb1b39bfe8', 'COLLABORATOR', 1, 1, '127.0.0.1', 52939, 52940),
('aacc45f5-2057-481e-a83e-322abea26af7', '999f2fa6-dbfa-4833-bce0-f7fb1b39bfe8', 'OWNER', 1, 1, '127.0.0.1', 52931, 52932);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `user_id` varchar(100) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `first_name` varchar(20) NOT NULL,
  `last_name` varchar(20) NOT NULL,
  `is_verified` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`user_id`, `email`, `password`, `first_name`, `last_name`, `is_verified`) VALUES
('4d4de057-b67d-44f8-99d8-b7bc5bdeb6e0', 'simmz@gmail.com', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c3312448eb225', 'simmz', 'simmz', 1),
('aacc45f5-2057-481e-a83e-322abea26af7', 'simran@gmail.com', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c3312448eb225', 'Simran', 'Kaur', 1);

-- --------------------------------------------------------

--
-- Table structure for table `user_verification`
--

CREATE TABLE `user_verification` (
  `user_email` varchar(50) NOT NULL,
  `verification_token` varchar(100) NOT NULL,
  `expires_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user_verification`
--

INSERT INTO `user_verification` (`user_email`, `verification_token`, `expires_at`) VALUES
('simran@gmail.com', 'mqjbvY', '2021-12-26 09:37:27'),
('simmz@gmail.com', 'UA70lj', '2021-12-26 09:39:22');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `active_editors`
--
ALTER TABLE `active_editors`
  ADD KEY `user_in_control` (`user_in_control`),
  ADD KEY `codedoc_id3` (`codedoc_id`);

--
-- Indexes for table `codedoc`
--
ALTER TABLE `codedoc`
  ADD PRIMARY KEY (`codedoc_id`),
  ADD KEY `codedoc_owner` (`owner_id`);

--
-- Indexes for table `codedoc_accessor`
--
ALTER TABLE `codedoc_accessor`
  ADD PRIMARY KEY (`user_id`,`codedoc_id`),
  ADD KEY `codedoc_id` (`codedoc_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `user_verification`
--
ALTER TABLE `user_verification`
  ADD KEY `user_email` (`user_email`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `active_editors`
--
ALTER TABLE `active_editors`
  ADD CONSTRAINT `codedoc_id3` FOREIGN KEY (`codedoc_id`) REFERENCES `codedoc` (`codedoc_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `user_in_control` FOREIGN KEY (`user_in_control`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `codedoc`
--
ALTER TABLE `codedoc`
  ADD CONSTRAINT `codedoc_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `codedoc_accessor`
--
ALTER TABLE `codedoc_accessor`
  ADD CONSTRAINT `codedoc_id` FOREIGN KEY (`codedoc_id`) REFERENCES `codedoc` (`codedoc_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `user_id2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `user_verification`
--
ALTER TABLE `user_verification`
  ADD CONSTRAINT `user_email` FOREIGN KEY (`user_email`) REFERENCES `user` (`email`) ON DELETE CASCADE ON UPDATE CASCADE;

DELIMITER $$
--
-- Events
--
CREATE DEFINER=`root`@`localhost` EVENT `delete user verification table entry` ON SCHEDULE EVERY 1 MINUTE STARTS '2021-12-12 23:53:47' ENDS '2021-12-31 23:53:47' ON COMPLETION NOT PRESERVE ENABLE DO DELETE FROM user_verification
WHERE expires_at < NOW()$$

DELIMITER ;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
