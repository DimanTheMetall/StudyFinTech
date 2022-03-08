package com.example.homework2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.homework2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.customViewGroup.addEmoji("\uD83E\uDD70", 10)
        binding.customViewGroup.addEmoji("\uD83E\uDD70", 10)
        binding.customViewGroup.addEmoji("\uD83E\uDD70", 10)
    }
}
