package com.example.homework2.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.homework2.Constance
import com.example.homework2.R
import com.example.homework2.ZulipApp
import com.example.homework2.databinding.FragmentProfileBinding
import com.example.homework2.dataclasses.Member
import com.example.homework2.viewmodels.MyProfileViewModel
import io.reactivex.disposables.CompositeDisposable

class MyProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyProfileViewModel by viewModels()
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater)

        val myProfileDisposable = viewModel.subjectProfile
            .subscribe { updateMyProfile(it) }

        viewModel.loadMyProfile((requireActivity().application as ZulipApp).retrofitService)

        compositeDisposable.add(myProfileDisposable)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        compositeDisposable.clear()
    }

    private fun updateMyProfile(member: Member) {
        Glide.with(this@MyProfileFragment.requireContext())
            .load(member.avatar_url)
            .circleCrop()
            .into(binding.profileImage)

        binding.profileName.text = member.full_name

        when (member.is_active) {
            true -> {
                binding.profileStatusOnline.setText(R.string.online)
                binding.profileStatusOnline.setTextColor(Color.GREEN)
            }
            else -> {
                binding.profileStatusOnline.setText(R.string.offline)
                binding.profileStatusOnline.setTextColor(Color.RED)
            }
        }


    }

    companion object {

        fun newInstance(): MyProfileFragment {

            return MyProfileFragment()
        }
    }
}
