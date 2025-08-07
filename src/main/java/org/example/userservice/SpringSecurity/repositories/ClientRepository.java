package org.example.userservice.SpringSecurity.repositories;

import java.util.Optional;


import org.example.userservice.SpringSecurity.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByClientId(String clientId);
}
