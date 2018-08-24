package com.mediabox.rentalshare.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="price")
public class Price {
	
    @Id
    @Column(name="price_id")
    @GeneratedValue
    private Integer id;

    @Column(name = "unit_price")
    private double unitPrice;

    @Column(name = "period_type")
    private int periodType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    @Column(name = "createtimestamp")
    private Date createTimestamp;

    @Column(name = "updatetimestamp")
    private Date updateTimestamp;
}