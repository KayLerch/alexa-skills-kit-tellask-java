package io.klerch.alexa.tellask.util.resource;

public class ResourceUtteranceReaderTest extends UtteranceReaderTest<ResourceUtteranceReader> {

    @Override
    ResourceUtteranceReader givenReader() throws Exception {
        return new ResourceUtteranceReader();
    }

    @Override
    ResourceUtteranceReader givenReaderWithLeadingPath(final String leadingPath) throws Exception {
        return new ResourceUtteranceReader(leadingPath);
    }
}
