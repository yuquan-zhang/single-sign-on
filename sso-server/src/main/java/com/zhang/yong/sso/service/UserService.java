package com.zhang.yong.sso.service;

import com.zhang.yong.sso.dao.UserDao;
import com.zhang.yong.sso.entity.User;
import com.zhang.yong.sso.util.EncryptUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class UserService {
    @Resource
    private UserDao userDao;

    public User getById(Long id) {
        return userDao.findOne(id);
    }

    public String login(String username, String password) {
        User user = userDao.findByUsername(username);
        if(user != null){
            String encryptedPsd = EncryptUtils.getMD5Hash(password+user.getSalt());
            if(encryptedPsd.equals(user.getPassword())) return "true";
        }
        return "用户名或密码错误";
    }
}
