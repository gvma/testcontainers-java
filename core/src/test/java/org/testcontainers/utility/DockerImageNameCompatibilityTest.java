package org.testcontainers.utility;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.StringContains.containsString;
import static org.rnorth.visibleassertions.VisibleAssertions.assertFalse;
import static org.rnorth.visibleassertions.VisibleAssertions.assertTrue;


public class DockerImageNameCompatibilityTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testPlainImage() {
        DockerImageName subject = DockerImageName.parse("foo");

        assertFalse("missing compatibility claim", subject.isCompatibleWith(DockerImageName.parse("bar")));
    }

    @Test
    public void testImageWithAutomaticCompatibility() {
        DockerImageName subject = DockerImageName.parse("foo:1.2.3");

        assertTrue("correct compatibility claim", subject.isCompatibleWith(DockerImageName.parse("foo")));
    }

    @Test
    public void testImageWithAutomaticCompatibilityForFullPath() {
        DockerImageName subject = DockerImageName.parse("repo/foo:1.2.3");

        assertTrue("correct compatibility claim", subject.isCompatibleWith(DockerImageName.parse("repo/foo")));
    }

    @Test
    public void testImageWithClaimedCompatibility() {
        DockerImageName subject = DockerImageName.parse("foo").asCompatibleSubstituteFor("bar");

        assertTrue("correct compatibility claim", subject.isCompatibleWith(DockerImageName.parse("bar")));
        assertFalse("compatibility claim against wrong name", subject.isCompatibleWith(DockerImageName.parse("fizz")));
    }

    @Test
    public void testImageWithClaimedCompatibilityAndVersion() {
        DockerImageName subject = DockerImageName.parse("foo:1.2.3").asCompatibleSubstituteFor("bar");

        assertTrue("correct compatibility claim", subject.isCompatibleWith(DockerImageName.parse("bar")));
    }

    @Test
    public void testImageWithClaimedCompatibilityForFullPath() {
        DockerImageName subject = DockerImageName.parse("foo").asCompatibleSubstituteFor("registry/repo/bar");

        assertTrue("correct compatibility claim", subject.isCompatibleWith(DockerImageName.parse("registry/repo/bar")));
        assertFalse("missing registry part", subject.isCompatibleWith(DockerImageName.parse("repo/bar")));
        assertFalse("missing all non-name parts", subject.isCompatibleWith(DockerImageName.parse("bar")));
    }

    @Test
    public void testImageWithClaimedCompatibilityForVersion() {
        DockerImageName subject = DockerImageName.parse("foo").asCompatibleSubstituteFor("bar:1.2.3");

        assertTrue("compatible with any version", subject.isCompatibleWith(DockerImageName.parse("bar")));
        assertTrue("compatible with same version", subject.isCompatibleWith(DockerImageName.parse("bar:1.2.3")));
        assertFalse("not compatible with different version", subject.isCompatibleWith(DockerImageName.parse("bar:0.0.1")));
        assertFalse("not compatible with different version", subject.isCompatibleWith(DockerImageName.parse("bar:2.0.0")));
        assertFalse("not compatible with different version", subject.isCompatibleWith(DockerImageName.parse("bar:1.2.4")));
    }

    @Test
    public void testCheckMethodAcceptsCompatible() {
        DockerImageName subject = DockerImageName.parse("foo").asCompatibleSubstituteFor("bar");
        subject.checkCompatibleWith(DockerImageName.parse("bar"));
    }

    @Test
    public void testCheckMethodRejectsIncompatible() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(containsString("Failed to verify that image 'foo' is a compatible substitute for 'bar'"));

        DockerImageName subject = DockerImageName.parse("foo");
        subject.checkCompatibleWith(DockerImageName.parse("bar"));
    }
}
