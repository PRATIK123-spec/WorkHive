package com.example.workhive

import android.app.Application
import com.google.firebase.FirebaseApp

class WorkHiveApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}
