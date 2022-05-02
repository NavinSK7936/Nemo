package com.spacenine.nemo.next

//import com.spacenine.nemo.bglocation.GeneralService
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsOptions
import com.google.android.gms.auth.api.credentials.HintRequest
import com.spacenine.dora.view.CellLocationActivity
import com.spacenine.nemo.MainActivity
import com.spacenine.nemo.R
import com.spacenine.nemo.gravity.GravityActivity
import com.spacenine.nemo.magnet.MagnetActivity
import com.spacenine.nemo.user.User
import com.spacenine.nemo.user.currentFirebaseUser
import com.spacenine.nemo.user.currentUser
import com.spacenine.nemo.util.getPhoneNumberInSharedPreferences
import com.spacenine.nemo.util.updatePhoneNumberInSharedPreferences
import kotlinx.android.synthetic.main.activity_next.*

class NextActivity : AppCompatActivity() {

    companion object {
        private const val CREDENTIAL_PICKER_REQUEST = 2
        private const val IGNORE_BATTERY_OPTIMIZATION_REQUEST = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)

//        checkPermissions()

        currentUser = User(currentFirebaseUser?.displayName)

        Glide.with(this)
            .load(currentFirebaseUser!!.photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .dontAnimate()
            .into(userImageView)

        userText.text = currentFirebaseUser!!.displayName

        phoneNumberEditButton.setOnClickListener {
            phoneSelection()
        }

        contactsLabel.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        accelerometerIcon.setOnClickListener {
            startActivity(Intent(this, GravityActivity::class.java))
        }

        magnetometerIcon.setOnClickListener {
            startActivity(Intent(this, MagnetActivity::class.java))
        }

        myLocationIcon.setOnClickListener {
            startActivity(Intent(this, CellLocationActivity::class.java))
        }

        val phoneNo = getPhoneNumberInSharedPreferences()

        if (phoneNo == null)
            phoneSelection()
        else
            phoneNumber.text = phoneNo

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            CREDENTIAL_PICKER_REQUEST -> {
                val credential: Credential = data!!.getParcelableExtra(Credential.EXTRA_KEY)!!
                phoneNumber.text = credential.id
                updatePhoneNumberInSharedPreferences(credential.id)
            }
        }
    }

    private fun phoneSelection() {
        // To retrieve the Phone Number hints, first, configure
        // the hint selector dialog by creating a HintRequest object.
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val options = CredentialsOptions.Builder()
            .forceEnableSaveDialog()
            .build()

        // Then, pass the HintRequest object to
        // credentialsClient.getHintPickerIntent()
        // to get an intent to prompt the currentFirebaseUser to
        // choose a phone number.
        val credentialsClient = Credentials.getClient(applicationContext, options)
        val intent = credentialsClient.getHintPickerIntent(hintRequest)
        try {
            startIntentSenderForResult(
                intent.intentSender, CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0, Bundle()
            )
        } catch (e: SendIntentException) {
            e.printStackTrace()
        }
    }

}