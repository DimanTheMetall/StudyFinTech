package com.example.homework2.fragments

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.example.homework2.Constance
import com.example.homework2.PeopleAdapter
import com.example.homework2.Profile
import com.example.homework2.R
import com.example.homework2.databinding.FragmentProfileBinding
import com.example.homework2.viewmodels.ProfileViewModel

class ProfileFragment : Fragment() {
lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater)

        val profile: Profile = requireArguments().getParcelable(Constance.PROFILE_KEY)!!

        binding.apply {
            profileName.text = (profile as Profile).name
            when(profile.isOnline){
                true ->{
                    profileStatusOnline.text = "online"
                    profileStatusOnline.setTextColor(Color.GREEN)
                }
                else -> {
                    profileStatusOnline.text = "offline"
                    profileStatusOnline.setTextColor(Color.RED)
                }
            }
        }

        return binding.root
    }

    companion object {

        fun newInstance(profile: Profile): ProfileFragment {
            val fragment = ProfileFragment()
            val arguments: Bundle = Bundle()
            arguments.putParcelable(Constance.PROFILE_KEY, profile)
            fragment.arguments = arguments

            return fragment
        }
    }
}
