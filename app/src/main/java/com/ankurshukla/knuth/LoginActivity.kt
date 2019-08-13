package com.ankurshukla.knuth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

private const val RC_SIGN_IN = 123

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mGoogleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()
        sign_in_button.setOnClickListener {
            signIn()
        }

    }

    override fun onStart() {
        super.onStart()
        if(mAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java).putExtra("firebase_auth", true))
        } else {
            updateUI(null)
        }
    }

    private fun signIn() {
        activity_login_progress_bar.visibility = View.VISIBLE
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("FirebaseLogin", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d("FirebaseLogin", "firebaseAuthWithGoogle: " + account.id)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    activity_login_progress_bar.visibility = View.INVISIBLE
                    Log.d("FirebaseLogin", "sign in successful")
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    activity_login_progress_bar.visibility = View.INVISIBLE
                    Log.d("FirebaseLogin", "sign in failed")
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null) {
            Toast.makeText(this, "Welcome " + user.displayName, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java).putExtra("firebase_auth", true))
        }
    }


}
