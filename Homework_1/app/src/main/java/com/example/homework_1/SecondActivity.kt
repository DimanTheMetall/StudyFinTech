package com.example.homework_1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.homework_1.databinding.ActivitySecondBinding


class SecondActivity : AppCompatActivity() {
    lateinit var secondBinding: ActivitySecondBinding

    private val localBroadcastReceiver by lazy {
        LocalBroadcastReceiver(this) {
            println(it.joinToString())
            secondBinding.progressCircular.isVisible = false
            sendResult(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        secondBinding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(secondBinding.root)
        secondBinding.btnStartService.isVisible = true

        secondBinding.btnStartService.setOnClickListener { startContactService() }
    }

    override fun onStart() {
        super.onStart()
        localBroadcastReceiver.registerReceiver()
    }

    override fun onStop() {
        localBroadcastReceiver.unregisterReceiver()
        super.onStop()
    }

    private fun startContactService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
        } else {
            secondBinding.progressCircular.isVisible = true
            secondBinding.btnStartService.isVisible = false
            startService(Intent(this, ContactService::class.java))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startContactService()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.second_permission_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun sendResult(result: ArrayList<String>) {
        val intent = Intent()
        intent.putStringArrayListExtra(Constance.RESULT_KEY, result)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        private const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

}