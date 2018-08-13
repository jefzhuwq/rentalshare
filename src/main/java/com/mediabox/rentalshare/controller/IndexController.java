package com.mediabox.rentalshare.controller;

import com.mediabox.rentalshare.model.Category;
import com.mediabox.rentalshare.model.Product;
import com.mediabox.rentalshare.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
public class IndexController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");
        return mav;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView mav = new ModelAndView("login");
        return mav;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView signup() {
        ModelAndView mav = new ModelAndView("signup");
        User user = new User();
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(value = "/product/create", method = RequestMethod.GET)
    public ModelAndView createProduct() {
        ModelAndView mav = new ModelAndView("product/create");
        return mav;
    }

    @RequestMapping(value = "/product/edit", method = RequestMethod.GET)
    public ModelAndView editProduct() {
        ModelAndView mav = new ModelAndView("product/edit");
        return mav;
    }

    @RequestMapping(value = "/product/view", method = RequestMethod.GET)
    public ModelAndView viewProduct() {
        ModelAndView mav = new ModelAndView("product/view");
        return mav;
    }

    @RequestMapping(value = "/search_result", method = RequestMethod.GET)
    public ModelAndView searchResult() {
        ModelAndView mav = new ModelAndView("search_result");
        return mav;
    }

    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public ModelAndView cart() {
        ModelAndView mav = new ModelAndView("cart");
        return mav;
    }

    @RequestMapping(value = "/browse_product", method = RequestMethod.GET)
    public ModelAndView browseProductList() {
        ModelAndView mav = new ModelAndView("browse_product");
        return mav;
    }

    @RequestMapping(value = "/post_product", method = RequestMethod.GET)
    public ModelAndView postProduct() {
        ModelAndView mav = new ModelAndView("/product/create");
        Product product = new Product();
        mav.addObject("product", product);
        return mav;
    }

    @RequestMapping(value = "/access_denied", method = RequestMethod.GET)
    public ModelAndView accessDenied() {
        ModelAndView mav = new ModelAndView("/access_denied");
        return mav;
    }
}
