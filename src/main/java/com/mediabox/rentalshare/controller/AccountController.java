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
        Product product = productRepository.findById(Integer.parseInt(productId)).get();
        ModelAndView mav = new ModelAndView("redirect:/view_product/" + productId);
        if (product != null) {
            Date date = DateTime.parse(request.getParameter("startDate")).toDate();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String name = auth.getName(); //get logged in username
            User user = userRepository.findByEmail(name);

            RentalRequest rentalRequest = new RentalRequest();

            rentalRequest.setProduct(product);
            rentalRequest.setRequester(user);
            rentalRequest.setCreateTimestamp(new Date());
            rentalRequest.setUpdateTimestamp(new Date());
            rentalRequest.setStartDate(date);

            rentalRequestRepository.save(rentalRequest);
        }
        return mav;
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
