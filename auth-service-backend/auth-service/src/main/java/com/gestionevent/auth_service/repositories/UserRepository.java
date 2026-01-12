package com.gestionevent.auth_service.repositories;

import com.gestionevent.auth_service.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Pour vérifier si l'email existe (utilisé dans Register)
    boolean existsByEmail(String email);

    // Pour récupérer l'utilisateur complet (utilisé dans Login ou Profil)
    // On utilise Optional pour éviter les erreurs si l'utilisateur n'est pas trouvé
    Optional<User> findByEmail(String email);
}