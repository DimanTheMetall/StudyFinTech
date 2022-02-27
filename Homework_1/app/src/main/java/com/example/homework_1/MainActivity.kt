package com.example.homework_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.homework_1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.textOnMain.text = getString(R.string.main_contact_init)

        val activityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data?.getStringArrayListExtra(Constance.RESULT_KEY) ?: arrayListOf()
                    renderState(data)
                }
            }

        mainBinding.btnOnSecondActivity.setOnClickListener {
            activityLauncher.launch(Intent(this, SecondActivity::class.java))
        }

    }

    private fun renderState(result: List<String>) {
        if (result.isEmpty()) {
            mainBinding.recycler.isVisible = false
            mainBinding.textOnMain.isVisible = true
            mainBinding.textOnMain.text = getString(R.string.main_contact_empty)
        } else {
            mainBinding.textOnMain.isVisible = false
            mainBinding.recycler.isVisible = true
            mainBinding.recycler.adapter = AccountAdapter(result)
        }
    }
}