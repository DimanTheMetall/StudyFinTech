package com.example.homework2.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework2.Constance
import com.example.homework2.R
import com.example.homework2.databinding.ProfileLayoutBinding
import com.example.homework2.dataclasses.Member

class PeopleAdapter(val openFrag: (Member) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var peopleList = mutableListOf<Member>()

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
            if (!item.avatar_url.isNullOrBlank()) {
                Glide.with(itemView.context)
                    .load(item.avatar_url)
                    .circleCrop()
                    .into(binding.profileImage)
            } else {
                binding.profileImage.setImageResource(R.mipmap.ic_launcher)
            }

            with(binding) {
                when (item.website?.status) {
                    Constance.Status.ACTIVE -> {
                        onlineImage.isVisible = true
                    }
                    else -> {
                        onlineImage.isVisible = false
                    }
                }

                profileEmail.text = item.email
                profileName.text = item.full_name
                root.setOnClickListener {
                    openFrag.invoke(item)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateProfileList(list: List<Member>) {
        peopleList = list.toMutableList()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ProfileHolder).bind(peopleList[position])
    }

    override fun getItemCount(): Int = peopleList.size

}