package com.example.todolist.data

import android.content.Context
import android.preference.PreferenceDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class FilterPreferences(val sortBy: SortBy ,val hideCompleted :Boolean )
enum class SortBy { Name, Date }
@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext private val context:Context) {
    private val Context.dataStore:DataStore<Preferences> by preferencesDataStore(name="Tasks")
    val dataStorePref = context.dataStore


    val preferenceFlow = dataStorePref.data.map { preferences ->
        val sortBy = SortBy.valueOf(preferences[PreferenceKeys.SORT_ORDER] ?: SortBy.Date.name )
        val hideCompleted = preferences[PreferenceKeys.HIDE_COMPLETED] ?: false
        FilterPreferences(sortBy,hideCompleted)
    }

    suspend fun setSortOrder(sortBy: SortBy){
        dataStorePref.edit {preferences ->
            preferences[PreferenceKeys.SORT_ORDER] = sortBy.name
        }
    }

    suspend fun setHideCompleted(hideCompleted: Boolean){
        dataStorePref.edit {preferences ->
            preferences[PreferenceKeys.HIDE_COMPLETED] = hideCompleted
        }
    }

    private object PreferenceKeys{
         val SORT_ORDER = stringPreferencesKey("sort_order")
         val HIDE_COMPLETED = booleanPreferencesKey("hide_completed")
    }


}