package com.izettle.wrench.database;


import android.database.Cursor;

import com.izettle.wrench.database.tables.ConfigurationTable;
import com.izettle.wrench.database.tables.ConfigurationValueTable;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface WrenchConfigurationDao {

    @Query("SELECT configuration.id, " +
            " configuration.configurationKey, " +
            " configuration.configurationType," +
            " configurationValue.value" +
            " FROM " + ConfigurationTable.TABLE_NAME +
            " INNER JOIN " + ConfigurationValueTable.TABLE_NAME + " ON configuration.id = configurationValue.configurationId " +
            " WHERE configuration.id = (:configurationId) AND configurationValue.scope = (:scopeId)")
    Cursor getBolt(long configurationId, long scopeId);

    @Query("SELECT configuration.id, " +
            " configuration.configurationKey, " +
            " configuration.configurationType," +
            " configurationValue.value" +
            " FROM " + ConfigurationTable.TABLE_NAME +
            " INNER JOIN " + ConfigurationValueTable.TABLE_NAME + " ON configuration.id = configurationValue.configurationId " +
            " WHERE configuration.configurationKey = (:configurationKey) AND configurationValue.scope = (:scopeId)")
    Cursor getBolt(String configurationKey, long scopeId);

    @Query("SELECT * " +
            " FROM " + ConfigurationTable.TABLE_NAME +
            " WHERE configuration.applicationId = (:applicationId) AND configuration.configurationKey = (:configurationKey)")
    WrenchConfiguration getWrenchConfiguration(long applicationId, String configurationKey);

    @Query("SELECT * FROM configuration WHERE id = :configurationId")
    LiveData<WrenchConfiguration> getConfiguration(long configurationId);

    @Transaction
    @Query("SELECT id, applicationId, configurationKey, configurationType FROM configuration WHERE applicationId = :applicationId ORDER BY lastUse DESC")
    LiveData<List<WrenchConfigurationWithValues>> getApplicationConfigurations(long applicationId);

    @Transaction
    @Query("SELECT id, applicationId, configurationKey, configurationType FROM configuration WHERE applicationId = :applicationId AND configurationKey LIKE :query ORDER BY lastUse DESC")
    LiveData<List<WrenchConfigurationWithValues>> getApplicationConfigurations(long applicationId, String query);

    @Insert
    long insert(WrenchConfiguration wrenchConfiguration);

    @Query("UPDATE configuration set lastUse=:date WHERE id= :configurationId")
    void touch(long configurationId, Date date);
}
