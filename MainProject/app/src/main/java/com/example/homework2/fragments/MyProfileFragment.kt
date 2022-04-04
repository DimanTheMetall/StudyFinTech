package com.example.homework2.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.homework2.R
import com.example.homework2.ZulipApp
import com.example.homework2.databinding.FragmentProfileBinding
import com.example.homework2.dataclasses.Member
import com.example.homework2.dataclasses.chatdataclasses.Website
import com.example.homework2.viewmodels.MyProfileViewModel
import com.example.homework2.zulipApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

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

        requireActivity().zulipApp().retrofitService
            .getPresense(member.email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                renderProfile(member, it.presence.website)
            },{
                //обработать ошибку
            })
    }
    private fun renderProfile(member: Member, website: Website){
        Glide.with(requireContext())
            .load(member.avatar_url)
            .placeholder(R.mipmap.ic_launcher)
            .circleCrop()
            .into(binding.profileImage)

        with(binding){
            profileName.text = member.full_name

            if (website.timestamp>10){
                profileStatusOnline.text = "offline"
                profileStatusOnline.setTextColor(Color.RED)
            }

            when(website.status){
                "active" -> {
                    profileStatusOnline.text = website.status
                    profileStatusOnline.setTextColor(Color.GREEN)
                }
                "idle" -> {
                    profileStatusOnline.text = website.status
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
