package com.example.homework2.mvp.peoples

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework2.Constance
import com.example.homework2.PeoplesDiffCallback
import com.example.homework2.R
import com.example.homework2.databinding.ProfileLayoutBinding
import com.example.homework2.dataclasses.streamsandtopics.Member

class PeopleAdapter(private val openFrag: (Member) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differ = AsyncListDiffer(this, PeoplesDiffCallback())

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.profile_layout,
            null,
            false
        )
        return ProfileHolder(view)
    }

    inner class ProfileHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ProfileLayoutBinding.bind(view)

        fun bind(item: Member) {
            setAvatar(member = item, binding = binding, itemView = itemView)
            setStatus(member = item, binding = binding)
        }
    }


    fun updateProfileList(list: List<Member>) {
        differ.submitList(list)
    }

    private fun setAvatar(member: Member, binding: ProfileLayoutBinding, itemView: View) {
        if (!member.avatarUrl.isNullOrBlank()) {
            Glide.with(itemView.context)
                .load(member.avatarUrl)
                .placeholder(R.mipmap.ic_launcher)
                .circleCrop()
                .into(binding.profileImage)
        } else {
            binding.profileImage.setImageResource(R.mipmap.ic_launcher)
        }
    }

    private fun setStatus(member: Member, binding: ProfileLayoutBinding) {
        with(binding) {
            when (member.website?.status) {
                Constance.Status.ACTIVE -> {
                    onlineImage.isVisible = true
                }
                else -> {
                    onlineImage.isVisible = false
                }
            }

            profileEmail.text = member.email
            profileName.text = member.fullName
            root.setOnClickListener {
                openFrag.invoke(member)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ProfileHolder).bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

}
