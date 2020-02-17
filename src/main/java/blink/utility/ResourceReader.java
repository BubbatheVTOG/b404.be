package blink.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidParameterException;

/**
 * This class will read resources located in the "resources" directory of the project.
 */
public class ResourceReader {

    // This is the relative location of the resources to the root of the project.
    // If this code is moved elsewhere in the project, this value will have to be changed.
    private static final String CONTEXT_ROOT = "../../";

    private InputStream inputStream;

    /**
     * Prevent this class from being instantiated without needed dependencies.
     */
    private ResourceReader() {}

    /**
     * Instantiates a new ResourceReader object which can be used to read the specified resource.
     * @param resourceLocation The location to the resource.
     * @throws InvalidParameterException Thrown if the resourceLocation contains illegal characters.
     */
    public ResourceReader(final String resourceLocation) throws InvalidParameterException {

        // It would be really bad if we introduced a directory traversal exploit to our code base.
        // This is a (poor) attempt at preventing that.
        if (resourceLocation.contains("..")) {
            throw new InvalidParameterException("Resource location contained illegal characters.");
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        this.inputStream = classLoader.getResourceAsStream(CONTEXT_ROOT+resourceLocation);

        // Need to ensure that the user of this object can't shoot themselves in the foot.
        if (this.inputStream == null) {
            throw new InvalidParameterException("Resource specified could not be obtained.");
        }
    }

    /**
     * Returns the resource as an InputStream. Null if the resource could not be found.
     * @return The resource as an InputStream. Null if the resource could not be found.
     */
    public InputStream getResourceAsInputStream() {
        return this.inputStream;
    }

    /**
     * Returns the resource as a String. Hopefully the resource can be represented as a String.
     * @return The designated resource as a String.
     * @throws IOException Thrown if the resource could not be read.
     */
    public String getResourceAsString() throws IOException {
        StringBuilder sb = new StringBuilder();
        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String buf;
                while ((buf = reader.readLine()) != null) {
                    sb.append(buf);
                }
            }
        }
        return sb.toString();
    }
}
