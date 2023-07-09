package com.example.firebasecrud

import android.app.Application
import com.google.firebase.FirebaseApp

class FireBaseCRUD:Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}