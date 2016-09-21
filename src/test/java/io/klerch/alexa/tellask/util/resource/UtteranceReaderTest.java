package io.klerch.alexa.tellask.util.resource;

import io.klerch.alexa.tellask.schema.UtteranceReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.InputStream;

public abstract class UtteranceReaderTest<READER extends UtteranceReader> {
    private READER reader;
    public static final String LOCALE = "en-US";
    private static final String leadingPath = "/my/leading/path/";
    private static final String trailingPath = "/my/trailing/path/utterances.yml";

    abstract READER givenReader() throws Exception;

    abstract READER givenReaderWithLeadingPath(final String leadingPath) throws Exception;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void init() throws Exception {
        reader = givenReader();
    }

    @Test
    public void getFromResourceWithLeadingAndTrailingPath() throws Exception {
        final READER reader1 = givenReaderWithLeadingPath(leadingPath);
        reader1.setResourceLocation(trailingPath);
        final InputStream stream = reader1.read(LOCALE);
        Assert.assertNotNull(stream);
        Assert.assertEquals(leadingPath, reader1.getLeadingPath());
        Assert.assertEquals(trailingPath, reader1.getResourceLocation());
    }

    @Test
    public void getFromResourceWithLeadingPath() throws Exception {
        final READER reader1 = givenReaderWithLeadingPath(leadingPath);
        final InputStream stream = reader1.read(LOCALE);
        Assert.assertNotNull(stream);
        Assert.assertEquals(leadingPath, reader1.getLeadingPath());
    }

    @Test
    public void getFromResourceWithTrailingPath() throws Exception {
        final READER reader1 = givenReader();
        reader1.setResourceLocation(trailingPath);
        final InputStream stream = reader1.read(LOCALE);
        Assert.assertNotNull(stream);
    }

    @Test
    public void getSetResourceLocation() throws Exception {
        final READER reader1 = givenReader();
        reader1.setResourceLocation("new_resource.yml");
        Assert.assertEquals("/new_resource.yml", reader1.getResourceLocation());

        reader1.setResourceLocation("new_resource.yml");
        Assert.assertEquals("/new_resource.yml", reader1.getResourceLocation());

        exception.expect(NullPointerException.class);
        reader1.setResourceLocation(null);

        exception.expect(NullPointerException.class);
        reader1.setResourceLocation("");

        exception.expect(NullPointerException.class);
        reader1.setResourceLocation("/");
    }

    @Test
    public void fromResourceLocation() throws Exception {
        final READER reader1 = givenReader();
        reader1.fromResourceLocation("new_resource.yml");
        Assert.assertEquals("/new_resource.yml", reader1.getResourceLocation());

        reader1.fromResourceLocation("new_resource.yml");
        Assert.assertEquals("/new_resource.yml", reader1.getResourceLocation());

        exception.expect(NullPointerException.class);
        reader1.fromResourceLocation(null);

        exception.expect(NullPointerException.class);
        reader1.fromResourceLocation("");

        exception.expect(NullPointerException.class);
        reader1.fromResourceLocation("/");
    }

    @Test
    public void read() throws Exception {
        final READER reader1 = givenReader();
        Assert.assertNotNull(reader.read(LOCALE));

        reader.setResourceLocation("this-file-does-not-exist.yml");
        exception.expect(NullPointerException.class);
        reader.read(LOCALE);
    }

}