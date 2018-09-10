package com.mediabox.rentalshare.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue
    private Integer id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "location")
    private String location;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "is_active")
    private int isActive;

    @Column(name = "createtimestamp")
    private Date createTimestamp;

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name="product_id")
//    private List<ProductImage> productImageList;

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name="product_id")
//    private List<Price> priceList;

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name="product_id")
//    private List<RentalRequest> rentalRequestList;

    @Column(name = "updatetimestamp")
    private Date updateTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
