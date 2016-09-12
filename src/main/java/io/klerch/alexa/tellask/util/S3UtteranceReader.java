package io.klerch.alexa.tellask.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import io.klerch.alexa.tellask.schema.UtteranceReader;
import org.apache.commons.lang3.Validate;

import java.io.InputStream;

public class S3UtteranceReader implements UtteranceReader {
    private final AmazonS3Client s3Client;
    private final String bucketName;
    private final String bucketFolder;
    private String resourceLocation;

    public S3UtteranceReader(final String bucketName, final String path) {
        // initializes an AWS client with default configuration obtained from environment
        this.s3Client = new AmazonS3Client();
        this.bucketName = bucketName;
        this.bucketFolder = path;
        this.resourceLocation = "";
    }

    public S3UtteranceReader(final AmazonS3Client client, final String bucketName, final String path) {
        this.s3Client = client;
        this.bucketName = bucketName;
        this.bucketFolder = path;
        this.resourceLocation = "";
    }

    @Override
    public String getResourceLocation() {
        return this.resourceLocation;
    }

    @Override
    public void setResourceLocation(final String resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    @Override
    public ResourceUtteranceReader fromResourceLocation(final String resourceLocation) {
        return null;
    }

    @Override
    public InputStream read() {
        Validate.notBlank(resourceLocation);
        final S3Object s3Object = s3Client.getObject(bucketName, bucketFolder + resourceLocation);
        Validate.notNull(s3Object, "Resource " + resourceLocation + " does not exist in current context.");
        return s3Object.getObjectContent();
    }
}
