package com.vijay.systemreader

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.vijay.systemreader.databinding.ActivityReadOtpBinding
import java.text.SimpleDateFormat
import java.util.*


class ReadOtp : AppCompatActivity() {
    private lateinit var binding: ActivityReadOtpBinding
    val myCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        startListeningSms(this)
//        openCalender()
        openPoneNumberSuggestion()

    }

    private fun openCalender() {
        val date =
            OnDateSetListener { view, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                updateLabel()
            }
        binding.calTv.setOnClickListener {
            DatePickerDialog(
                this,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
    }

    private fun updateLabel() {
        val dateFormat = SimpleDateFormat("MM/dd/yy", Locale.US)
        binding.calTv.text = dateFormat.format(myCalendar.time)
    }

    private fun startListeningSms(
        context: Context
    ) {
        val client = SmsRetriever.getClient(context)

        // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
        // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
        // action SmsRetriever#SMS_RETRIEVED_ACTION.
        val task: Task<Void> = client.startSmsRetriever()

        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(OnSuccessListener<Void?> {
            // Successfully started retriever, expect broadcast intent

        })

        task.addOnFailureListener(OnFailureListener {
            // Failed to start retriever, inspect Exception for more details
        })
    }

    private fun openPoneNumberSuggestion() {
        val request: GetPhoneNumberHintIntentRequest =
            GetPhoneNumberHintIntentRequest.builder().build()
        val resultLauncher2: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
                try {
                    val phoneNumber = Identity.getSignInClient(this).getPhoneNumberFromIntent(result.data)
                } catch(e: Exception) {

                }
            }
        val resultLauncher: ActivityResultLauncher<IntentSenderRequest> = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            try {
                val phoneNumber =
                    Identity.getSignInClient(this).getPhoneNumberFromIntent(result.data)
                Toast.makeText(this, "$phoneNumber",Toast.LENGTH_SHORT).show()
                // Do something with the number
            } catch (e: Exception) {
                Log.e("vijay", "Phone Number Hint failed")
            }
        }
        Identity.getSignInClient(this)
            .getPhoneNumberHintIntent(request)

            .addOnSuccessListener { request: PendingIntent ->
                try {
                    resultLauncher.launch(IntentSenderRequest.Builder(request).build())
                } catch(e: Exception) {
                    Log.e("TAG", "Launching the PendingIntent failed")
                }
            }
            .addOnFailureListener{

            }



        }
    private fun getPhoneByGoogleMethod(){
        val request: GetPhoneNumberHintIntentRequest =
            GetPhoneNumberHintIntentRequest.builder().build()
//        val phoneNumberHintIntentResultLaunche: ActivityResultLauncher<IntentSenderRequest> =
//            registerForActivityResult.ActivityResultContracts.StartActivityForResult(){ result ->
//                try {
//                    val phoneNumber = Identity.getSignInClient(this).getPhoneNumberFromIntent(result.data)
//                } catch(e: Exception) {
//
//                }
//            }
//        val phoneNumberHintIntentResultLauncher: ActivityResultLauncher =
//
//        Identity.getSignInClient(this)
//            .getPhoneNumberHintIntent(request)
//            .addOnSuccessListener{request : PendingIntent ->
//                try {
//                    phoneNumberHintIntentResultLaunche.launch(request.getIntentSender())
//                } catch(e: Exception) {
//                }}
//            .addOnFailureListener{
//
//            }
    }
//    }

}