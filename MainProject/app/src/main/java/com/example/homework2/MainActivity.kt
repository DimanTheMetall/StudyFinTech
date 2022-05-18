package com.example.homework2

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.homework2.databinding.ActivityMainBinding
import com.example.homework2.mvp.chat.ChatFragment
import com.example.homework2.mvp.myprofile.MyProfileFragment
import com.example.homework2.mvp.peoples.PeoplesFragment
import com.example.homework2.mvp.streams.StreamFragment
import io.reactivex.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        compositeDisposable = CompositeDisposable()

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
                R.id.item_profile -> replaceFrag(MyProfileFragment.newInstance())
            }
            true
        }
        supportActionBar?.hide()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            supportFragmentManager.popBackStack()
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        compositeDisposable.clear()
    }

    private fun replaceFrag(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentHolder.id, fragment)
            .commit()
    }

}
