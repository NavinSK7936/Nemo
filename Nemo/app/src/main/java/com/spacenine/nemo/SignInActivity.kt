package com.spacenine.nemo

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class SignInActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 9009
        const val G_TAG = "Google Sign In -> "
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        FirebaseApp.initializeApp(applicationContext)

        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        Toast.makeText(this, "${FirebaseAuth.getInstance().currentUser?.displayName}", Toast.LENGTH_SHORT).show()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // NOTE: Got it from "client": "oauth_client": "client_id"
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<SignInButton>(R.id.googleSignInButton).setOnClickListener {
            googleSignIn()
        }


    }

    private fun googleSignIn() =
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Toast.makeText(this, "firebaseAuthWithGoogleID:" + account.id, Toast.LENGTH_LONG).show()
                Log.d(G_TAG, "firebaseAuthWithGoogle:" + account.id)
                try {
                    Log.e("HERE is the fuck: ", "${account.idToken}")
                    firebaseAuthWithGoogle(account.idToken)
                } catch(e: Exception) {
                    Toast.makeText(this, "exception:$e", Toast.LENGTH_SHORT).show()

                }
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google sign in failed- $e", Toast.LENGTH_LONG).show()
                Log.w(G_TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        Toast.makeText(this, "sCAME99999", Toast.LENGTH_SHORT).show()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        Toast.makeText(this, "sCAME", Toast.LENGTH_SHORT).show()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "signInWithCredential:success", Toast.LENGTH_SHORT).show()
                    Log.d(G_TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "signInWithCredential:failure ${task.exception}", Toast.LENGTH_SHORT).show()
                    Log.w(G_TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }

    }

    private fun updateUI(user0: FirebaseUser?): Any =

        if (user0 == null)
            Log.e("NULL USER", "!!")
        else {
            Log.e("USER IS: ", "${user0.displayName}-${user0.uid}")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

}