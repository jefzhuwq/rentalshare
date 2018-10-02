package com.mediabox.rentalshare.repository;

import com.mediabox.rentalshare.model.Category;
import com.mediabox.rentalshare.model.Product;
import com.mediabox.rentalshare.model.Review;
import com.mediabox.rentalshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @Query("SELECT p FROM Review p where p.user= :user")
    List<Review> findByUser(@Param("user") User user);

    @Query("SELECT p FROM Review p where p.product=:product")
    List<Review> findByProduct(@Param("product") Product product);
}
