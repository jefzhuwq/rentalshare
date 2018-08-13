package com.mediabox.rentalshare.repository;

import com.mediabox.rentalshare.model.Category;
import com.mediabox.rentalshare.model.Product;
import com.mediabox.rentalshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p where p.user= :user")
    List<Product> findByUser(@Param("user") User user);
}
