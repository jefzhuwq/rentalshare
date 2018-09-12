package com.mediabox.rentalshare.repository;

import com.mediabox.rentalshare.model.Price;
import com.mediabox.rentalshare.model.Product;
import com.mediabox.rentalshare.model.RentalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRequestRepository extends JpaRepository<RentalRequest, Integer> {
    @Query("SELECT p FROM RentalRequest p where p.product= :product")
    List<RentalRequest> findByProduct(@Param("product") Product product);
}
