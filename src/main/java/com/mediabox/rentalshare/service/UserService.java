package com.mediabox.rentalshare.service;

import com.mediabox.rentalshare.model.User;

public interface UserService {
    User findUserByEmail(String email);
    void saveUser(User user);
}
