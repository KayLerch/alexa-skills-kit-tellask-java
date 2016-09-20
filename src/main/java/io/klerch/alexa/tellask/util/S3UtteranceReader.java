package io.klerch.alexa.tellask.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import io.klerch.alexa.tellask.schema.UtteranceReader;
import org.apache.commons.lang3.Validate;

import java.io.InputStream;

/**
 * An implementation to the UtteranceReader interface which encapsulates
 * logic of reading out a set of utterances from S3-files. Thus
 * it expects YAML files (utterances.yml) in a bucket you provide in the constructor.
 * As long as you don't override the resourceLocation it looks
 * for this YAML file in directories whose name equals the locale coming in
 * from the speechlet request (e.g. /en-US/utterances.yml). That said it is
 * possible to provide utterances for different locales just by having those
 * files in the right place.
 */
public class S3UtteranceReader implements UtteranceReader {
    private final AmazonS3Client s3Client;
    private final String bucketName;
    private final String leadingPath;
    private String resourceLocation = "/utterances.yml";

    /**
     * A new S3 reader pointing to a bucket
     * @param bucketName name of the S3 bucket
     */
    public S3UtteranceReader(final String bucketName) {
        // initializes an AWS client with default configuration obtained from environment
        this.s3Client = new AmazonS3Client();
        this.bucketName = bucketName;
        this.leadingPath = "/";
    }

    /**
     * A new S3 reader pointing to a bucket and a leading path. Note that a
     * fully qualified path is a concatenation of the leading path (by default "/" when not providing one),
     * the locale (given to the read-method) and the trailing path containing the actual file (can be overridden
     * by setting resourceLocation). You may end up with something like /my/leading/path/en-US/my/trailing/path/utterances.yml
     * @param bucketName name of the S3 bucket
     * @param leadingPath valid path within the bucket
     */
    public S3UtteranceReader(final String bucketName, final String leadingPath) {
        // initializes an AWS client with default configuration obtained from environment
        this.s3Client = new AmazonS3Client();
        this.bucketName = bucketName;
        this.leadingPath = leadingPath;
    }

    /**
     * A new S3 reader pointing to a bucket and a leading path in it (if desired). For
     * reading from S3 it will use the S3 client you provide. Note that a
     * fully qualified path is a concatenation of the leading path (by default "/" when not providing one),
     * the locale (given to the read-method) and the trailing path containing the actual file (can be overridden
     * by setting resourceLocation). You may end up with something like /my/leading/path/en-US/my/trailing/path/utterances.yml
     * @param client S3 client to communicate with AWS SDK for S3
     * @param bucketName name of the S3 bucket
     * @param leadingPath valid path within the bucket
     */
    public S3UtteranceReader(final AmazonS3Client client, final String bucketName, String leadingPath) {
        this.s3Client = client;
        this.bucketName = bucketName;

        if (!leadingPath.startsWith("/"))
            leadingPath += "/";
        if (!leadingPath.endsWith("/"))
            leadingPath = "/" + leadingPath;
        this.leadingPath = leadingPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceLocation() {
        return this.resourceLocation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResourceLocation(final String resourceLocation) {
        Validate.notBlank(resourceLocation, "No resource location is set to read from.");
        Validate.notBlank(resourceLocation.replace("/", ""), "No resource location is set to read from.");
        Validate.isTrue(resourceLocation.endsWith(".yml"), "Resource location must end with .yml");
        this.resourceLocation = resourceLocation.startsWith("/") ? resourceLocation : "/" + resourceLocation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S3UtteranceReader fromResourceLocation(final String resourceLocation) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream read(final String locale) {
        Validate.notNull(locale, "Locale must not be blank.");

        final String resourcePath = leadingPath + locale + resourceLocation;

        final S3Object s3Object = s3Client.getObject(bucketName, resourcePath);
        Validate.notNull(s3Object, "Resource " + resourcePath + " does not exist in bucket with name " + bucketName);
        return s3Object.getObjectContent();
    }
}
