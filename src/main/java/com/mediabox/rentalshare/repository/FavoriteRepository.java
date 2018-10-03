package com.mediabox.rentalshare.repository;

import com.mediabox.rentalshare.model.Favorite;
import com.mediabox.rentalshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    @Query("SELECT p FROM Favorite p where p.user= :user")
    List<Favorite> findByUser(@Param("user") User user);
}
