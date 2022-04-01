package com.example.homework2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.homework2.Constance
import com.example.homework2.R
import com.example.homework2.databinding.FragmentOtherProfileBinding
import com.example.homework2.databinding.FragmentPeopleBinding
import com.example.homework2.dataclasses.Member

class OtherProfileFragment : Fragment() {

    private var _binding: FragmentOtherProfileBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtherProfileBinding.inflate(inflater)

        val member: Member? = requireArguments().getParcelable(Constance.PROFILE_KEY)
        renderProfile(member)


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderProfile(member: Member?) {
        if (member != null) {
            Glide.with(requireContext())
                .load(member.avatar_url)
                .into(binding.profileImage)
            with(binding) {
                profileName.text = member.full_name

            }
        }
    }

    companion object {

        fun newInstance(member: Member): OtherProfileFragment {
            val fragment = OtherProfileFragment()
            val argument = Bundle()
            argument.putParcelable(Constance.PROFILE_KEY, member)
            fragment.arguments = argument
            return fragment
        }
    }
}