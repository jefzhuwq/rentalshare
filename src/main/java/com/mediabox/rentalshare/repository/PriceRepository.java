package com.mediabox.rentalshare.repository;

import com.mediabox.rentalshare.model.Price;
import com.mediabox.rentalshare.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<Price, Integer> {
    @Query("SELECT p FROM Price p where p.product= :product")
    List<Price> findByProduct(@Param("product") Product product);
}
