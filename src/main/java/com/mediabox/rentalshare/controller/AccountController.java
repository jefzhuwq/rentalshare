package com.mediabox.rentalshare.controller;

import com.mediabox.rentalshare.model.Category;
import com.mediabox.rentalshare.model.Price;
import com.mediabox.rentalshare.model.Product;
import com.mediabox.rentalshare.repository.CategoryRepository;
import com.mediabox.rentalshare.repository.PriceRepository;
import com.mediabox.rentalshare.repository.ProductRepository;
import com.mediabox.rentalshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.DateTimeException;
import java.util.Date;
import java.util.List;


@Controller
public class AccountController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PriceRepository priceRepository;

    @RequestMapping(value = "/my_account", method = RequestMethod.GET)
    public ModelAndView myAccount() {
        ModelAndView mav = new ModelAndView("/account/my_account");
        return mav;
    }

    @RequestMapping(value = "/post_product", method = RequestMethod.POST)
    public ModelAndView createNewProduct(@ModelAttribute Product product) {
        ModelAndView modelAndView = new ModelAndView("/index");
        product.setCreateTimestamp(new Date());
        product.setUpdateTimestamp(new Date());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        product.setUser(userRepository.findByEmail(name));

        productRepository.save(product);
        return modelAndView;
    }

    @RequestMapping(value = "/product_list", method = RequestMethod.GET)
    public ModelAndView productList() {
        ModelAndView mav = new ModelAndView("/account/product_list");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        List<Product> productList = productRepository.findByUser(userRepository.findByEmail(name));
        mav.addObject("productList", productList);
        return mav;
    }

    @RequestMapping(value = "/edit_product/{id}", method = RequestMethod.GET)
    public ModelAndView editProduct(@PathVariable("id") int id) {
        ModelAndView mav = new ModelAndView("/product/edit");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        Product product = productRepository.findById(id).get();

        if (product.getUser().getEmail().equals(name)) {
            mav.addObject("product", product);
            mav.addObject("price", new Price());

            List<Price> priceList = priceRepository.findByProduct(product);
            mav.addObject("priceList", priceList);
        } else{
            mav.setViewName("/error");
        }

        return mav;
    }

    @RequestMapping(value = "/edit_product", method = RequestMethod.POST)
    public ModelAndView updateProduct(@ModelAttribute Product product) {
        ModelAndView mav = new ModelAndView("redirect:/product_list");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        Product productEdit = productRepository.findById(product.getId()).get();

        if (productEdit.getUser().getEmail().equals(name)) {
            productEdit.setCategory(product.getCategory());
            productEdit.setProductDescription(product.getProductDescription());
            productEdit.setProductName(product.getProductName());
            productEdit.setUpdateTimestamp(new Date());
            productRepository.save(productEdit);
        } else{
            mav.setViewName("/error");
        }

        return mav;
    }

    @RequestMapping(value = "/delete_product/{id}", method = RequestMethod.GET)
    public ModelAndView deleteProduct(@PathVariable("id") int id) {
        ModelAndView mav = new ModelAndView("redirect:/product_list");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        Product product = productRepository.findById(id).get();

        if (product.getUser().getEmail().equals(name)) {
            product.setIsActive(0);
            product.setUpdateTimestamp(new Date());
            productRepository.save(product);
        } else{
            mav.setViewName("/error");
        }

        return mav;
    }

    @RequestMapping(value = "/delete_price/{id}", method = RequestMethod.GET)
    public ModelAndView deletePrice(@PathVariable("id") int id) {
        Price price = priceRepository.findById(id).get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        ModelAndView mav = new ModelAndView();
        if (price.getProduct().getUser().getEmail().equals(name)) {
            mav.setViewName("redirect:/edit_product/" + price.getProduct().getId());
            priceRepository.delete(price);
        } else{
            mav.setViewName("/error");
        }

        return mav;
    }

    @RequestMapping(value = "/add_price", method = RequestMethod.POST)
    public ModelAndView addPrice(@ModelAttribute Price price, HttpServletRequest request) {
        String productId = request.getParameter("productId");
        ModelAndView mav = new ModelAndView("redirect:/edit_product/" + productId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        Product productEdit = productRepository.findById(Integer.parseInt(productId)).get();

        if (productEdit.getUser().getEmail().equals(name)) {
            price.setProduct(productEdit);
            price.setCreateTimestamp(new Date());
            price.setUpdateTimestamp(new Date());
            priceRepository.save(price);
        } else{
            mav.setViewName("/error");
        }

        return mav;
    }
}
