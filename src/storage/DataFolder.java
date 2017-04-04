package it.menzani.yiupp.storage;

import com.google.gson.Gson;
import it.menzani.yiupp.Yiupp;
import it.menzani.yiupp.reporter.UncaughtException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class DataFolder {

    private static final File ROOT_FOLDER = new File(System.getProperty("user.home"), Yiupp.DISPLAY_NAME.expand());

    public static final File RESOURCE_FOLDER = new File(ROOT_FOLDER, "Liste");
    private static final File SETTINGS_FOLDER = new File(ROOT_FOLDER, "settings");
    private static final File PREFERENCES_FILE = new File(SETTINGS_FOLDER, "preferences.json");
    private static final File SITE_HISTORY_FILE = new File(SETTINGS_FOLDER, "siteHistory.txt");
    private static final File LAYOUT_FILE = new File(SETTINGS_FOLDER, "layout.json");

    private static final Gson PARSER = new Gson();
    private static final DataFolder INSTANCE = new DataFolder();

    private final SiteHistory siteHistory;
    private Preferences preferences;
    private Layout layout;

    private DataFolder() {
        ROOT_FOLDER.mkdirs();
        SETTINGS_FOLDER.mkdirs();
        preferences = readJson(PREFERENCES_FILE, Preferences.class);
        if (preferences == null) {
            preferences = new Preferences();
        }

        List<String> temp = new ArrayList<>();
        try (Scanner scanner = new Scanner(SITE_HISTORY_FILE)) {
            while (scanner.hasNextLine()) {
                temp.add(scanner.nextLine());
            }
        } catch (FileNotFoundException ex) {
        }
        siteHistory = new SiteHistory(temp);

        layout = readJson(LAYOUT_FILE, Layout.class);
        if (layout == null) {
            layout = new Layout();
        }
    }

    public static DataFolder getInstance() {
        return INSTANCE;
    }

    private static <T> T readJson(File file, Class<T> clazz) {
        try {
            return PARSER.fromJson(new FileReader(file), clazz);
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    private static void writeJson(File file, Object object) {
        try (Writer writer = new FileWriter(file)) {
            PARSER.toJson(object, writer);
        } catch (IOException ex) {
            throw new UncaughtException(ex);
        }
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public SiteHistory getSiteHistory() {
        return siteHistory;
    }

    public Layout getLayout() {
        return layout;
    }

    public void saveAll() {
        savePreferences();
        saveLayout();
        saveSiteHistory();
    }

    public void savePreferences() {
        writeJson(PREFERENCES_FILE, preferences);
    }

    public void saveLayout() {
        writeJson(LAYOUT_FILE, layout);
    }

    public void saveSiteHistory() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SITE_HISTORY_FILE))) {
            for (String siteUrl : siteHistory.getHistory()) {
                writer.println(siteUrl);
            }
        } catch (IOException ex) {
            throw new UncaughtException(ex);
        }
    }

}
