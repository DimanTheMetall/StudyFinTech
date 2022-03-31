package com.example.homework2

import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework2.databinding.ProfileLayoutBinding
import com.example.homework2.dataclasses.Member

class PeopleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var peopleList = mutableListOf<Member>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.profile_layout,
            null,
            false

        )
        return ProfileHolder(view)
    }

    class ProfileHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ProfileLayoutBinding.bind(view)

        fun bind(item: Member) {
            when(item.avatar_url!=null){
                true -> {Glide.with(itemView.context)
                    .load(item.avatar_url)
                    .into(binding.profileImage)}
            }


            with(binding) {
                profileEmail.text = item.email
                profileName.text = item.full_name
                when (item.is_active) {
                    true -> onlineImage.visibility = View.VISIBLE
                    else -> onlineImage.visibility = View.INVISIBLE
                }
                root.setOnClickListener {
                    //Need or not go on profile clicker?
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ProfileHolder).bind(peopleList[position])
    }

    override fun getItemCount(): Int = peopleList.size

    fun updateProfileList(list: List<Member>){
        peopleList = list.toMutableList()
        notifyDataSetChanged()
    }

}
