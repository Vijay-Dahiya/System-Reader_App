package com.vijay.systemreader

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.vijay.systemreader.databinding.ActivitySmsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySmsBinding
    private var data = StringBuilder("")
    private var dataFilter = StringBuilder("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getSmsDetails()
        binding.bankSms.setOnClickListener {
            binding.textView.text = dataFilter
        }
        binding.allSms.setOnClickListener {
            binding.textView.text = data
        }

    }
    private fun getSmsDetails() {
        CoroutineScope(Dispatchers.IO).launch {
            val cursor =
                contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)

            if (cursor!!.moveToFirst()) { // must check the result to prevent exception
                do {
                    var sub =""
                    for (idx in 0 until cursor.columnCount) {
                        sub ="\n" + cursor.getColumnName(idx) + ":" + cursor.getString(
                            idx
                        )+"\n"
                        data.append(sub)
                        if (sub.contains("XXXXX"))
                            dataFilter.append(sub)
                        Log.d("getting",data.toString())
                    }
                    // use msgData
                } while (cursor.moveToNext())
            } else {
                // empty box, no SMS
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

}