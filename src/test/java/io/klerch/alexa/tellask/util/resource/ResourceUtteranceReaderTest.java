/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
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
