package com.example.homework2.mvp.myprofile

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.homework2.Constance
import com.example.homework2.R
import com.example.homework2.databinding.FragmentProfileBinding
import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.mvp.BaseFragment
import com.example.homework2.zulipApp

class MyProfileFragment : BaseFragment<MyProfilePresenter, FragmentProfileBinding>(),
    MyProfileView {

    override fun renderProfile(member: Member) {

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(32))

        Glide.with(requireContext())
            .load(member.avatarUrl)
            .placeholder(R.mipmap.ic_launcher)
            .apply(requestOptions)
            .into(binding.profileImage)

        with(binding) {
            profileName.text = member.fullName

            if (member.website != null && member.website.timestamp > Constance.Status.TIME_FOR_ON_ACTIVE_STATUS) {
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

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun initPresenter(): MyProfilePresenter {
        return MyProfilePresenterImpl(
            model = MyProfileModelImpl(requireActivity().zulipApp().retrofitService)
        )
    }
}
