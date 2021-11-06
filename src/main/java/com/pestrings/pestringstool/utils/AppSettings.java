package com.pestrings.pestringstool.utils;
import com.pestrings.pestringstool.Application;

import java.util.prefs.Preferences;

public class AppSettings {

    static private final Preferences prefs = Preferences.userNodeForPackage(Application.class);

    static public String version = "v0.2.1";
    static public String translateSource = "en";
    static public String translateTarget = "ru";
    static public String aimToken = "";
    static public String folderId = "";

    static public void save() {
        prefs.put("translateSource", translateSource);
        prefs.put("translateTarget", translateTarget);
        prefs.put("aimToken", aimToken);
        prefs.put("folderId", folderId);
    }

    static public void restore() {
        translateSource = prefs.get("translateSource", "en");
        translateTarget = prefs.get("translateTarget", "ru");
        aimToken = prefs.get("aimToken", "");
        folderId = prefs.get("folderId", "");
    }

}
