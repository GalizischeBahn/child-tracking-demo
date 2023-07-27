package com.example.child_tracking.ui

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.child_tracking.R
import com.example.child_tracking.data.UsersDataStore
import com.example.child_tracking.util.DefaultLocationClient
import com.example.child_tracking.util.LocationService
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.time.AbstractLongTimeSource
import androidx.lifecycle.ViewModel
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.coroutines.runBlocking


class ParentActivity : AppCompatActivity() {


    private lateinit var navController: NavController
    private lateinit var usersDataStore: UsersDataStore
    private val viewModel: ChildTrackingViewModel by viewModels<ChildTrackingViewModel> {  ChildTrackingViewModel.Factory}
   private lateinit var textForView: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.parent_activity)

        val extras: Bundle? = intent.extras
        val parentemail = extras!!.getString("parentEmail")

        viewModel.email = parentemail!!
        viewModel.addParentEmailToDatabase(viewModel.email)
        viewModel.makeParentOnline(viewModel.email)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

    }

    override fun onStart() {
        viewModel.makeParentOnline(viewModel.email)
        super.onStart()
     
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.makeParentOffline(textForView)

    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}









