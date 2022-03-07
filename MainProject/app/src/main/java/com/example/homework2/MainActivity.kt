package com.example.homework2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.get
import com.example.homework2.customviews.CustomFlexBox
import com.example.homework2.customviews.CustomTextView
import com.example.homework2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClicklLisneter(findViewById<CustomTextView>(R.id.emoji_view_1))
        setClicklLisneter(findViewById<CustomTextView>(R.id.emoji_view_2))
        setClicklLisneter(findViewById<CustomTextView>(R.id.emoji_view_3))
        setTextOnCustomTextView(findViewById(R.id.emoji_view_2), "\uD83D\uDE18", 100)
        setTextOnCustomTextView(findViewById(R.id.emoji_view_3), "\uD83D\uDE0D", 65)


    }

    private fun setClicklLisneter(_view: View) {
        _view.setOnClickListener { view ->
            view.isSelected = !view.isSelected
        }
    }

    private fun setTextOnCustomTextView(
        view: CustomTextView,
        emoji: String = "\uD83E\uDD70",
        number: Int = 0
    ) {
        view.setEmojiOnView(emoji)
        view.setEmojiNumberOnView(number)
    }
}
