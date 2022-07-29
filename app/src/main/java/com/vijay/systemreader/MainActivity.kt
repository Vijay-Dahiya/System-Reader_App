package com.vijay.systemreader

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.CallLog
import androidx.appcompat.app.AppCompatActivity
import com.vijay.systemreader.databinding.ActivityMainBinding
import java.lang.Long
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.String
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsOptions
import com.google.android.gms.auth.api.credentials.HintRequest
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.content.IntentSender
import android.text.Editable
import android.util.Log


class MainActivity : AppCompatActivity() {
    val sb = StringBuffer()
    var myInt = 0
    private lateinit var binding: ActivityMainBinding
    private var data : StringBuilder = StringBuilder("")
    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val am = android.provider.Settings.Secure.getString(
            applicationContext.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        )

        data.append(am.toString())
        binding.textView.text = data
//       mainMethod()
        requestPhone()
    }
    @SuppressLint("HardwareIds")
    private fun mainMethod(){

        getPhoneDetails()
        binding.btn.setOnClickListener{
            binding.textView.text = sb
            myInt--
        }
        binding.sms.setOnClickListener{
            startActivity(Intent(this, SmsActivity::class.java))
            finish()
        }
        binding.contacts.setOnClickListener {
            startActivity(Intent(this, ContactsActivity::class.java))
            finish()
        }
        binding.Apps.setOnClickListener {
            startActivity(Intent(this, AppsName::class.java))
            finish()
        }
    }
    private val phonePickIntentResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result != null) {
                val intent = result.data
                val credential = intent?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)

                val num = credential?.id?.substring(3)
                num?.let {
                    binding.editTextPhone.setText(Editable.Factory.getInstance().newEditable(num).toString())
                }

                num?.let {
                    Log.d("phoneNumber", it)
                }
            }
        }
    private fun requestPhone() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val options = CredentialsOptions.Builder()
            .forceEnableSaveDialog()
            .build()

        val credentialClient = Credentials.getClient(applicationContext, options)
        val intent = credentialClient.getHintPickerIntent(hintRequest)

        try {
            val intentSenderRequest = IntentSenderRequest.Builder(intent.intentSender).build()
            phonePickIntentResultLauncher.launch(intentSenderRequest)
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("Range")
    private fun getPhoneDetails() {

        binding.textView.text = data
        CoroutineScope(Dispatchers.IO).launch {

            val managedCursor = managedQuery(
                CallLog.Calls.CONTENT_URI, null,
                null, null, null
            )
            val number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER)
            val type = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
            val date = managedCursor.getColumnIndex(CallLog.Calls.DATE)
            val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
            sb.append("Call Details :")
            while (managedCursor.moveToNext()) {
                val phNumber = managedCursor.getString(number)
                val callType = managedCursor.getString(type)
                val callDate = managedCursor.getString(date)
                val callDayTime = Date(Long.valueOf(callDate))
                val callDuration = managedCursor.getString(duration)
                var dir: String? = null
                val dircode = callType.toInt()
                when (dircode) {
                    CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
                    CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
                    CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
                }
                sb.append(
                    """
Phone Number:--- $phNumber 
Call Type:--- $dir 
Call Date:--- $callDayTime 
Call duration in sec :--- $callDuration"""
                )
                sb.append("\n----------------------------------")
            }
            managedCursor.close()

        }


    }

    override fun onBackPressed() {

        if (myInt <=0) {
            binding.textView.text = ""
            myInt++
        }else
            super.onBackPressed()

    }
}


