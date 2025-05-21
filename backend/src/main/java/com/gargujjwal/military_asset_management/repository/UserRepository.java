package com.gargujjwal.military_asset_management.repository;

import com.gargujjwal.military_asset_management.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  void deleteByUsername(String username);
}
