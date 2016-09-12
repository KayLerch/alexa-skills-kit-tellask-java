package io.klerch.alexa.tellask.schema;

import io.klerch.alexa.tellask.util.ResourceUtteranceReader;

import java.io.InputStream;

public interface UtteranceReader {
    String getResourceLocation();
    void setResourceLocation(final String resourceLocation);
    ResourceUtteranceReader fromResourceLocation(final String resourceLocation);
    InputStream read();
}
