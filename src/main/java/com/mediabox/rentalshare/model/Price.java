package com.mediabox.rentalshare.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="price")
public class Price {
	
    @Id
    @Column(name="price_id")
    @GeneratedValue
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;
}