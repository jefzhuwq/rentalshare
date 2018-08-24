package com.mediabox.rentalshare.service;

import com.mediabox.rentalshare.model.User;

public interface UserService {
    public User findUserByEmail(String email);
    public void saveUser(User user);
}
