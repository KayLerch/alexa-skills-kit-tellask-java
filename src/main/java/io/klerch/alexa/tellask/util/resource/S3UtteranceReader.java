/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.util.resource;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
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
public class S3UtteranceReader extends ResourceUtteranceReader {
    private final AmazonS3Client s3Client;
    private final String bucketName;

    /**
     * A new S3 reader pointing to a bucket
     * @param bucketName name of the S3 bucket
     */
    public S3UtteranceReader(final String bucketName) {
        this(new AmazonS3Client(), bucketName, DEFAULT_LEADING_PATH, DEFAULT_RESOURCE_LOCATION);
    }

    /**
     * A new S3 reader pointing to a bucket and a leading path. Note that a
     * fully qualified path is a concatenation of the leading path (set in the constructor),
     * the locale (given to the read-method) and the trailing resource location containing the actual filename (can be overridden
     * by setting resourceLocation or providing the the constructor as well).
     * You may end up with something like /my/leading/path/en-US/my/trailing/path/utterances.yml
     * where "en-US" is the only portion you cannot influence as it comes with the locale of
     * a speechlet request.
     * @param bucketName name of the S3 bucket
     * @param leadingPath valid path within the bucket
     */
    public S3UtteranceReader(final String bucketName, final String leadingPath) {
        this(new AmazonS3Client(), bucketName, leadingPath, DEFAULT_RESOURCE_LOCATION);
    }

    /**
     * A new S3 reader pointing to a bucket and a leading path. Note that a
     * fully qualified path is a concatenation of the leading path (set in the constructor),
     * the locale (given to the read-method) and the trailing resource location containing the actual filename (can be overridden
     * by setting resourceLocation or providing the the constructor as well).
     * You may end up with something like /my/leading/path/en-US/my/trailing/path/utterances.yml
     * where "en-US" is the only portion you cannot influence as it comes with the locale of
     * a speechlet request.
     * @param bucketName name of the S3 bucket
     * @param leadingPath valid path within the bucket
     * @param resourceLocation the resource location. must end with ".yml"
     */
    public S3UtteranceReader(final String bucketName, final String leadingPath, final String resourceLocation) {
        this(new AmazonS3Client(), bucketName, leadingPath, resourceLocation);
    }

    /**
     * A new S3 reader pointing to a bucket and a leading path in it (if desired). For
     * reading from S3 it will use the S3 client you provide. Note that a
     * fully qualified path is a concatenation of the leading path (set in the constructor),
     * the locale (given to the read-method) and the trailing resource location containing the actual filename (can be overridden
     * by setting resourceLocation or providing the the constructor as well).
     * You may end up with something like /my/leading/path/en-US/my/trailing/path/utterances.yml
     * where "en-US" is the only portion you cannot influence as it comes with the locale of
     * a speechlet request.
     * @param client S3 client to communicate with AWS SDK for S3
     * @param bucketName name of the S3 bucket
     * @param leadingPath valid path within the bucket
     * @param resourceLocation the resource location. must end with ".yml"
     */
    public S3UtteranceReader(final AmazonS3Client client, final String bucketName, final String leadingPath, final String resourceLocation) {
        super(leadingPath, resourceLocation);

        Validate.notNull(client, "S3 client must not be null.");
        Validate.notBlank(bucketName, "Bucket name must not be blank.");

        this.s3Client = client;
        this.bucketName = bucketName;
    }

    /**
     * Returns the name of the S3 bucket.
     * @return name of the S3 bucket
     */
    public String getBucketName() {
        return this.bucketName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S3UtteranceReader fromResourceLocation(final String resourceLocation) {
        setResourceLocation(resourceLocation);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream read(final String locale) {
        Validate.notNull(locale, "Locale must not be blank.");

        final String resourcePath = getLeadingPath() + locale + getResourceLocation();

        final S3Object s3Object = s3Client.getObject(bucketName, resourcePath);

        Validate.notNull(s3Object, "Resource " + resourcePath + " does not exist in bucket with name " + bucketName);
        return s3Object.getObjectContent();
    }
}
