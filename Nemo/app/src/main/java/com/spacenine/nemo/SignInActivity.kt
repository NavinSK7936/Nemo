package com.spacenine.nemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.google.android.gms.auth.api.credentials.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.spacenine.nemo.next.NextActivity
import com.spacenine.nemo.user.currentFirebaseUser
import com.spacenine.nemo.util.hide
import kotlinx.android.synthetic.main.activity_sign_in.*

@GlideModule
class GlideNine : AppGlideModule()

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

        supportActionBar?.hide()

        FirebaseApp.initializeApp(applicationContext)

        if (currentFirebaseUser != null) {

            Glide.with(this)
                .load(currentFirebaseUser!!.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .dontAnimate()
                .into(userImageView)

            userName.text = currentFirebaseUser!!.displayName

            userLayout.setOnClickListener {
                startActivity(Intent(this, NextActivity::class.java))
                finish()
            }

        } else {
            orBox.text = "Register and Nemo finds you!!!"
            userLayout.hide()
        }

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
//                Toast.makeText(this, "firebaseAuthWithGoogleID:" + account.id, Toast.LENGTH_LONG).show()
                Log.d(G_TAG, "firebaseAuthWithGoogle:" + account.id)
                try {
                    Log.e("HERE is the fuck: ", "${account.idToken}")
                    firebaseAuthWithGoogle(account.idToken)
                } catch(e: Exception) {
//                    Toast.makeText(this, "exception:$e", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
//                Toast.makeText(this, "Google sign in failed- $e", Toast.LENGTH_LONG).show()
                Log.w(G_TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
//        Toast.makeText(this, "sCAME99999", Toast.LENGTH_SHORT).show()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        Toast.makeText(this, "sCAME", Toast.LENGTH_SHORT).show()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in currentFirebaseUser's information
//                    Toast.makeText(this, "signInWithCredential:success", Toast.LENGTH_SHORT).show()
                    Log.d(G_TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the currentFirebaseUser.
//                    Toast.makeText(this, "signInWithCredential:failure ${task.exception}", Toast.LENGTH_SHORT).show()
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
            startActivity(Intent(this, NextActivity::class.java))
            finish()
        }

}

// THIS WAS THE PROBLEM SO FAR
//E/AndroidRuntime: FATAL EXCEPTION: main
//Process: com.spacenine.nemo, PID: 31513
//java.lang.RuntimeException: Unable to instantiate activity ComponentInfo{com.spacenine.nemo/com.spacenine.nemo.SignInActivity}: java.lang.IllegalStateException: Default FirebaseApp is not initialized in this process com.spacenine.nemo. Make sure to call FirebaseApp.initializeApp(Context) first.
//at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3690)
//at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3950)
//at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:85)
//at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:135)
//at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:95)
//at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2377)
//at android.os.Handler.dispatchMessage(Handler.java:106)
//at android.os.Looper.loop(Looper.java:262)
//at android.app.ActivityThread.main(ActivityThread.java:8304)
//at java.lang.reflect.Method.invoke(Native Method)
//at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
//at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1049)
//Caused by: java.lang.IllegalStateException: Default FirebaseApp is not initialized in this process com.spacenine.nemo. Make sure to call FirebaseApp.initializeApp(Context) first.
//at com.google.firebase.FirebaseApp.getInstance(FirebaseApp.java:184)
//at com.google.firebase.auth.FirebaseAuth.getInstance(com.google.firebase:firebase-auth@@21.0.3:1)
//at com.spacenine.nemo.SignInActivity.<init>(SignInActivity.kt:27)
//at java.lang.Class.newInstance(Native Method)
//at android.app.AppComponentFactory.instantiateActivity(AppComponentFactory.java:95)
//at androidx.core.app.CoreComponentFactory.instantiateActivity(CoreComponentFactory.java:45)
//at android.app.Instrumentation.newActivity(Instrumentation.java:1260)
//at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3678)



//E/AndroidRuntime: FATAL EXCEPTION: main
//Process: com.spacenine.nemo, PID: 32354
//java.lang.RuntimeException: Unable to start activity ComponentInfo{com.spacenine.nemo/com.spacenine.nemo.SignInActivity}: android.view.InflateException: Binary XML file line #2 in com.spacenine.nemo:layout/activity_sign_in: Binary XML file line #2 in com.spacenine.nemo:layout/activity_sign_in: Error inflating class layout
//at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3782)
//at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3950)
//at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:85)
//at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:135)
//at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:95)
//at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2377)
//at android.os.Handler.dispatchMessage(Handler.java:106)
//at android.os.Looper.loop(Looper.java:262)
//at android.app.ActivityThread.main(ActivityThread.java:8304)
//at java.lang.reflect.Method.invoke(Native Method)
//at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
//at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1049)
//Caused by: android.view.InflateException: Binary XML file line #2 in com.spacenine.nemo:layout/activity_sign_in: Binary XML file line #2 in com.spacenine.nemo:layout/activity_sign_in: Error inflating class layout
//Caused by: android.view.InflateException: Binary XML file line #2 in com.spacenine.nemo:layout/activity_sign_in: Error inflating class layout
//Caused by: java.lang.ClassNotFoundException: android.view.layout
//at java.lang.Class.classForName(Native Method)
//at java.lang.Class.forName(Class.java:454)
//at android.view.LayoutInflater.createView(LayoutInflater.java:813)
//at android.view.LayoutInflater.createView(LayoutInflater.java:774)
//at android.view.LayoutInflater.onCreateView(LayoutInflater.java:911)
//at com.android.internal.policy.PhoneLayoutInflater.onCreateView(PhoneLayoutInflater.java:68)
//at android.view.LayoutInflater.onCreateView(LayoutInflater.java:928)
//at android.view.LayoutInflater.onCreateView(LayoutInflater.java:948)
//at android.view.LayoutInflater.createViewFromTag(LayoutInflater.java:1004)
//at android.view.LayoutInflater.createViewFromTag(LayoutInflater.java:959)
//at android.view.LayoutInflater.inflate(LayoutInflater.java:657)
//at android.view.LayoutInflater.inflate(LayoutInflater.java:532)
//at android.view.LayoutInflater.inflate(LayoutInflater.java:479)
//at androidx.appcompat.app.AppCompatDelegateImpl.setContentView(AppCompatDelegateImpl.java:699)
//at androidx.appcompat.app.AppCompatActivity.setContentView(AppCompatActivity.java:195)
//at com.spacenine.nemo.SignInActivity.onCreate(SignInActivity.kt:32)
//at android.app.Activity.performCreate(Activity.java:8183)
//at android.app.Activity.performCreate(Activity.java:8167)
//at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1316)
//at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3751)
//at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3950)
//at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:85)
//at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:135)
//at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:95)
//at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2377)
//at android.os.Handler.dispatchMessage(Handler.java:106)
//at android.os.Looper.loop(Looper.java:262)
//at android.app.ActivityThread.main(ActivityThread.java:8304)
//at java.lang.reflect.Method.invoke(Native Method)
//at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
//at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1049)
//E/AndroidRuntime: Caused by: java.lang.ClassNotFoundException: Didn't find class "android.view.layout" on path: DexPathList[[zip file "/data/app/~~kotxazHk7zJ8X2nWt6AgIg==/com.spacenine.nemo-o4HCRs1gbySSAnxnERLbvQ==/base.apk"],nativeLibraryDirectories=[/data/app/~~kotxazHk7zJ8X2nWt6AgIg==/com.spacenine.nemo-o4HCRs1gbySSAnxnERLbvQ==/lib/arm64, /system/lib64, /system/system_ext/lib64, /vendor/lib64, /odm/lib64]]
//at dalvik.system.BaseDexClassLoader.findClass(BaseDexClassLoader.java:207)
//at java.lang.ClassLoader.loadClass(ClassLoader.java:379)
//at java.lang.ClassLoader.loadClass(ClassLoader.java:312)
//... 31 more