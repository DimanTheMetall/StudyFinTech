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
import com.example.homework2.databinding.FragmentOtherProfileBinding
import com.example.homework2.dataclasses.Member
import com.example.homework2.dataclasses.chatdataclasses.Presence
import com.example.homework2.viewmodels.ProfileViewModel
import com.example.homework2.zulipApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class OtherProfileFragment : Fragment() {

    private var _binding: FragmentOtherProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtherProfileBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val member: Member = requireArguments().getParcelable(Constance.PROFILE_KEY)
            ?: throw IllegalArgumentException("Member cannot be null")

        val statusDisposable = viewModel.otherProfilePresenceObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { setStatus(member, it) }

        viewModel.loadOtherProfile(requireActivity().zulipApp().retrofitService, member)
        compositeDisposable.add(statusDisposable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        compositeDisposable.clear()
    }

    private fun setStatus(member: Member, presence: Presence) {

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(32))
        Glide.with(requireContext())
            .load(member.avatar_url)
            .apply(requestOptions)
            .placeholder(R.mipmap.ic_launcher)
            .into(binding.profileImage)

        with(binding) {
            profileName.text = member.full_name
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
