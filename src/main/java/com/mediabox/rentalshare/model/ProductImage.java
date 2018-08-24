package com.mediabox.rentalshare.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "product_image")
public class ProductImage {

    @Id
    @Column(name = "product_image_id")
    @GeneratedValue
    private Integer id;

    @Column(name = "image_path")
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    @Column(name = "createtimestamp")
    private Date createTimestamp;

    @Column(name = "updatetimestamp")
    private Date updateTimestamp;
}
