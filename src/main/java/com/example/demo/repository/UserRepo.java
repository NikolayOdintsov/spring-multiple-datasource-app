package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by nikolay.odintsov on 14.05.18.
 */

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    User findByName(String name);
}
