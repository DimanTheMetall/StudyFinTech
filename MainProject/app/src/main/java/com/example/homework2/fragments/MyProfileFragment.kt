package com.example.homework2.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.homework2.Constance
import com.example.homework2.R
import com.example.homework2.ZulipApp
import com.example.homework2.databinding.FragmentProfileBinding
import com.example.homework2.dataclasses.Member
import com.example.homework2.viewmodels.ProfileViewModel
import io.reactivex.disposables.CompositeDisposable

class MyProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater)

        val myProfileDisposable = viewModel.myProfileObservable
            .subscribe({ renderProfile(member = it)}, {})

        viewModel.loadMyProfile((requireActivity().application as ZulipApp).retrofitService)

        compositeDisposable.add(myProfileDisposable)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        compositeDisposable.clear()
    }

    private fun renderProfile(member: Member) {

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(32))

        Glide.with(requireContext())
            .load(member.avatar_url)
            .placeholder(R.mipmap.ic_launcher)
            .apply(requestOptions)
            .into(binding.profileImage)

        with(binding) {
            profileName.text = member.full_name

            if (member.website != null && member.website.timestamp > 150) {
                profileStatusOnline.text = Constance.Status.OFFLINE
                profileStatusOnline.setTextColor(Color.RED)
            }

            when (member.website?.status) {
                Constance.Status.ACTIVE -> {
                    profileStatusOnline.text = member.website.status
                    profileStatusOnline.setTextColor(Color.GREEN)
                }
                Constance.Status.IDLE -> {
                    profileStatusOnline.text = member.website.status
                    profileStatusOnline.setTextColor(Color.YELLOW)
                }
            }
        }
    }

    companion object {

        fun newInstance(): MyProfileFragment {

            return MyProfileFragment()
        }
    }
}
