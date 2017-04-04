package it.menzani.yiupp.util;

public class UnknownSystemException extends RuntimeException {

    private final String osName;
    private final String osArch;
    private final String env;
    private final String wow64Env;

    public UnknownSystemException(String osName, String osArch, String env, String wow64Env) {
        super("Could not retrieve full OS information. Available: ");
        this.osName = osName;
        this.osArch = osArch;
        this.env = env;
        this.wow64Env = wow64Env;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "osName=" + osName + ", " + "osArch=" + osArch + ", " + "env=" + env + ", " +
                "wow64Env=" + wow64Env;
    }

}
