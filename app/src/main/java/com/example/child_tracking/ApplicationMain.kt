package com.example.child_tracking

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.child_tracking.data.FirebaseRepository
import com.example.child_tracking.ui.ChildTrackingViewModel

class ApplicationMain: Application() {


    companion object {
         var FIREBASE_REPOSITORY_INSTANCE: FirebaseRepository? = null
        fun getFirebaseRepo(): FirebaseRepository = FIREBASE_REPOSITORY_INSTANCE ?: synchronized(this){
            FIREBASE_REPOSITORY_INSTANCE ?: FirebaseRepository()
                .also { FIREBASE_REPOSITORY_INSTANCE = it }
        }

    }

    val firebaseRepo: FirebaseRepository by lazy { getFirebaseRepo() }




}