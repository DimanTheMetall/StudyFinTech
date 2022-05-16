package com.example.homework2.mvp.otherprofile

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.homework2.Constants
import com.example.homework2.Errors
import com.example.homework2.Errors.*
import com.example.homework2.R
import com.example.homework2.databinding.FragmentOtherProfileBinding
import com.example.homework2.dataclasses.chatdataclasses.Presence
import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.di.otherprofilecomponent.DaggerOtherProfileComponent
import com.example.homework2.di.otherprofilecomponent.OtherProfileComponent
import com.example.homework2.mvp.BaseFragment
import com.example.homework2.zulipApp
import javax.inject.Inject

class OtherProfileFragment : BaseFragment<OtherProfilePresenter, FragmentOtherProfileBinding>(),
    OtherProfileView {

    @Inject
    override lateinit var presenter: OtherProfilePresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val otherProfileComponent: OtherProfileComponent = DaggerOtherProfileComponent.factory()
            .create(requireActivity().zulipApp().zulipComponent)
        otherProfileComponent.inject(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUser()
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

    override fun updateUser() {
        val member: Member = requireArguments().getParcelable(Constants.PROFILE_KEY)
            ?: throw IllegalArgumentException("Member cannot be null")
        presenter.onUserNeededUpdate(member = member)
    }

    override fun setStatus(member: Member, presence: Presence) {

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(32))
        Glide.with(requireContext())
            .load(member.avatarUrl)
            .apply(requestOptions)
            .placeholder(R.mipmap.ic_launcher)
            .into(binding.profileImage)

        with(binding) {
            profileName.text = member.fullName
            profileStatusOnline.text = presence.website.status
            when (presence.website.status) {
                Constants.Status.ACTIVE -> {
                    profileStatusOnline.setTextColor(Color.GREEN)
                }
                Constants.Status.IDLE -> {
                    profileStatusOnline.setTextColor(Color.YELLOW)
                }
                Constants.Status.OFFLINE -> {
                    profileStatusOnline.setTextColor(Color.RED)
                }
            }
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOtherProfileBinding {
        return FragmentOtherProfileBinding.inflate(inflater, container, false)
    }

    companion object {

        fun newInstance(member: Member): OtherProfileFragment {
            return OtherProfileFragment().apply {
                arguments = bundleOf(Constants.PROFILE_KEY to member)
            }
        }
    }
}
