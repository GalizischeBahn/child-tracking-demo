package com.example.child_tracking.ui

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.child_tracking.R
import com.example.child_tracking.data.UsersDataStore
import com.example.child_tracking.util.LocationService
import com.example.child_tracking.util.formatEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {


    private lateinit var usersDataStore: UsersDataStore
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mail_layout)

        auth = FirebaseAuth.getInstance()
        usersDataStore = UsersDataStore(applicationContext)

        if (auth.currentUser != null) {
            val intent = Intent(applicationContext, ParentActivity::class.java).putExtra("parentEmail",
                auth.currentUser.email.formatEmail())
            startActivity(intent)

        }

//       val job =  lifecycleScope.launch {
//           withContext(Dispatchers.Main) {
//            usersDataStore.parentEmailPreferenceFlow.collect{
//                if (it?.isNotEmpty()?: false) {
//                    startActivity(Intent(this@MainActivity, ParentActivity::class.java)
//                        .putExtra("parentEmail", it))
//                }
//            }}
//
//        }

        val parentbutton = findViewById<Button>(R.id.btnParent)

        parentbutton.setOnClickListener {
            if (auth.currentUser == null) {
                val intent = Intent(applicationContext, ParentSignupActivity::class.java)
                startActivity(intent)


            } else {
                val intent = Intent(applicationContext, ParentActivity::class.java).putExtra("parentEmail",
                FirebaseAuth.getInstance().currentUser.email.formatEmail())
                startActivity(intent)
            }}

        val childButton = findViewById<Button>(R.id.btnChild)

        childButton.setOnClickListener {
                val intent = Intent(this, ChildActivity::class.java)
                startActivity(intent)

            }


        }


  }







