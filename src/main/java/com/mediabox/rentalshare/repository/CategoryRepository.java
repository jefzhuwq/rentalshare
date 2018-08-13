package com.mediabox.rentalshare.repository;

import com.mediabox.rentalshare.model.Category;
import com.mediabox.rentalshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
