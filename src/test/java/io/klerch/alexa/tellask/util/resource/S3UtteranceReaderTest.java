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
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class S3UtteranceReaderTest extends UtteranceReaderTest<S3UtteranceReader> {

    private AmazonS3Client givenS3Mock() {
        return Mockito.mock(AmazonS3Client.class, new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                if (invocation.getMethod().getName().equals("getObject")) {
                    // second parameter is the path to the file
                    final String resourcePath = invocation.getArguments()[1].toString();
                    // extract leading path
                    final String leadingPath = resourcePath.substring(0, resourcePath.indexOf(LOCALE));
                    // extract trailing path
                    final String trailingPath = resourcePath.substring(resourcePath.indexOf(LOCALE) + LOCALE.length() + 1);
                    // we reroute this request to the resourceutterancereader
                    final ResourceUtteranceReader resourceReader = new ResourceUtteranceReader(leadingPath);
                    resourceReader.setResourceLocation(trailingPath);
                    final S3Object s3Object = new S3Object();
                    s3Object.setObjectContent(resourceReader.read(LOCALE));
                    return s3Object;
                }
                return invocation.callRealMethod();
            }
        });
    }

    @Override
    S3UtteranceReader givenReader() throws Exception {
        return new S3UtteranceReader(givenS3Mock(), "bucketName", S3UtteranceReader.DEFAULT_LEADING_PATH, S3UtteranceReader.DEFAULT_RESOURCE_LOCATION);
    }

    @Override
    S3UtteranceReader givenReaderWithLeadingPath(String leadingPath) throws Exception {
        return new S3UtteranceReader(givenS3Mock(), "bucketName", leadingPath, S3UtteranceReader.DEFAULT_RESOURCE_LOCATION);
    }

    @Test
    public void testConstructors() throws Exception {
        final S3UtteranceReader reader = new S3UtteranceReader("bucketName");
        Assert.assertEquals("bucketName", reader.getBucketName());

        final S3UtteranceReader reader2 = new S3UtteranceReader("bucketName", "/leading/path/");
        Assert.assertEquals("bucketName", reader2.getBucketName());
        Assert.assertEquals("leading/path/", reader2.getLeadingPath());

        final S3UtteranceReader reader3 = new S3UtteranceReader("bucketName", "leading/path", "trailing/test.yml");
        Assert.assertEquals("bucketName", reader3.getBucketName());
        Assert.assertEquals("leading/path/", reader3.getLeadingPath());
        Assert.assertEquals("/trailing/test.yml", reader3.getResourceLocation());

        exception.expect(IllegalArgumentException.class);
        new S3UtteranceReader(null, "bucketName", "leadingPath");

        exception.expect(IllegalArgumentException.class);
        new S3UtteranceReader("");

        exception.expect(IllegalArgumentException.class);
        new S3UtteranceReader("", "leadingPath");

        exception.expect(IllegalArgumentException.class);
        new S3UtteranceReader(new AmazonS3Client(), "", "leadingPath", S3UtteranceReader.DEFAULT_RESOURCE_LOCATION);
    }
}