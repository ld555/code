package me.chuang6.jz.service;

import me.chuang6.jz.bean.User;

import java.util.List;

public interface UserService {
    List<User> getList();

    void add(User user) throws Exception;

    String loginUser(String openid);

    int vaild(String uuid, String timestamp, String digest);
}
