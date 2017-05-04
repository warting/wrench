package com.izettle.localconfiguration;

import android.net.Uri;

public class ConfigProviderHelper {
    public static final String AUTHORITY = BuildConfig.AUTHORITY;

    protected static final Uri applicationUri = Uri.parse("content://" + AUTHORITY + "/application");
    protected static final Uri configurationUri = Uri.parse("content://" + AUTHORITY + "/configuration");
    protected static final Uri configurationValueUri = Uri.parse("content://" + AUTHORITY + "/configurationValue");

    public static Uri applicationUri() {
        return applicationUri;
    }

    public static Uri configurationUri() {
        return configurationUri;
    }

    public static Uri configurationValueUri() {
        return configurationValueUri;
    }


}
