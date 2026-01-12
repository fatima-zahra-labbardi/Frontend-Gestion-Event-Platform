-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : dim. 11 jan. 2026 à 23:26
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `db_auth_service`
--

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  `filiere` varchar(255) DEFAULT NULL,
  `nom_etablissement` varchar(255) DEFAULT NULL,
  `type_organisateur` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `full_name`, `email`, `password_hash`, `role`, `filiere`, `nom_etablissement`, `type_organisateur`, `created_at`) VALUES
(3, 'amal', 'amal@gamil.com', '$2a$10$m20uYE1RGZhTnHG3veZele/c4qLqohAhe6s0f9NEDIS7iuziWrWS2', 'ORGANIZER', NULL, 'NESAK', 'afaq', '2025-12-27 03:42:21'),
(4, 'Utilisateur Test', 'test@gmail.com', '$2a$10$z6y7iG5rMiSQOc0RZtjHHej8y2qdqfkYvnF0TbmsnvnIN48N0LbA6', 'STUDENT', 'Informatique', NULL, NULL, '2026-01-11 12:55:09'),
(5, 'Rim Karimi', 'rim@gmail.com', '$2a$10$R6k2mmSSB7LxhVbIpqhXaODfRu9gwUX.k2UOo2GjKSPOSnUKsL.ji', 'STUDENT', 'Informatique', 'ENSAK', NULL, '2026-01-11 18:06:54'),
(6, 'ilsu', 'ilsu@gmail.com', '$2a$10$1jI9K0RituPG8.3GGpAGm.Wr55JizeqZ0KQM.J0Tnmi5tDUD0aQlS', 'ORGANIZER', NULL, 'Afaq', 'Club', '2026-01-11 21:58:15');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
