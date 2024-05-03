package com.chandankrr.authservice.repository;

import com.chandankrr.authservice.entity.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserInfo, String> {

    UserInfo findByUsername(String username);
}
