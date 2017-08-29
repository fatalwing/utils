package com.townmc.utils.jackson.databind.cfg;

import com.townmc.utils.jackson.core.Version;
import com.townmc.utils.jackson.core.Versioned;
import com.townmc.utils.jackson.core.util.VersionUtil;

/**
 * Automatically generated from PackageVersion.java.in during
 * packageVersion-generate execution of maven-replacer-plugin in
 * pom.xml.
 */
public final class PackageVersion implements Versioned {
    public final static Version VERSION = VersionUtil.parseVersion(
        "2.9.0", "com.fasterxml.jackson.databind", "jackson-databind");

    @Override
    public Version version() {
        return VERSION;
    }
}
