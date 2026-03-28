package com.example.demo.repository;

import com.example.demo.repository.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findByUserName(String username);

  List<User> getAllById(Integer id);
}
