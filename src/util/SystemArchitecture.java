package it.menzani.yiupp.util;

public enum SystemArchitecture {

    BIT_32("32"),
    BIT_64("64"),
    PPC("pcc"),
    SPARC("sparc");

    private final String friendlyName;

    SystemArchitecture(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public String toString() {
        return friendlyName;
    }

}
