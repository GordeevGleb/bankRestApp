package org.example.bank_app.repository;

import org.example.bank_app.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(value = "User.withRoles", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findByLogin(String username);
    boolean existsByLogin(String login);

    boolean existsByLoginAndIsBlocked(String login, boolean isBlocked);
}
