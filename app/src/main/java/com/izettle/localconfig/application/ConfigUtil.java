package com.izettle.localconfig.application;


import android.content.ContentResolver;
import android.database.Cursor;

import com.izettle.localconfig.application.library.Application;
import com.izettle.localconfig.application.library.ApplicationCursorParser;
import com.izettle.localconfig.application.library.ConfigurationFull;
import com.izettle.localconfig.application.library.ConfigurationFullCursorParser;
import com.izettle.localconfiguration.ConfigProviderHelper;
import com.izettle.localconfiguration.Configuration;
import com.izettle.localconfiguration.ConfigurationValue;
import com.izettle.localconfiguration.util.ConfigurationCursorParser;
import com.izettle.localconfiguration.util.ConfigurationValueCursorParser;

import java.util.ArrayList;

class ConfigUtil {
    static void deleteApplication(ContentResolver contentResolver, Application application) {
        ArrayList<Configuration> configurations = getConfigurationsForApplication(contentResolver, application);
        for (Configuration configuration : configurations) {
            deleteConfiguration(contentResolver, configuration);
        }

        contentResolver.delete(ConfigProviderHelper.applicationUri(),
                ApplicationCursorParser.Columns._ID + " = ?",
                new String[]{String.valueOf(application._id)});
    }

    private static ArrayList<Configuration> getConfigurationsForApplication(ContentResolver contentResolver, Application application) {
        ArrayList<Configuration> configurations = new ArrayList<>();
        Cursor query = null;
        try {

            query = contentResolver.query(ConfigProviderHelper.configurationUri(),
                    ConfigurationFullCursorParser.PROJECTION,
                    ConfigurationFullCursorParser.Columns.APPLICATION_ID + " = ?",
                    new String[]{String.valueOf(application._id)}, null);

            ConfigurationFullCursorParser cursorParser = new ConfigurationFullCursorParser();
            while (query != null && query.moveToNext()) {
                configurations.add(cursorParser.populateFromCursor(new ConfigurationFull(), query));
            }
        } finally {
            if (query != null && !query.isClosed()) {
                query.close();
            }
        }
        return configurations;
    }

    private static void deleteConfiguration(ContentResolver contentResolver, Configuration configuration) {
        ArrayList<ConfigurationValue> configurationValues = getConfigurationsValuesForConfiguration(contentResolver, configuration);
        for (ConfigurationValue configurationValue : configurationValues) {
            deleteConfigurationValue(contentResolver, configurationValue);
        }

        contentResolver.delete(ConfigProviderHelper.configurationUri(),
                ConfigurationCursorParser.Columns._ID + " = ?",
                new String[]{String.valueOf(configuration._id)});
    }

    private static void deleteConfigurationValue(ContentResolver contentResolver, ConfigurationValue configurationValue) {
        contentResolver.delete(ConfigProviderHelper.configurationValueUri(),
                ConfigurationValueCursorParser.Columns._ID + " = ?",
                new String[]{String.valueOf(configurationValue._id)});
    }

    private static ArrayList<ConfigurationValue> getConfigurationsValuesForConfiguration(ContentResolver contentResolver, Configuration configuration) {
        ArrayList<ConfigurationValue> configurations = new ArrayList<>();
        Cursor query = null;
        try {

            query = contentResolver.query(ConfigProviderHelper.configurationValueUri(),
                    ConfigurationValueCursorParser.PROJECTION,
                    ConfigurationValueCursorParser.Columns.CONFIGURATION_ID + " = ?",
                    new String[]{String.valueOf(configuration._id)}, null);

            ConfigurationValueCursorParser cursorParser = new ConfigurationValueCursorParser();
            while (query != null && query.moveToNext()) {
                configurations.add(cursorParser.populateFromCursor(new ConfigurationValue(), query));
            }
        } finally {
            if (query != null && !query.isClosed()) {
                query.close();
            }
        }
        return configurations;
    }
}
