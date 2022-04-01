package com.example.homework2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.homework2.databinding.ActivityMainBinding
import com.example.homework2.dataclasses.Member
import com.example.homework2.fragments.StreamFragment
import com.example.homework2.fragments.ChatFragment
import com.example.homework2.fragments.PeoplesFragment
import com.example.homework2.fragments.MyProfileFragment
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

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

    private fun getOwnUserData(retrofitService: RetrofitService): Member? {
        var user: Member? = null

        val userDisposable = retrofitService.getOwnUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ user = it }, {})

        compositeDisposable.add(userDisposable)

        return user
    }
}
