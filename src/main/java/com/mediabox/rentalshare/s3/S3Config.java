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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@Configuration
public class S3Config {
    private Regions region = Regions.US_EAST_2;

    @Bean
    public AmazonS3 s3Client() throws IOException {
        List<String> credential = FileUtils.readLines(new File("D:\\temp\\aws_credential.txt"), Charset.defaultCharset());
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(credential.get(0), credential.get(1));
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        return s3Client;
    }

}
