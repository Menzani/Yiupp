package it.menzani.yiupp.util;

public final class System {

    private final SystemFamily family;
    private final SystemArchitecture architecture;

    System(SystemFamily family, SystemArchitecture architecture) {
        this.family = family;
        this.architecture = architecture;
    }

    public SystemFamily getFamily() {
        return family;
    }

    public SystemArchitecture getArchitecture() {
        return architecture;
    }

    @Override
    public String toString() {
        return family + "_" + architecture;
    }

}
