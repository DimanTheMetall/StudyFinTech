package com.example.homework2

import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.homework2.databinding.ActivityMainBinding
import com.example.homework2.dataclasses.Member
import com.example.homework2.fragments.StreamFragment
import com.example.homework2.fragments.ChatFragment
import com.example.homework2.fragments.PeoplesFragment
import com.example.homework2.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        replaceFrag(StreamFragment.newInstance())

        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentByTag(ChatFragment.TAG)

            if (currentFragment is ChatFragment) {
                binding.bNavigation.visibility = View.GONE
            } else {
                binding.bNavigation.visibility = View.VISIBLE
            }
        }

        binding.bNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_channels -> replaceFrag(StreamFragment.newInstance())
                R.id.item_people -> replaceFrag(PeoplesFragment.newInstance())
                R.id.item_profile -> replaceFrag(ProfileFragment.newInstance())
            }
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun replaceFrag(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentHolder.id, fragment)
            .commit()
    }
}
