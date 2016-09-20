package io.klerch.alexa.tellask.schema;

import java.io.InputStream;

/**
 * The utterance reader is an interface for implementations to encapsulate
 * logic of reading a set of utterances from a source (like a file). As this
 * readers are leveraged by the YamlReader the utterance collection is expected
 * to be in YAML format. Note that a fully qualified path is a concatenation
 * of the leading path (set in the constructor), the locale (given to the read-method)
 * and the trailing path containing the actual file (can be overridden
 * by setting resourceLocation). You may end up with something like /my/leading/path/en-US/my/trailing/path/utterances.yml.
 */
public interface UtteranceReader {
    /**
     * Gets the trailing portion of the fully qualified path to the YAML file which
     * is read out for utterances. Note that a fully qualified path is a concatenation
     * of the leading path (set in the constructor), the locale (given to the read-method)
     * and the trailing path containing the actual file (can be overridden
     * by setting resourceLocation). You may end up with something like /my/leading/path/en-US/my/trailing/path/utterances.yml
     * @return reference to a resource (like a filename) which is read out.
     */
    String getResourceLocation();

    /**
     * Sets the trailing portion of the fully qualified path to the YAML file which
     * is read out for utterances. Note that a fully qualified path is a concatenation
     * of the leading path (set in the constructor), the locale (given to the read-method)
     * and the trailing path containing the actual file. So be sure you give this method
     * a valid path ending with ".yml"
     * @param resourceLocation trailing portion of the fully qualified path to the YAML file
     */
    void setResourceLocation(final String resourceLocation);

    /**
     * yet another setter for the resource location but this one is
     * for fluent method concatenation. Note that a fully qualified path is a concatenation
     * of the leading path (set in the constructor), the locale (given to the read-method)
     * and the trailing path containing the actual file (can be overridden
     * by setting resourceLocation). You may end up with something like /my/leading/path/en-US/my/trailing/path/utterances.yml.
     * So be sure you give this method a valid path ending with ".yml"
     * @param resourceLocation trailing portion of the fully qualified path to the YAML file
     * @return the reader object
     */
    UtteranceReader fromResourceLocation(final String resourceLocation);

    /**
     * Reads from the source referenced by the resourceLocation. Note that a fully qualified path
     * is a concatenation of the leading path (set in the constructor), the locale (given to the read-method)
     * and the trailing path containing the actual file (can be overridden
     * by setting resourceLocation). You may end up with something like /my/leading/path/en-US/my/trailing/path/utterances.yml.
     * So be sure you give this method valid locale (or blank string if not desired)
     * @param locale the locale from the speechlet request
     * @return an input stream containing the set of utterances in YAML format
     */
    InputStream read(final String locale);
}
