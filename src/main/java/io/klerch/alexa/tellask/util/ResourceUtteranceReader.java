package io.klerch.alexa.tellask.util;

import io.klerch.alexa.tellask.schema.UtteranceReader;
import org.apache.commons.lang3.Validate;

import java.io.InputStream;

/**
 * An implementation to the UtteranceReader interface which encapsulates
 * logic of reading out a set of utterances from the the class-loader. Thus
 * it expects YAML files (utterances.yml) in the resources-directory of your
 * skill project. It looks for this YAML file in directories whose name equals the locale coming in
 * from the speechlet request (e.g. /en-US/utterances.yml). You could provide
 * a leading path over the constructor to have your files stored like e.g.
 * /my/leading/path/en-US/utterances.yml). In addition by overriding the resourceLocation
 * you could add some trailing path to have something like this
 * /my/leading/path/en-US/my/trailing/path/utterances.yml
 * That said it is possible to provide utterances for different locales just
 * by having those files in the right place.
 */
public class ResourceUtteranceReader implements UtteranceReader {
    public final String defaultResourceLocation = "/utterances.yml";
    public final String leadingPath;
    private String resourceLocation = defaultResourceLocation;

    /**
     * New reader for classloader-resources. Note that a
     * fully qualified path is a concatenation of the leading path (by default "/" when not providing one),
     * the locale (given to the read-method) and the trailing path containing the actual file (can be overridden
     * by setting resourceLocation). You may end up with something like /my/leading/path/en-US/my/trailing/path/utterances.yml
     */
    public ResourceUtteranceReader() {
        this.leadingPath = "/";
    }

    /**
     * New reader for classloader-resources giving it a valid leading path. Note that a
     * fully qualified path is a concatenation of the leading path (set in the constructor),
     * the locale (given to the read-method) and the trailing path containing the actual file (can be overridden
     * by setting resourceLocation). You may end up with something like /my/leading/path/en-US/my/trailing/path/utterances.yml.
     * @param leadingPath leading path to the actual resource (YAML) file
     */
    public ResourceUtteranceReader(String leadingPath) {
        Validate.notBlank(leadingPath, "Leading path for utterance resource must not be blank. At least give it a '/'");
        if (!leadingPath.startsWith("/"))
            leadingPath += "/";
        if (!leadingPath.endsWith("/"))
            leadingPath = "/" + leadingPath;
        this.leadingPath = leadingPath;
    }

    /**
     * {@inheritDoc}
     */
    public String getResourceLocation() {
        return this.resourceLocation;
    }

    /**
     * {@inheritDoc}
     */
    public void setResourceLocation(final String resourceLocation) {
        Validate.notBlank(resourceLocation, "No resource location is set to read from.");
        Validate.notBlank(resourceLocation.replace("/", ""), "No resource location is set to read from.");
        Validate.isTrue(resourceLocation.endsWith(".yml"), "Resource location must end with .yml");
        this.resourceLocation = resourceLocation.startsWith("/") ? resourceLocation : "/" + resourceLocation;
    }

    /**
     * {@inheritDoc}
     */
    public ResourceUtteranceReader fromResourceLocation(final String resourceLocation) {
        setResourceLocation(resourceLocation);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream read(final String locale) {
        Validate.notNull(locale, "Local must not be blank.");

        final String resourcePath = leadingPath + locale + resourceLocation;

        Validate.notNull(ClassLoader.class.getResource(resourcePath), "Resource " + resourcePath + " does not exist in current context.");

        return ClassLoader.class.getResourceAsStream(resourcePath);
    }
}
