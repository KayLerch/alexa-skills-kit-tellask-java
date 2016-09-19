package io.klerch.alexa.tellask.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class ResourceUtteranceReaderTest {
    private ResourceUtteranceReader reader;
    private static final String LOCALE = "en-US";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void init() throws Exception {
        reader = new ResourceUtteranceReader();
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
        reader.setResourceLocation(reader.defaultResourceLocation);
        Assert.assertNotNull(reader.read(LOCALE));

        reader.setResourceLocation("this-file-does-not-exist.yml");
        exception.expect(NullPointerException.class);
        reader.read(LOCALE);
    }

}