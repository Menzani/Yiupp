package it.menzani.yiupp.launcher;

class VersionInformation {

    private Version current;

    Version getCurrent() {
        return current;
    }

    static class Version {

        private String buildNumber;
        private String description;

        String getBuildNumber() {
            return buildNumber;
        }

        String getDescription() {
            return description;
        }

    }

}
