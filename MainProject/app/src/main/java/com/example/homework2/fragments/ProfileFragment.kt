package com.example.homework2.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.homework2.Constance
import com.example.homework2.R
import com.example.homework2.dataclasses.Profile
import com.example.homework2.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater)

        val profile: Profile = requireArguments().getParcelable(Constance.PROFILE_KEY)!!

        binding.apply {
            profileName.text = (profile).name
            when (profile.isOnline) {
                true -> {
                    profileStatusOnline.setText(R.string.online)
                    profileStatusOnline.setTextColor(Color.GREEN)
                }
                else -> {
                    profileStatusOnline.setText(R.string.offline)
                    profileStatusOnline.setTextColor(Color.RED)
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun newInstance(profile: Profile): ProfileFragment {
            val fragment = ProfileFragment()
            val arguments = Bundle()
            arguments.putParcelable(Constance.PROFILE_KEY, profile)
            fragment.arguments = arguments

            return fragment
        }
    }
}
