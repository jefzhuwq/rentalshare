package com.mediabox.rentalshare.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@Table(name="contact_us")
public class ContactUs {
	
    @Id
    @Column(name="contact_id")
    @GeneratedValue
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name="full_name")
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "status")
    private int status;

    @Column(name = "message")
    private String message;

    @Column(name = "createtimestamp")
    private Date createTimestamp;

    @Column(name = "updatetimestamp")
    private Date updateTimestamp;
}
