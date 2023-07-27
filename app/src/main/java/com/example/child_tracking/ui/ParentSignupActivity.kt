package com.example.child_tracking.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.child_tracking.ApplicationMain
import com.example.child_tracking.R
import com.example.child_tracking.data.UsersDataStore
import com.example.child_tracking.util.formatEmail
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
//import androidx.activity.viewModels

class ParentSignupActivity : AppCompatActivity()

{
  private lateinit var usersDataStore: UsersDataStore
    private lateinit var auth: FirebaseAuth


    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_signup)
        auth = FirebaseAuth.getInstance()

    }

    public override fun onStart() {

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setLogo(R.mipmap.ic_launcher)
            .setAvailableProviders(listOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
            ))
            .build()

        signInLauncher.launch(signInIntent)
        super.onStart()


    }
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            lifecycleScope.launch(){
                usersDataStore = UsersDataStore(applicationContext)
               usersDataStore.saveEmailToPreferencesStore(user.email.formatEmail(), applicationContext)

            }
            startActivity(Intent(this, ParentActivity::class.java).putExtra("parentEmail", user.email.formatEmail( )))


        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }
}