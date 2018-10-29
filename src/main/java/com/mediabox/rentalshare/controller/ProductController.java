package com.mediabox.rentalshare.controller;

import com.mediabox.rentalshare.model.*;
import com.mediabox.rentalshare.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
public class ProductController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PriceRepository priceRepository;

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    RentalRequestRepository rentalRequestRepository;

    @Autowired
    FavoriteRepository favoriteRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @RequestMapping(value = "/new_review", method = RequestMethod.GET)
    public ModelAndView newReview(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("review/create");
        String productId = request.getParameter("productId");

        Product product = productRepository.findById(Integer.parseInt(productId)).get();
        mav.addObject("product", product);
        mav.addObject("review", new Review());
        return mav;
    }

    @RequestMapping(value = "/post_review", method = RequestMethod.POST)
    public ModelAndView postReview(@ModelAttribute Review review, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        String productId = request.getParameter("productId");
        mav.setViewName("redirect:/view_product/" + productId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username
        User user = userRepository.findByEmail(name);

        Product product = productRepository.findById(Integer.parseInt(productId)).isPresent() ? productRepository.findById(Integer.parseInt(productId)).get() : null;
        review.setProduct(product);
        review.setStatus(1);
        review.setUser(user);
        review.setCreateTimestamp(new Date());
        review.setUpdateTimestamp(new Date());
        reviewRepository.save(review);

        return mav;
    }
}
