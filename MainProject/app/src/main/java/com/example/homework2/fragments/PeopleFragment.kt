package com.example.homework2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework2.PeopleAdapter
import com.example.homework2.databinding.FragmentPeopleBinding
import com.example.homework2.viewmodels.ProfileViewModel


class PeopleFragment : Fragment() {


    lateinit var binding: FragmentPeopleBinding
    private val viewModel: ProfileViewModel by viewModels()
    private val recycleAdapter = PeopleAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPeopleBinding.inflate(inflater)
        binding.recyclePeople.adapter = recycleAdapter
        binding.recyclePeople.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )

        viewModel.profileList.observe(viewLifecycleOwner) {
            recycleAdapter.updateProfileList(it.toMutableList())
        }
        return binding.root
    }

    companion object {

        fun newInstance(): PeopleFragment {
            return PeopleFragment()
        }
    }
}
