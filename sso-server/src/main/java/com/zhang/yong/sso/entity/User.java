package com.zhang.yong.sso.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Setter
@Getter
@Table(name = "sso_user")
@Entity
public class User extends DataModel{
    private String nickname;
    private String username;
    private String password;
    private String salt;
    private String phone;
    private String email;
    private Long type;
    private boolean delFlag;
}
