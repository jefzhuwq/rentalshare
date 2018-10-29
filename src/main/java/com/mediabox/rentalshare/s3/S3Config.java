package com.mediabox.rentalshare.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

@Configuration
public class S3Config {
    private Regions region = Regions.US_EAST_2;

    @Value("${s3client.accessKey}")
    private String accessKey;

    @Value("${s3client.secretKey}")
    private String secretKey;

    @Bean
    public AmazonS3 s3Client() throws IOException {
//        List<String> credential = FileUtils.readLines(new File("aws_credential.txt"), Charset.defaultCharset());
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        return s3Client;
    }

    private Properties getProperties() throws IOException {
        Properties prop = new Properties();
        InputStream input = new FileInputStream("/application.properties");
        // load a properties file
        prop.load(input);
        return prop;
    }

}
