package com.mediabox.rentalshare.repository;

import com.mediabox.rentalshare.model.Price;
import com.mediabox.rentalshare.model.Product;
import com.mediabox.rentalshare.model.RentalRequest;
import com.mediabox.rentalshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRequestRepository extends JpaRepository<RentalRequest, Integer> {
    @Query("SELECT p FROM RentalRequest p where p.product= :product")
    List<RentalRequest> findByProduct(@Param("product") Product product);

    @Query("SELECT p FROM RentalRequest p where p.product= :product and p.status= :status")
    List<RentalRequest> findByProductAndStatus(@Param("product") Product product, @Param("status") Integer status);

    @Query("SELECT p FROM RentalRequest p where p.requester= :requester")
    List<RentalRequest> findByUser(@Param("requester") User user);

    @Query("SELECT p FROM RentalRequest p where p.requester= :requester and p.status=:status")
    List<RentalRequest> findByUserAndStatus(@Param("requester") User user, @Param("status") Integer status);
}
