package com.example.child_tracking.ui

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.Intent
import android.os.Build
import android.widget.EditText
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.child_tracking.ApplicationMain
import com.example.child_tracking.R
import com.example.child_tracking.data.FirebaseRepository
import com.example.child_tracking.data.UsersDataStore
import com.example.child_tracking.util.LocationService
import com.example.child_tracking.util.formatEmail
import kotlinx.coroutines.launch


class ChildActivity : AppCompatActivity() {

    private lateinit var usersDataStore: UsersDataStore
    private val viewModel: ChildTrackingViewModel by viewModels<ChildTrackingViewModel> {  ChildTrackingViewModel.Factory}


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestLocationPermissions()



        val button = findViewById<Button>(R.id.submitButton)
        val childName = findViewById<EditText>(R.id.nameEditText)
        val parentEmail = findViewById<EditText>(R.id.parentEmailEditText)
        val intent = Intent(this, LocationService::class.java)


        button.setOnClickListener {

            lifecycleScope.launch {

                usersDataStore = UsersDataStore(applicationContext)
                usersDataStore.saveEmailToPreferencesStore(parentEmail.text.toString().formatEmail(), applicationContext)
                usersDataStore.saveChildNameToPreferencesStore(childName.text.toString(), applicationContext)

                viewModel.registerNewChild(parentEmail.text.toString().formatEmail(), childName.text.toString())


                startForegroundService(Intent(applicationContext, LocationService::class.java))

            }



        }}



    private fun hasCoarseLocationPermission() =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun hasFineLocationPermission() =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    private fun hasAudioRecordingPermisson() =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)


        if (!hasCoarseLocationPermission()) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        }
        else {
            val toastMessageSuccess  = "ranted"
            Toast.makeText(this, toastMessageSuccess , Toast.LENGTH_SHORT).show()}

        if (!hasFineLocationPermission()) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (!hasAudioRecordingPermisson()) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }


        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 0)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {


                if (grantResults.isNotEmpty() && requestCode == 0) {
                    for (i in grantResults.indices) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.d("granted", "${permissions[i]} has been granted")
                            val toastMessageSuccess  = "${permissions[i]} has been granted"
                            Toast.makeText(this, toastMessageSuccess , Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d("granted", "${permissions[i]} has been failed")
                            val toastMessageFail  = "${permissions[i]} has been failed"
                            Toast.makeText(this, toastMessageFail , Toast.LENGTH_SHORT).show()
                        }
                    }
                }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }}







