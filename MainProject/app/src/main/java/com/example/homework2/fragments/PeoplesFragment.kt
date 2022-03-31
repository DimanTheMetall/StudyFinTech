package com.example.homework2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework2.PeopleAdapter
import com.example.homework2.ZulipApp
import com.example.homework2.databinding.FragmentPeopleBinding
import com.example.homework2.dataclasses.ResultMember
import com.example.homework2.viewmodels.ProfileViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class PeoplesFragment : Fragment() {

    private var _binding: FragmentPeopleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private val recycleAdapter = PeopleAdapter()
    private val compositeDisposable = CompositeDisposable()
    private val searchSubject = PublishSubject.create<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPeopleBinding.inflate(inflater)
        binding.recyclePeople.adapter = recycleAdapter
        binding.recyclePeople.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )

        binding.searchUsers.addTextChangedListener { text ->
            searchSubject.onNext(text.toString())
        }

        val searchDisposable = searchSubject
            .debounce(1, TimeUnit.SECONDS)
            .distinctUntilChanged()
            .subscribe{ viewModel.onSearchProfile(it) }

        val shimmer = binding.shimmerPeople
        val profileDisposable = viewModel.memberObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is ResultMember.Error -> {
                        Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_LONG).show()
                        shimmer.hideShimmer()
                    }
                    is ResultMember.Progress -> {
                        shimmer.showShimmer(true)
                    }
                    is ResultMember.Success -> {
                        recycleAdapter.updateProfileList(it.profileList)
                        shimmer.hideShimmer()
                    }
                }
            }
        binding.searchImage.setOnClickListener {
            viewModel.loadAllUsers((requireActivity().application as ZulipApp).retrofitService)
        }
        compositeDisposable.add(searchDisposable)
        compositeDisposable.add(profileDisposable)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        compositeDisposable.clear()
    }


    companion object {

        fun newInstance(): PeoplesFragment {
            return PeoplesFragment()
        }
    }
}