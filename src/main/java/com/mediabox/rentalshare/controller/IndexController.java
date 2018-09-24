package com.mediabox.rentalshare.controller;

import com.mediabox.rentalshare.model.*;
import com.mediabox.rentalshare.repository.PriceRepository;
import com.mediabox.rentalshare.repository.ProductImageRepository;
import com.mediabox.rentalshare.repository.ProductRepository;
import com.mediabox.rentalshare.repository.RentalRequestRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.print.DocFlavor;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;


@Controller
public class IndexController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    RentalRequestRepository rentalRequestRepository;

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    PriceRepository priceRepository;

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
    public ModelAndView searchResult(@RequestParam("keyword") String keyword) {
        ModelAndView mav = new ModelAndView("search_result");
        List<Product> productList = this.search(keyword);
        mav.addObject("productList", productList);
        Map<Integer, List<ProductImage>> productImageMap = new HashMap<>();
        Map<Integer, List<Price>> priceMap = new HashMap<>();
        for (Product product : productList) {
            productImageMap.put(product.getId(), productImageRepository.findByProduct(product));
            priceMap.put(product.getId(), priceRepository.findByProduct(product));
        }
        mav.addObject("productImageMap", productImageMap);
        mav.addObject("priceMap", priceMap);
        return mav;
    }

    private List<Product> search(String keyword) {
        List<Product> productList = productRepository.searchByKeyword(keyword);
        return productList;
    }

    @RequestMapping(value = "/view_product/{id}", method = RequestMethod.GET)
    public ModelAndView editProduct(@PathVariable("id") int id) {
        ModelAndView mav = new ModelAndView("/product/view");
        Product product = productRepository.findById(id).get();
        mav.addObject("product", product);
        mav.addObject("productImageList", productImageRepository.findByProduct(product));
        List<RentalRequest> rentalRequestList = rentalRequestRepository.findByProduct(product);
        mav.addObject("rentalRequestList", rentalRequestList);
        mav.addObject("priceList", priceRepository.findByProduct(product));

        List<LocalDate> disabledStartDateList = new ArrayList<>();
        List<LocalDate> disabledEndDateList = new ArrayList<>();

        rentalRequestList.forEach(obj -> {
            LocalDate localStartDate = obj.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate localEndDate = obj.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//            disabledStartDateList.add(localStartDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
//            disabledEndDateList.add(localEndDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            disabledStartDateList.add(localStartDate);
            disabledEndDateList.add(localEndDate);
        });

        mav.addObject("disabledStartDateList", disabledStartDateList);
        mav.addObject("disabledEndDateList", disabledEndDateList);
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

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public ModelAndView contact() {
        ModelAndView mav = new ModelAndView("contact");
        mav.addObject("contactUs", new ContactUs());
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

    @RequestMapping(value = "/calendar", method = RequestMethod.GET)
    public ModelAndView calendar() {
        ModelAndView mav = new ModelAndView("/calendar");
        return mav;
    }
}
