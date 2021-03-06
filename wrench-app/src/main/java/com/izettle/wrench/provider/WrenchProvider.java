package com.izettle.wrench.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;

import com.izettle.wrench.BuildConfig;
import com.izettle.wrench.core.Bolt;
import com.izettle.wrench.core.WrenchProviderContract;
import com.izettle.wrench.database.WrenchApplication;
import com.izettle.wrench.database.WrenchApplicationDao;
import com.izettle.wrench.database.WrenchConfiguration;
import com.izettle.wrench.database.WrenchConfigurationDao;
import com.izettle.wrench.database.WrenchConfigurationValue;
import com.izettle.wrench.database.WrenchConfigurationValueDao;
import com.izettle.wrench.database.WrenchPredefinedConfigurationValue;
import com.izettle.wrench.database.WrenchPredefinedConfigurationValueDao;
import com.izettle.wrench.database.WrenchScope;
import com.izettle.wrench.database.WrenchScopeDao;
import com.izettle.wrench.preferences.WrenchPreferences;

import java.util.Date;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dagger.android.AndroidInjection;
import dagger.android.HasContentProviderInjector;

import static com.izettle.wrench.provider.WrenchApiVersion.API_1;
import static com.izettle.wrench.provider.WrenchApiVersion.API_INVALID;


public class WrenchProvider extends ContentProvider {

    private static final int CURRENT_CONFIGURATION_ID = 1;
    private static final int CURRENT_CONFIGURATION_KEY = 2;
    private static final int CURRENT_CONFIGURATIONS = 3;
    private static final int PREDEFINED_CONFIGURATION_VALUES = 5;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(WrenchProviderContract.WRENCH_AUTHORITY, "currentConfiguration/#", CURRENT_CONFIGURATION_ID);
        sUriMatcher.addURI(WrenchProviderContract.WRENCH_AUTHORITY, "currentConfiguration/*", CURRENT_CONFIGURATION_KEY);
        sUriMatcher.addURI(WrenchProviderContract.WRENCH_AUTHORITY, "currentConfiguration", CURRENT_CONFIGURATIONS);
        sUriMatcher.addURI(WrenchProviderContract.WRENCH_AUTHORITY, "predefinedConfigurationValue", PREDEFINED_CONFIGURATION_VALUES);
    }

    @Inject
    WrenchApplicationDao applicationDao;

    @Inject
    WrenchScopeDao scopeDao;

    @Inject
    WrenchConfigurationDao configurationDao;

    @Inject
    WrenchConfigurationValueDao configurationValueDao;

    @Inject
    WrenchPredefinedConfigurationValueDao predefinedConfigurationDao;

    @Inject
    IPackageManagerWrapper packageManagerWrapper;

    @Inject
    WrenchPreferences wrenchPreferences;

    public WrenchProvider() {
    }

    private static synchronized WrenchScope getDefaultScope(@Nullable Context context, WrenchScopeDao scopeDao, long applicationId) {
        if (context == null) {
            return null;
        }

        WrenchScope scope = scopeDao.getDefaultScope(applicationId);

        if (scope == null) {
            scope = new WrenchScope();
            scope.setApplicationId(applicationId);
            long id = scopeDao.insert(scope);
            scope.setId(id);
        }
        return scope;
    }

    private static synchronized WrenchScope getSelectedScope(@Nullable Context context, WrenchScopeDao scopeDao, long applicationId) {
        if (context == null) {
            return null;
        }

        WrenchScope scope = scopeDao.getSelectedScope(applicationId);

        if (scope == null) {
            WrenchScope defaultScope = new WrenchScope();
            defaultScope.setApplicationId(applicationId);
            defaultScope.setId(scopeDao.insert(defaultScope));

            WrenchScope customScope = new WrenchScope();
            customScope.setApplicationId(applicationId);
            customScope.setTimeStamp(new Date(defaultScope.getTimeStamp().getTime() + 1000));
            customScope.setName(WrenchScope.SCOPE_USER);
            customScope.setId(scopeDao.insert(customScope));

            scope = customScope;
        }
        return scope;
    }

    private static void assertValidApiVersion(WrenchPreferences wrenchPreferences, Uri uri) {
        switch (getApiVersion(uri)) {
            case API_1: {
                return;
            }
            case WrenchApiVersion.API_INVALID:
            default: {
                long l = 0;
                try {
                    l = Binder.clearCallingIdentity();
                    if (wrenchPreferences.getBoolean("Require valid wrench api version", false)) {
                        throw new IllegalArgumentException("This content provider requires you to provide a valid api-version in a queryParameter");
                    }
                } finally {
                    Binder.restoreCallingIdentity(l);
                }
            }
        }
    }

    @WrenchApiVersion
    private static int getApiVersion(Uri uri) {
        String queryParameter = uri.getQueryParameter(WrenchProviderContract.WRENCH_API_VERSION);
        if (queryParameter != null) {
            return Integer.valueOf(queryParameter);
        } else {
            return API_INVALID;
        }
    }

    @Nullable
    private synchronized WrenchApplication getCallingApplication(@Nullable Context context, WrenchApplicationDao applicationDao) {
        if (context == null) {
            return null;
        }

        WrenchApplication wrenchApplication = applicationDao.loadByPackageName(packageManagerWrapper.getCallingApplicationPackageName());

        if (wrenchApplication == null) {
            try {
                wrenchApplication = new WrenchApplication(0, packageManagerWrapper.getCallingApplicationPackageName(), packageManagerWrapper.getApplicationLabel());

                wrenchApplication.setId(applicationDao.insert(wrenchApplication));

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }

        return wrenchApplication;
    }

    @Override
    public boolean onCreate() {

        if (getContext().getApplicationContext() instanceof HasContentProviderInjector) {
            AndroidInjection.inject(this);
        }

        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        WrenchApplication callingApplication = getCallingApplication(getContext(), applicationDao);

        if (callingApplication == null) {
            return null;
        }

        if (!isWrenchApplication(callingApplication)) {
            assertValidApiVersion(wrenchPreferences, uri);
        }

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CURRENT_CONFIGURATION_ID: {
                WrenchScope scope = getSelectedScope(getContext(), scopeDao, callingApplication.id());
                cursor = configurationDao.getBolt(Long.valueOf(uri.getLastPathSegment()), scope.id());

                if (cursor.getCount() == 0) {
                    cursor.close();

                    WrenchScope defaultScope = getDefaultScope(getContext(), scopeDao, callingApplication.id());
                    cursor = configurationDao.getBolt(Long.valueOf(uri.getLastPathSegment()), defaultScope.id());
                }

                break;
            }
            case CURRENT_CONFIGURATION_KEY: {
                WrenchScope scope = getSelectedScope(getContext(), scopeDao, callingApplication.id());
                cursor = configurationDao.getBolt(uri.getLastPathSegment(), scope.id());

                if (cursor.getCount() == 0) {
                    cursor.close();

                    WrenchScope defaultScope = getDefaultScope(getContext(), scopeDao, callingApplication.id());
                    cursor = configurationDao.getBolt(uri.getLastPathSegment(), defaultScope.id());
                }

                break;
            }
            default: {
                throw new UnsupportedOperationException("Not yet implemented " + uri.toString());
            }
        }

        if (cursor != null && getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    private boolean isWrenchApplication(WrenchApplication callingApplication) {
        return callingApplication.packageName().equals(BuildConfig.APPLICATION_ID);
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        WrenchApplication callingApplication = getCallingApplication(getContext(), applicationDao);
        if (callingApplication == null) {
            return null;
        }

        if (!isWrenchApplication(callingApplication)) {
            assertValidApiVersion(wrenchPreferences, uri);
        }

        long insertId;
        switch (sUriMatcher.match(uri)) {
            case CURRENT_CONFIGURATIONS: {
                Bolt bolt = Bolt.fromContentValues(values);

                bolt = fixRCTypes(bolt);

                WrenchConfiguration wrenchConfiguration = configurationDao.getWrenchConfiguration(callingApplication.id(), bolt.getKey());

                if (wrenchConfiguration == null) {
                    wrenchConfiguration = new WrenchConfiguration(0, callingApplication.id(), bolt.getKey(), bolt.getType());

                    wrenchConfiguration.setId(configurationDao.insert(wrenchConfiguration));
                }

                WrenchScope defaultScope = getDefaultScope(getContext(), scopeDao, callingApplication.id());

                WrenchConfigurationValue wrenchConfigurationValue = new WrenchConfigurationValue(0, wrenchConfiguration.id(), bolt.getValue(), defaultScope.id());
                wrenchConfigurationValue.setConfigurationId(wrenchConfiguration.id());
                wrenchConfigurationValue.setValue(bolt.getValue());
                wrenchConfigurationValue.setScope(defaultScope.id());

                wrenchConfigurationValue.setId(configurationValueDao.insert(wrenchConfigurationValue));

                insertId = wrenchConfiguration.id();
                break;
            }
            case PREDEFINED_CONFIGURATION_VALUES: {
                WrenchPredefinedConfigurationValue fullConfig = WrenchPredefinedConfigurationValue.fromContentValues(values);
                insertId = predefinedConfigurationDao.insert(fullConfig);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Not yet implemented " + uri);
            }
        }

        getContext().getContentResolver().notifyChange(Uri.withAppendedPath(uri, String.valueOf(insertId)), null, false);

        return ContentUris.withAppendedId(uri, insertId);
    }

    private Bolt fixRCTypes(Bolt bolt) {
        switch (bolt.getType()) {
            case "java.lang.String":
                return bolt.copy(bolt.getId(), Bolt.TYPE.STRING, bolt.getKey(), bolt.getValue());
            case "java.lang.Integer":
                return bolt.copy(bolt.getId(), Bolt.TYPE.INTEGER, bolt.getKey(), bolt.getValue());
            case "java.lang.Enum":
                return bolt.copy(bolt.getId(), Bolt.TYPE.ENUM, bolt.getKey(), bolt.getValue());
            case "java.lang.Boolean":
                return bolt.copy(bolt.getId(), Bolt.TYPE.BOOLEAN, bolt.getKey(), bolt.getValue());
            default: {
                return bolt;
            }

        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        WrenchApplication callingApplication = getCallingApplication(getContext(), applicationDao);
        if (callingApplication == null) {
            return 0;
        }

        if (!isWrenchApplication(callingApplication)) {
            assertValidApiVersion(wrenchPreferences, uri);
        }

        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        WrenchApplication callingApplication = getCallingApplication(getContext(), applicationDao);
        if (callingApplication == null) {
            return 0;
        }

        if (!isWrenchApplication(callingApplication)) {
            assertValidApiVersion(wrenchPreferences, uri);
        }

        int updatedRows;
        switch (sUriMatcher.match(uri)) {
            case CURRENT_CONFIGURATION_ID: {
                Bolt bolt = Bolt.fromContentValues(values);
                WrenchScope scope = getSelectedScope(getContext(), scopeDao, callingApplication.id());
                updatedRows = configurationValueDao.updateConfigurationValue(Long.parseLong(uri.getLastPathSegment()), scope.id(), bolt.getValue());
                if (updatedRows == 0) {
                    WrenchConfigurationValue wrenchConfigurationValue = new WrenchConfigurationValue(0, Long.parseLong(uri.getLastPathSegment()), bolt.getValue(), scope.id());
                    configurationValueDao.insert(wrenchConfigurationValue);
                }

                break;
            }
            default: {
                throw new UnsupportedOperationException("Not yet implemented " + uri);
            }
        }

        if (updatedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null, false);
        }

        return updatedRows;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        WrenchApplication callingApplication = getCallingApplication(getContext(), applicationDao);
        if (callingApplication == null) {
            return 0;
        }

        if (!isWrenchApplication(callingApplication)) {
            assertValidApiVersion(wrenchPreferences, uri);
        }

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(@NonNull Uri uri) {
        WrenchApplication callingApplication = getCallingApplication(getContext(), applicationDao);
        if (callingApplication == null) {
            return null;
        }

        if (!isWrenchApplication(callingApplication)) {
            assertValidApiVersion(wrenchPreferences, uri);
        }

        switch (sUriMatcher.match(uri)) {
            case CURRENT_CONFIGURATIONS: {
                return "vnd.android.cursor.dir/vnd." + BuildConfig.APPLICATION_ID + ".currentConfiguration";
            }
            case CURRENT_CONFIGURATION_ID: {
                return "vnd.android.cursor.item/vnd." + BuildConfig.APPLICATION_ID + ".currentConfiguration";
            }
            case CURRENT_CONFIGURATION_KEY: {
                return "vnd.android.cursor.dir/vnd." + BuildConfig.APPLICATION_ID + ".currentConfiguration";
            }
            case PREDEFINED_CONFIGURATION_VALUES: {
                return "vnd.android.cursor.dir/vnd." + BuildConfig.APPLICATION_ID + ".predefinedConfigurationValue";
            }
            default: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }
    }

}
