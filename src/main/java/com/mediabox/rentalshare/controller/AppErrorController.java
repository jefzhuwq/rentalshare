package com.mediabox.rentalshare.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AppErrorController implements ErrorController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/error")
    @ResponseBody
    public ModelAndView handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        ModelAndView mav = new ModelAndView(statusCode.toString());
        return mav;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}