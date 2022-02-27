package com.example.homework_1

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.provider.ContactsContract
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.lang.Thread.sleep


class ContactService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread {
            sleep(2000)
            for (i in 0..10) {
                println("I love peace $i")
            }
            sendMessage(readContact())
            stopSelf()
        }.start()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun sendMessage(data: ArrayList<String>) {
        val intent = Intent(Constance.INTENT_FILTER).putStringArrayListExtra(Constance.BROAD_CAST_KEY, data)
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.sendBroadcast(intent)
    }

    private fun readContact(): ArrayList<String> {
        val contacts: ArrayList<String> = ArrayList()
        val cr = contentResolver
        val cursor = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        ) ?: return arrayListOf()

        if (cursor.moveToFirst()) {
            do {
                val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    val name = cursor.getString(nameIndex)
                    contacts.add(name)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return contacts
    }

}