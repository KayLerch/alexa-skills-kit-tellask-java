package io.klerch.alexa.tellask.util;

import io.klerch.alexa.tellask.schema.UtteranceReader;
import org.apache.commons.lang3.Validate;

import java.io.InputStream;

public class ResourceUtteranceReader implements UtteranceReader {
    public final String defaultResourceLocation = "/utterances.yml";
    private String resourceLocation = defaultResourceLocation;

    public String getResourceLocation() {
        return this.resourceLocation;
    }

    public void setResourceLocation(final String resourceLocation) {
        Validate.notBlank(resourceLocation, "No resource location is set to read from.");
        Validate.notBlank(resourceLocation.replace("/", ""), "No resource location is set to read from.");
        this.resourceLocation = resourceLocation.startsWith("/") ? resourceLocation : "/" + resourceLocation;
    }

    public ResourceUtteranceReader fromResourceLocation(final String resourceLocation) {
        setResourceLocation(resourceLocation);
        return this;
    }

    public InputStream read() {
        Validate.notNull(ClassLoader.class.getResource(resourceLocation), "Resource " + resourceLocation + " does not exist in current context.");
        return ClassLoader.class.getResourceAsStream(resourceLocation);
    }
}
