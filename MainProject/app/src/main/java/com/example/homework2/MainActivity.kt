package com.example.homework2

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.customviews.*
import com.example.homework2.databinding.ActivityMainBinding
import com.example.homework2.fragments.ChannelFragment
import com.example.homework2.fragments.ChatFragment
import com.example.homework2.fragments.PeopleFragment
import com.example.homework2.fragments.ProfileFragment
import org.joda.time.DateTime

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFrag(ChannelFragment.newInstance())

        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentByTag(ChatFragment.TAG)

            if (currentFragment is ChatFragment){
                binding.bNavigation.visibility = View.GONE
            } else {
                binding.bNavigation.visibility = View.VISIBLE
            }
        }

        binding.bNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.item_channels -> replaceFrag(ChannelFragment.newInstance())
                R.id.item_people -> replaceFrag(PeopleFragment.newInstance())
                R.id.item_profile -> replaceFrag(ProfileFragment.newInstance())
            }
            true
        }
    }
    private fun replaceFrag(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentHolder.id, fragment)
            .commit()
    }
}
