package com.mediabox.rentalshare.controller;

import com.mediabox.rentalshare.model.*;
import com.mediabox.rentalshare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.Date;

@Controller
public class ContactUsController {
    @Autowired
    ContactUsRepository contactUsRepository;

    @RequestMapping(value = "/submit_contactus", method = RequestMethod.POST)
    public ModelAndView createNewProduct(@ModelAttribute ContactUs contactUs) {
        ModelAndView modelAndView = new ModelAndView("redirect:/index");
        contactUs.setCreateTimestamp(new Date());
        contactUs.setUpdateTimestamp(new Date());
        contactUs.setStatus(ContactUsStatus.CREATED.getValue());

        contactUsRepository.save(contactUs);
        return modelAndView;
    }
}
