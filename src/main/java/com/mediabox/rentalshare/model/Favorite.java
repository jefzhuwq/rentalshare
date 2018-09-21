package com.mediabox.rentalshare.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "favorite")
public class Favorite {

    @Id
    @Column(name = "favorite_id")
    @GeneratedValue
    private Integer favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "createtimestamp")
    private Date createTimestamp;

    @Column(name = "updatetimestamp")
    private Date updateTimestamp;
}
