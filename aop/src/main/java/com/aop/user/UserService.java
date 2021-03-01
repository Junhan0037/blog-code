package com.aop.user;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    List<User> getUsers();

    void update(User user) throws Exception;

}
