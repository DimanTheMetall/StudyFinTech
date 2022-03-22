package com.example.homework2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.databinding.ProfileLayoutBinding
import com.example.homework2.dataclasses.Profile

class PeopleAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var peopleList = mutableListOf<Profile>()

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

        fun bind(item: Profile) {
            with(binding) {
                profileEmail.text = item.email
                profileName.text = item.name
                when (item.isOnline) {
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

    fun updateProfileList(list: MutableList<Profile>){
        peopleList = list
        notifyDataSetChanged()
    }

}