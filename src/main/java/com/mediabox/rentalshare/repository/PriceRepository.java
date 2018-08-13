package com.mediabox.rentalshare.repository;

import com.mediabox.rentalshare.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends JpaRepository<Product, Integer> {
}
