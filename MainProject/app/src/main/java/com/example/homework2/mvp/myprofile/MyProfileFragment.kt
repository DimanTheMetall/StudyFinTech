package com.example.homework2.mvp.myprofile

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.homework2.Constants
import com.example.homework2.Errors
import com.example.homework2.Errors.*
import com.example.homework2.R
import com.example.homework2.databinding.FragmentProfileBinding
import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.di.myprofilecomponent.DaggerMyProfileComponent
import com.example.homework2.di.myprofilecomponent.MyProfileComponent
import com.example.homework2.mvp.BaseFragment
import com.example.homework2.zulipApp
import org.joda.time.DateTime
import javax.inject.Inject

class MyProfileFragment : BaseFragment<MyProfilePresenter, FragmentProfileBinding>(),
    MyProfileView {

    @Inject
    override lateinit var presenter: MyProfilePresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val myProfileComponent: MyProfileComponent =
            DaggerMyProfileComponent.factory().create(requireActivity().zulipApp().zulipComponent)
        myProfileComponent.inject(this)
        presenter.onInit()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun renderProfile(member: Member) {
        val currentTime = DateTime().millis
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(32))

        Glide.with(requireContext())
            .load(member.avatarUrl)
            .placeholder(R.mipmap.ic_launcher)
            .apply(requestOptions)
            .into(binding.profileImage)

        with(binding) {
            profileName.text = member.fullName

            if (member.website != null && currentTime - member.website.timestamp > Constants.Status.TIME_FOR_ON_ACTIVE_STATUS) {
                profileStatusOnline.text = Constants.Status.OFFLINE
                profileStatusOnline.setTextColor(Color.RED)
            }

            when (member.website?.status) {
                Constants.Status.ACTIVE -> {
                    profileStatusOnline.text = member.website.status
                    profileStatusOnline.setTextColor(Color.GREEN)
                }
                Constants.Status.IDLE -> {
                    profileStatusOnline.text = member.website.status
                    profileStatusOnline.setTextColor(Color.YELLOW)
                }
            }
        }
    }

    override fun showError(throwable: Throwable, error: Errors) {
        val textMessage = when (error) {
            INTERNET -> getString(R.string.internetError)
            SYSTEM -> getString(R.string.systemError)
            BACKEND -> getString(R.string.backend_error)
        }
        Log.e(Constants.LogTag.PEOPLES, textMessage, throwable)
        Toast.makeText(requireContext(), textMessage, Toast.LENGTH_SHORT).show()
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    companion object {
        fun newInstance(): MyProfileFragment = MyProfileFragment()
    }

}
