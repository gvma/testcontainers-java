package org.testcontainers.containers;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class MockServerContainer extends GenericContainer<MockServerContainer> {

    private static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName.parse("jamesdbloom/mockserver");
    private static final String DEFAULT_VERSION = "5.5.4";

    @Deprecated
    public static final String VERSION = DEFAULT_VERSION;

    public static final int PORT = 1080;

    /**
     * @deprecated use {@link MockServerContainer(DockerImageName)} instead
     */
    @Deprecated
    public MockServerContainer() {
        this(DOCKER_IMAGE_NAME.withTag("mockserver-" + DEFAULT_VERSION));
    }

    /**
     * @deprecated use {@link MockServerContainer(DockerImageName)} instead
     */
    @Deprecated
    public MockServerContainer(String version) {
        this(DOCKER_IMAGE_NAME.withTag("mockserver-" + version));
    }

    public MockServerContainer(DockerImageName dockerImageName) {
        super(dockerImageName);

        dockerImageName.checkCompatibleWith(DOCKER_IMAGE_NAME);

        withCommand("-logLevel INFO -serverPort " + PORT);
        addExposedPorts(PORT);
    }

    public String getEndpoint() {
        return String.format("http://%s:%d", getHost(), getMappedPort(PORT));
    }

    public Integer getServerPort() {
        return getMappedPort(PORT);
    }
}
