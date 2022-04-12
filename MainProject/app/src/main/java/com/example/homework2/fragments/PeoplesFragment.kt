package com.example.homework2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework2.R
import com.example.homework2.ZulipApp
import com.example.homework2.adapters.PeopleAdapter
import com.example.homework2.databinding.FragmentPeopleBinding
import com.example.homework2.dataclasses.Member
import com.example.homework2.dataclasses.ResultMember
import com.example.homework2.viewmodels.PeoplesViewModel
import com.example.homework2.zulipApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class PeoplesFragment : Fragment() {

    private var _binding: FragmentPeopleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PeoplesViewModel by viewModels()
    private val recycleAdapter = PeopleAdapter { member -> openProfileFrag(member) }
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
        val shimmer = binding.shimmerPeople

        val searchDisposable = searchSubject
            .debounce(1, TimeUnit.SECONDS)
            .distinctUntilChanged()
            .subscribe {
                viewModel.onSearchProfile(
                    it, requireActivity().zulipApp().retrofitService
                )
            }

        val profileDisposable = viewModel.peoplesObservable
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

        viewModel.loadAllUsers((requireActivity().application as ZulipApp).retrofitService)

        compositeDisposable.add(searchDisposable)
        compositeDisposable.add(profileDisposable)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        compositeDisposable.clear()
    }

    private fun openProfileFrag(member: Member) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_holder, OtherProfileFragment.newInstance(member))
            .addToBackStack(null)
            .commit()
    }

    companion object {

        fun newInstance(): PeoplesFragment {
            return PeoplesFragment()
        }
    }
}
