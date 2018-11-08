package com.mediabox.rentalshare.controller;

import com.mediabox.rentalshare.model.User;
import com.mediabox.rentalshare.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@RestController
public class SignupController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView mav = new ModelAndView();
//        StringBuilder error = new StringBuilder();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        if (bindingResult.hasErrors()) {
//            bindingResult.getAllErrors().forEach(obj-> error.append(obj.toString()));
//            mav.addObject("errorString", error.toString());
            mav.setViewName("signup");
        } else {
            userService.saveUser(user);
            mav.addObject("successMessage", "User has been registered successfully");
            mav.addObject("user", new User());
            mav.setViewName("login");
        }
        return mav;
    }
}
