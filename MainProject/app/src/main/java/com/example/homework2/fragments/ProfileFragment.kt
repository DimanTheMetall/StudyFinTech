package com.example.homework2.fragments

import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.homework2.Constance
import com.example.homework2.R
import com.example.homework2.databinding.FragmentProfileBinding
import com.example.homework2.dataclasses.Member

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater)

//        val profile: Member = requireArguments().getParcelable(Constance.PROFILE_KEY)!!

//        binding.apply {
//            profileName.text = profile.full_name
//            when (profile.is_active) {
//                true -> {
//                    profileStatusOnline.setText(R.string.online)
//                    profileStatusOnline.setTextColor(Color.GREEN)
//                }
//                else -> {
//                    profileStatusOnline.setText(R.string.offline)
//                    profileStatusOnline.setTextColor(Color.RED)
//                }
//            }
//        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun newInstance(): ProfileFragment {
            val fragment = ProfileFragment()
//            val arguments = Bundle()
//            arguments.putParcelable(Constance.PROFILE_KEY, profile)
//            fragment.arguments = arguments

            return fragment
        }
    }
}
