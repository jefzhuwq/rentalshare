package com.mediabox.rentalshare.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

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

    @Column(name = "is_active")
    private int isActive;

    @Column(name = "createtimestamp")
    private Date createTimestamp;

    @Column(name = "updatetimestamp")
    private Date updateTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
