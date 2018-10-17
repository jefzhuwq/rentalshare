package com.mediabox.rentalshare.controller;

import com.mediabox.rentalshare.category.BaseCategory;
import com.mediabox.rentalshare.model.*;
import com.mediabox.rentalshare.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


@Controller
public class IndexController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProductRepository productRepository;

    @Autowired
    RentalRequestRepository rentalRequestRepository;

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    PriceRepository priceRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");

        // load TextBook list
        Category textBookCategory = categoryRepository.findById(BaseCategory.TEXT_BOOK.getId()).isPresent() ? categoryRepository.findById(BaseCategory.TEXT_BOOK.getId()).get() : null;
        if (textBookCategory != null) {
            List<Product> topTextBookList = productRepository.getProductListByCategory(textBookCategory);
            mav.addObject("topTextBookList", topTextBookList);
            mav.addObject("topTextBookImageMap", this.getPrimaryProductImageMap(topTextBookList));
            mav.addObject("topTextBookPriceMap", this.getPriceMap(topTextBookList));
        }
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
        mav.addObject("user", new User());
        return mav;
    }

    @RequestMapping(value = "/product/create", method = RequestMethod.GET)
    public ModelAndView createProduct() {
        ModelAndView mav = new ModelAndView("product/create");
        return mav;
    }

    @RequestMapping(value = "/search_result", method = RequestMethod.GET)
    public ModelAndView searchResult(@RequestParam("keyword") String keyword, @RequestParam("category_select") String categorySelect) {
        ModelAndView mav = new ModelAndView("search_result");
        Category category = null;
        if (!StringUtils.isEmpty(categorySelect)) {
            category = this.categoryRepository.findById(Integer.parseInt(categorySelect)).orElse(null);
        }

        List<Product> productList = this.search(keyword, category);
        mav.addObject("productList", productList);
        mav.addObject("productImageMap", this.getPrimaryProductImageMap(productList));
        mav.addObject("priceMap", this.getPriceMap(productList));
        return mav;
    }

    private Map<Integer, List<ProductImage>> getProductImageMap(List<Product> productList) {
        Map<Integer, List<ProductImage>> productImageMap = new HashMap<>();
        for (Product product : productList) {
            productImageMap.put(product.getId(), productImageRepository.findByProduct(product));
        }
        return productImageMap;
    }

    private Map<Integer, ProductImage> getPrimaryProductImageMap(List<Product> productList) {
        Map<Integer, ProductImage> productImageMap = new HashMap<>();
        for (Product product : productList) {
            productImageMap.put(product.getId(), productImageRepository.findByProduct(product).stream().filter(obj -> obj.getIsPrimary()).findAny().orElse(null));
        }
        return productImageMap;
    }

    private Map<Integer, List<Price>> getPriceMap(List<Product> productList) {
        Map<Integer, List<Price>> priceMap = new HashMap<>();
        for (Product product : productList) {
            priceMap.put(product.getId(), priceRepository.findByProduct(product));
        }
        return priceMap;
    }

    private List<Product> search(String keyword, Category category) {
        List<Product> productList;
        if (category != null) {
            productList = productRepository.searchByKeyword(keyword, category);
        } else {
            productList = productRepository.searchByKeyword(keyword);
        }
        return productList;
    }

    @RequestMapping(value = "/view_product/{id}", method = RequestMethod.GET)
    public ModelAndView editProduct(@PathVariable("id") int id) {
        ModelAndView mav = new ModelAndView("/product/view");
        Product product = productRepository.findById(id).isPresent() ? productRepository.findById(id).get() : null;
        mav.addObject("product", product);
        mav.addObject("productImageList", productImageRepository.findByProduct(product));
        List<RentalRequest> rentalRequestList = rentalRequestRepository.findByProduct(product);
        mav.addObject("rentalRequestList", rentalRequestList);
        mav.addObject("priceList", priceRepository.findByProduct(product));

        List<Review> reviewList = reviewRepository.findByProduct(product);
        mav.addObject("reviewList", reviewList);

        // Add avg rate and breakdown
        mav.addObject("rateMap", this.getAvgRateAndBreakdownMap(reviewList));

        // initialize disabled dates
        List<LocalDate> disabledStartDateList = new ArrayList<>();
        List<LocalDate> disabledEndDateList = new ArrayList<>();

        rentalRequestList.forEach(obj -> {
            LocalDate localStartDate = obj.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate localEndDate = obj.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            disabledStartDateList.add(localStartDate);
            disabledEndDateList.add(localEndDate);
        });

        mav.addObject("disabledStartDateList", disabledStartDateList);
        mav.addObject("disabledEndDateList", disabledEndDateList);
        return mav;
    }

    private Map<String, String> getAvgRateAndBreakdownMap(List<Review> reviewList) {
        Map<String, String> rateMap = new HashMap<>();
        double totalRate = 0;
        for (Review review : reviewList) {
            if (rateMap.containsKey(review.getRate().toString())) {
                int count = Integer.parseInt(rateMap.get(review.getRate().toString()));
                count++;
                rateMap.put(review.getRate().toString(), Integer.toString(count));
            } else {
                rateMap.put(review.getRate().toString(), "1");
            }
            totalRate += review.getRate();
        }
        rateMap.put("avg", String.valueOf(totalRate / reviewList.size()));
        return rateMap;
    }

    @RequestMapping(value = "/browse_product", method = RequestMethod.GET)
    public ModelAndView browseProductList(@RequestParam("category") String category) {
        ModelAndView mav = new ModelAndView("browse_product");
        Category categoryEntity = null;
        List<Product> productList;
        if (!StringUtils.isEmpty(category)) {
            categoryEntity = categoryRepository.findById(Integer.parseInt(category)).orElse(null);
        }
        if (categoryEntity!=null) {
            productList = productRepository.getProductListByCategory(categoryEntity);
        } else {
            productList = productRepository.findAll();
        }
        mav.addObject("productList", productList);
        mav.addObject("productImageMap", this.getPrimaryProductImageMap(productList));
        mav.addObject("priceMap", this.getPriceMap(productList));

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
        mav.addObject("product", new Product());
        return mav;
    }

    @RequestMapping(value = "/access_denied", method = RequestMethod.GET)
    public ModelAndView accessDenied() {
        return new ModelAndView("/access_denied");
    }

    @RequestMapping(value = "/calendar", method = RequestMethod.GET)
    public ModelAndView calendar() {
        ModelAndView mav = new ModelAndView("/calendar");
        return mav;
    }
}
