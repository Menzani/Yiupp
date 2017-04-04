package it.menzani.yiupp.util;

public enum SystemFamily {

    WINDOWS("win"),
    MAC("osx"),
    LINUX("linux"),
    SOLARIS("solaris");

    private final String friendlyName;

    SystemFamily(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public String toString() {
        return friendlyName;
    }

}
