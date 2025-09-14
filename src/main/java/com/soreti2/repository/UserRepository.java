package com.soreti2.repository;

import com.soreti2.model.Role;
import com.soreti2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByRole(Role role);
    boolean existsByPhone(String phone);
}

