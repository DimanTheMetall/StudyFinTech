package com.example.homework2.mvp.otherprofile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.homework2.Constance
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


    override fun updateUser() {
        val member: Member = requireArguments().getParcelable(Constance.PROFILE_KEY)
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
                Constance.Status.ACTIVE -> {
                    profileStatusOnline.setTextColor(Color.GREEN)
                }
                Constance.Status.IDLE -> {
                    profileStatusOnline.setTextColor(Color.YELLOW)
                }
                Constance.Status.OFFLINE -> {
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
            val fragment = OtherProfileFragment()
            val argument = Bundle()
            argument.putParcelable(Constance.PROFILE_KEY, member)
            fragment.arguments = argument
            return fragment
        }
    }
}
