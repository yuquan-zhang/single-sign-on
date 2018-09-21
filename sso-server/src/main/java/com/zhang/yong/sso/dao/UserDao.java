package com.zhang.yong.sso.dao;

import com.zhang.yong.sso.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User,Long> {

    User findByUsername(String username);

    User findByUsernameAndPassword(String username, String password);
}
