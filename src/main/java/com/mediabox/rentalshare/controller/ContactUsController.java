package com.mediabox.rentalshare.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mediabox.rentalshare.model.*;
import com.mediabox.rentalshare.repository.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static com.mediabox.rentalshare.utils.Constants.UPLOADED_FOLDER;


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
