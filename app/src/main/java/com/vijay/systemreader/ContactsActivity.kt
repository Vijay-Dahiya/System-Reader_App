package com.vijay.systemreader

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.vijay.systemreader.databinding.ActivityContactsBinding


class ContactsActivity : AppCompatActivity() {
    private val sb = StringBuffer()
    private lateinit var binding: ActivityContactsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadContacts()
        binding.btn.setOnClickListener {
            binding.textView.text = sb
        }

    }

    @SuppressLint("Range")
    private fun loadContacts() {
        val cr = contentResolver
        val cur = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )

        if ((cur?.count ?: 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                val id = cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts._ID)
                )
                val name = cur.getString(
                    cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )
                var phoneNo =""
                if (cur.getInt(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER
                        )
                    ) > 0
                ) {
                    val pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (pCur!!.moveToNext()) {
                         phoneNo = pCur!!.getString(
                            pCur!!.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )
                    }
                    pCur!!.close()
                }
                sb.append(
                    """
Name:--- $name 
Id:--- $id 
PhoneNumber:--- $phoneNo"""
                )
                sb.append("\n----------------------------------")
            }
        }
        cur?.close()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }


}