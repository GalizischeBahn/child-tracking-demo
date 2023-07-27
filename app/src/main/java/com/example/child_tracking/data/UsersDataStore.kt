package com.example.child_tracking.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val USER_DATA_NAME = "user_data"

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(
    name = USER_DATA_NAME
)

class UsersDataStore(context: Context) {

    private val PARENT_EMAIL = stringPreferencesKey("PARENT_EMAIL")
    private val CHILD_NAME = stringPreferencesKey("CHILD_NAME")

    suspend fun saveEmailToPreferencesStore(parentEmail: String, context: Context) {
    context.dataStore.edit { preferences ->
    preferences[PARENT_EMAIL] = parentEmail
 }


}
    suspend fun saveChildNameToPreferencesStore(childName: String, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[CHILD_NAME] = childName
        }}

        val childNamePreferenceFlow: Flow<String?> =
            context.dataStore.data.catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }

            }
                .map { preferences -> preferences[CHILD_NAME] ?: "null" }


    val parentEmailPreferenceFlow: Flow<String?> =
        context.dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }

        }
            .map { preferences ->  preferences[PARENT_EMAIL] ?: "null" }


}




