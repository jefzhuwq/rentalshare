package com.mediabox.rentalshare.model;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Data
@Table(name="category")
@EntityListeners(AuditingEntityListener.class)
public class Category {
	
    @Id
    @Column(name="category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer category_id;
    
    @Column(name="category_name")
    private String categoryName;
}
