package com.example.homework_1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class LocalBroadcastReceiver(context: Context, private val onReceive: (ArrayList<String>) -> Unit): BroadcastReceiver() {

    private val localBroadcastManager = LocalBroadcastManager.getInstance(context)

    override fun onReceive(p0: Context?, p1: Intent?) {
        onReceive.invoke(p1?.extras?.getStringArrayList(Constance.BROAD_CAST_KEY) ?: arrayListOf())
    }

    fun registerReceiver() {
        val intentFilter = IntentFilter(Constance.INTENT_FILTER)
        localBroadcastManager.registerReceiver(this, intentFilter)
    }

    fun unregisterReceiver() {
        localBroadcastManager.unregisterReceiver(this)
    }

}