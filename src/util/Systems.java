package it.menzani.yiupp.util;

public final class Systems {

    private static final System system;

    static {
        String osName = java.lang.System.getProperty("os.name").toLowerCase();
        String osArch = java.lang.System.getProperty("os.arch");
        String env = java.lang.System.getenv("PROCESSOR_ARCHITECTURE");
        String wow64Env = java.lang.System.getenv("PROCESSOR_ARCHITEW6432");
        SystemFamily family;
        SystemArchitecture architecture = null;

        if (osName.contains("win")) {
            family = SystemFamily.WINDOWS;
            if (env.endsWith("64") || wow64Env != null && wow64Env.endsWith("64")) {
                architecture = SystemArchitecture.BIT_64;
            } else {
                architecture = SystemArchitecture.BIT_32;
            }
        } else if (osName.contains("mac")) {
            family = SystemFamily.MAC;
        } else if (osName.contains("linux") || osName.contains("nix")) {
            family = SystemFamily.LINUX;
            if (osArch != null) {
                if (osArch.contains("ppc")) {
                    architecture = SystemArchitecture.PPC;
                } else if (osArch.contains("64")) {
                    architecture = SystemArchitecture.BIT_64;
                } else {
                    architecture = SystemArchitecture.BIT_32;
                }
            }
        } else if (osName.contains("solaris") || osName.contains("sun")) {
            family = SystemFamily.SOLARIS;
            if (osArch != null && osArch.contains("sparc")) {
                architecture = SystemArchitecture.SPARC;
            } else {
                architecture = SystemArchitecture.BIT_32;
            }
        } else {
            throw new UnknownSystemException(osName, osArch, env, wow64Env);
        }

        system = new System(family, architecture);
    }

    private Systems() {

    }

    public static System getSystem() {
        return system;
    }

}
