package com.example.homework2.fragments

import android.graphics.Color
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
import com.example.homework2.retrofit.RetrofitService
import com.example.homework2.zulipApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OtherProfileFragment : Fragment() {

    private var _binding: FragmentOtherProfileBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtherProfileBinding.inflate(inflater)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val member: Member? = requireArguments().getParcelable(Constance.PROFILE_KEY)
        renderStatus(requireActivity().zulipApp().retrofitService, member!!)

    }

    private fun setStatus(member: Member, status: String) {
        with(binding) {
            profileName.text = member.full_name
            profileStatusOnline.text = status
            when (status) {
                "active" -> {
                    profileStatusOnline.setTextColor(Color.GREEN)
                }
                "idle" -> {
                    profileStatusOnline.setTextColor(Color.YELLOW)
                }
                "offline" -> {
                    profileStatusOnline.setTextColor(Color.RED)
                }
            }
        }
    }

    private fun renderStatus(retrofitService: RetrofitService, member: Member) {
        var resultString: String = "offline"

        val presenceDisposalbe = retrofitService.getPresense(member.email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.presence.website.timestamp < 10) {
                    setStatus(member, "offline")
                } else {
                    setStatus(member, it.presence.website.status)
                }
            }, { setStatus(member, "offline") })

        Glide.with(requireContext())
            .load(member.avatar_url)
            .circleCrop()
            .placeholder(R.mipmap.ic_launcher)
            .into(binding.profileImage)

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
