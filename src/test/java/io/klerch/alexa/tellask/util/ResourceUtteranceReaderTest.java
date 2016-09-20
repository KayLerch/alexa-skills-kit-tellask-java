package io.klerch.alexa.tellask.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.rules.ExpectedException;

import java.io.InputStream;

import static org.junit.Assert.*;

public class ResourceUtteranceReaderTest {
    private ResourceUtteranceReader reader;
    private static final String LOCALE = "en-US";
    private static final String leadingPath = "/my/leading/path";
    private static final String trailingPath = "/my/trailing/path/utterances.yml";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void init() throws Exception {
        reader = new ResourceUtteranceReader();
    }

    @Test
    public void getFromResourceWithLeadingAndTrailingPath() throws Exception {
        final ResourceUtteranceReader reader1 = new ResourceUtteranceReader(leadingPath);
        reader1.setResourceLocation(trailingPath);
        final InputStream stream = reader1.read(LOCALE);
        Assert.assertNotNull(stream);
    }

    @Test
    public void getFromResourceWithLeadingPath() throws Exception {
        final ResourceUtteranceReader reader1 = new ResourceUtteranceReader(leadingPath);
        final InputStream stream = reader1.read(LOCALE);
        Assert.assertNotNull(stream);
    }

    @Test
    public void getFromResourceWithTrailingPath() throws Exception {
        final ResourceUtteranceReader reader1 = new ResourceUtteranceReader();
        reader1.setResourceLocation(trailingPath);
        final InputStream stream = reader1.read(LOCALE);
        Assert.assertNotNull(stream);
    }

    @Test
    public void getSetResourceLocation() throws Exception {
        reader.setResourceLocation("new_resource.yml");
        Assert.assertEquals("/new_resource.yml", reader.getResourceLocation());

        reader.setResourceLocation("new_resource.yml");
        Assert.assertEquals("/new_resource.yml", reader.getResourceLocation());

        exception.expect(NullPointerException.class);
        reader.setResourceLocation(null);

        exception.expect(NullPointerException.class);
        reader.setResourceLocation("");

        exception.expect(NullPointerException.class);
        reader.setResourceLocation("/");
    }

    @Test
    public void fromResourceLocation() throws Exception {
        reader.fromResourceLocation("new_resource.yml");
        Assert.assertEquals("/new_resource.yml", reader.getResourceLocation());

        reader.fromResourceLocation("new_resource.yml");
        Assert.assertEquals("/new_resource.yml", reader.getResourceLocation());

        exception.expect(NullPointerException.class);
        reader.fromResourceLocation(null);

        exception.expect(NullPointerException.class);
        reader.fromResourceLocation("");

        exception.expect(NullPointerException.class);
        reader.fromResourceLocation("/");
    }

    @Test
    public void read() throws Exception {
        reader.setResourceLocation(ResourceUtteranceReader.DEFAULT_RESOURCE_LOCATION);
        Assert.assertNotNull(reader.read(LOCALE));

        reader.setResourceLocation("this-file-does-not-exist.yml");
        exception.expect(NullPointerException.class);
        reader.read(LOCALE);
    }

}