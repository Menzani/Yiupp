package it.menzani.yiupp.storage;

import java.util.List;

public class SiteHistory {

    private final List<String> history;

    public SiteHistory(List<String> history) {
        this.history = history;
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }

    public String[] getHistory() {
        return history.toArray(new String[history.size()]);
    }

    public void select(String siteUrl) {
        if (history.contains(siteUrl)) {
            history.remove(siteUrl);
        }
        history.add(0, siteUrl);
    }

}
