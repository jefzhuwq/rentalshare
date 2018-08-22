package com.mediabox.rentalshare.repository;

import com.mediabox.rentalshare.model.Price;
import com.mediabox.rentalshare.model.Product;
import com.mediabox.rentalshare.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    @Query("SELECT pi FROM ProductImage pi where pi.product= :product")
    List<ProductImage> findByProduct(@Param("product") Product product);
}
