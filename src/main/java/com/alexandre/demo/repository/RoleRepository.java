package com.alexandre.demo.repository;

import com.alexandre.demo.entities.Role;
import com.alexandre.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
