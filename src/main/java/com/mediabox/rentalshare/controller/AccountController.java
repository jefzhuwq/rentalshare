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
import org.springframework.util.StringUtils;
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
import java.time.Duration;
import java.time.ZoneId;
import java.util.*;

import static com.mediabox.rentalshare.utils.Constants.UPLOADED_FOLDER;


@Controller
public class AccountController {

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

    @RequestMapping(value = "/my_account", method = RequestMethod.GET)
    public ModelAndView myAccount() {
        ModelAndView mav = new ModelAndView("/account/my_account");
        return mav;
    }

    @RequestMapping(value = "/my_favorite", method = RequestMethod.GET)
    public ModelAndView myFavorite() {
        ModelAndView mav = new ModelAndView("/account/my_favorite");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        List<Favorite> favoriteList = favoriteRepository.findByUser(userRepository.findByEmail(name));
        mav.addObject("favoriteList", favoriteList);

        Map<Integer, List<ProductImage>> productImageMap = new HashMap<>();
        Map<Integer, List<Price>> priceMap = new HashMap<>();
        for (Favorite favorite : favoriteList) {
            productImageMap.put(favorite.getProduct().getId(), productImageRepository.findByProduct(favorite.getProduct()));
            priceMap.put(favorite.getProduct().getId(), priceRepository.findByProduct(favorite.getProduct()));
        }
        mav.addObject("productImageMap", productImageMap);
        mav.addObject("priceMap", priceMap);
        return mav;
    }

    @RequestMapping(value = "/my_order", method = RequestMethod.GET)
    public ModelAndView myOrder() {
        ModelAndView mav = new ModelAndView("/account/my_order");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        List<RentalRequest> rentalRequestList = rentalRequestRepository.findByUser(userRepository.findByEmail(name));
        mav.addObject("rentalRequestList", rentalRequestList);
        return mav;
    }

    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public ModelAndView cart() {
        ModelAndView mav = new ModelAndView("cart");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        List<RentalRequest> rentalRequestList = rentalRequestRepository.findByUserAndStatus(userRepository.findByEmail(name), RentalRequestStatus.IN_CART.getValue());
        mav.addObject("rentalRequestList", rentalRequestList);

        double totalPrice = 0;

        Map<Integer, List<Price>> priceMap = new HashMap<>();
        String result = "";

        for (RentalRequest request : rentalRequestList) {
            long rentalDays = Duration.between(
                    request.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(),
                    request.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay()).toDays() + 1;
//            result += ",day:" + rentalDays + ", price" + priceRepository.findByProduct(request.getProduct()).get(0).getUnitPrice();
            totalPrice += priceRepository.findByProduct(request.getProduct()).get(0).getUnitPrice() * rentalDays;
            priceMap.put(request.getProduct().getId(), priceRepository.findByProduct(request.getProduct()));
        }
        mav.addObject("totalPrice", totalPrice);
        mav.addObject("priceMap", priceMap);

        return mav;
    }

    @RequestMapping(value = "/checkout_cart", method = RequestMethod.GET)
    public ModelAndView checkoutCart() {
        ModelAndView mav = new ModelAndView("/account/checkout_cart");
        return mav;
    }

    @RequestMapping(value = "/submit_order", method = RequestMethod.POST)
    public ModelAndView submitOrder() {
        ModelAndView mav = new ModelAndView("redirect:/my_order");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        // Find all in cart requests
        List<RentalRequest> rentalRequestList = rentalRequestRepository.findByUserAndStatus(userRepository.findByEmail(name), RentalRequestStatus.IN_CART.getValue());

        for (RentalRequest request : rentalRequestList) {
            request.setStatus(RentalRequestStatus.SUBMITTED.getValue());
            request.setUpdateTimestamp(new Date());
        }
        rentalRequestRepository.saveAll(rentalRequestList);

        return mav;
    }

    @RequestMapping(value = "/add_to_favorite", method = RequestMethod.POST)
    public ModelAndView addToFavorite(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("redirect:/my_favorite");
        Favorite favorite = new Favorite();
        String productId = request.getParameter("productId");
        ModelAndView mav = new ModelAndView("redirect:/edit_product/" + productId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        Product product = productRepository.findById(Integer.parseInt(productId)).get();

        favorite.setProduct(product);
        favorite.setCreateTimestamp(new Date());
        favorite.setUpdateTimestamp(new Date());

        favorite.setUser(userRepository.findByEmail(name));

        favoriteRepository.save(favorite);
        return modelAndView;
    }

    @RequestMapping(value = "/my_schedule", method = RequestMethod.GET)
    public ModelAndView mySchedule() {
        ModelAndView mav = new ModelAndView("/account/my_schedule");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        List<Product> productList = productRepository.findByUser(userRepository.findByEmail(name));
        mav.addObject("productList", productList);

        if (productList.size()>0) {
            List<RentalRequest> rentalRequestList = rentalRequestRepository.findByProduct(productList.get(0));
            mav.addObject("rentalRequestList", rentalRequestList);
        }
        return mav;
    }

    @RequestMapping(value = "/my_schedule", method = RequestMethod.POST)
    public ModelAndView selectProductSchedule(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("/account/my_schedule");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        String selectedProductId = request.getParameter("select_product");
        List<Product> productList = productRepository.findByUser(userRepository.findByEmail(name));
        mav.addObject("productList", productList);
        mav.addObject("selectedProductId", selectedProductId);

        for (Product product: productList) {
            if (product.getId().equals(Integer.parseInt(selectedProductId))) {
                List<RentalRequest> rentalRequestList = rentalRequestRepository.findByProduct(product);
                mav.addObject("rentalRequestList", rentalRequestList);
                break;
            }
        }
        return mav;
    }

    @RequestMapping(value = "/post_product", method = RequestMethod.POST)
    public ModelAndView createNewProduct(@ModelAttribute Product product) {
        ModelAndView modelAndView = new ModelAndView("redirect:/product_list");
        product.setCreateTimestamp(new Date());
        product.setUpdateTimestamp(new Date());
        product.setIsActive(1);

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

            List<ProductImage> productImageList = productImageRepository.findByProduct(product);
            mav.addObject("productImageList", productImageList);
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
            productEdit.setLocation(product.getLocation());
            productEdit.setZipCode(product.getZipCode());
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

    @RequestMapping(value = "/delete_image/{id}", method = RequestMethod.GET)
    public ModelAndView deleteImage(@PathVariable("id") int id) {
        ProductImage productImage = productImageRepository.findById(id).get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        ModelAndView mav = new ModelAndView();
        if (productImage.getProduct().getUser().getEmail().equals(name)) {
            mav.setViewName("redirect:/edit_product/" + productImage.getProduct().getId());
            productImageRepository.delete(productImage);
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
            price.setPeriodType(PeriodType.DAY.getValue());
            price.setCreateTimestamp(new Date());
            price.setUpdateTimestamp(new Date());
            priceRepository.save(price);
        } else{
            mav.setViewName("/error");
        }

        return mav;
    }


    @RequestMapping(value = "/rent_product", method = RequestMethod.POST)
    public ModelAndView rentProduct(HttpServletRequest request) {
        String productId = request.getParameter("productId");
        String shippingOptionValue = request.getParameter("shippingOption");
        Product product = productRepository.findById(Integer.parseInt(productId)).get();
        ModelAndView mav = new ModelAndView("redirect:/view_product/" + productId);

        String startDateString = request.getParameter("startDate");
        String endDateString = request.getParameter("endDate");

        if (product != null && !StringUtils.isEmpty(startDateString) && !StringUtils.isEmpty(endDateString)) {
            Date startDate = DateTime.parse(startDateString).toDate();
            Date endDate = DateTime.parse(endDateString).toDate();

            if (!isDateRangeAvailable(product, startDate, endDate)) {
                mav.addObject("errorMessage", "Selected date range is not available.");
                return mav;
            }


            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String name = auth.getName(); //get logged in username
            User user = userRepository.findByEmail(name);

            RentalRequest rentalRequest = new RentalRequest();

            rentalRequest.setProduct(product);
            rentalRequest.setRequester(user);
            rentalRequest.setCreateTimestamp(new Date());
            rentalRequest.setUpdateTimestamp(new Date());
            rentalRequest.setStartDate(startDate);
            rentalRequest.setEndDate(endDate);
            rentalRequest.setStatus(RentalRequestStatus.IN_CART.getValue());
            rentalRequest.setShippingOption(Integer.parseInt(shippingOptionValue));

            rentalRequestRepository.save(rentalRequest);
        }
        return mav;
    }

    private boolean isDateRangeAvailable(Product product, Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            return false;
        }
        List<RentalRequest> rentalRequestList = this.rentalRequestRepository.findByProduct(product);
        for (RentalRequest rentalRequest : rentalRequestList) {
            if (rentalRequest.getStartDate().after(startDate) && rentalRequest.getStartDate().before(endDate)) {
                return false;
            }
            if (rentalRequest.getStartDate().equals(startDate) && rentalRequest.getStartDate().equals(endDate)) {
                return false;
            }
            if (rentalRequest.getEndDate().after(startDate) && rentalRequest.getEndDate().before(endDate)) {
                return false;
            }
            if (rentalRequest.getEndDate().equals(startDate) && rentalRequest.getEndDate().equals(endDate)) {
                return false;
            }
        }
        return true;
    }

    @PostMapping("/upload")
    public ModelAndView singleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            mav.setViewName("redirect:/index");
            return mav;
        }

        try {
            // get product
            String productId = request.getParameter("productId");
            mav.setViewName("redirect:/edit_product/" + productId);

            // Get the file and save it somewhere
            this.saveFileToLocal(file);

            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");

            this.addProductImage(productId, file.getOriginalFilename());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return mav;
    }

    private void saveFileToLocal(MultipartFile file) throws IOException {
        // Get the file and save it somewhere
        byte[] bytes = file.getBytes();
        Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
        Files.write(path, bytes);
    }

    private void addProductImage(String productId, String filePath) {
        ProductImage productImage = new ProductImage();
        Product productEdit = productRepository.findById(Integer.parseInt(productId)).get();
        productImage.setImagePath(filePath);
        productImage.setCreateTimestamp(new Date());
        productImage.setUpdateTimestamp(new Date());
        productImage.setProduct(productEdit);
        productImageRepository.save(productImage);
    }

    String clientRegion = "*** Client region ***";
    String bucketName = "*** Bucket name ***";
    String stringObjKeyName = "*** String object key name ***";
    String fileObjKeyName = "*** File object key name ***";
    String fileName = "*** Path to file to upload ***";

    private void addProductImageToS3() {
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .withCredentials(new ProfileCredentialsProvider())
                    .build();

            // Upload a text string as a new object.
            s3Client.putObject(bucketName, stringObjKeyName, "Uploaded String Object");

            // Upload a file as a new object with ContentType and title specified.
            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, new File(fileName));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("x-amz-meta-title", "someTitle");
            request.setMetadata(metadata);
            s3Client.putObject(request);
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }
}
