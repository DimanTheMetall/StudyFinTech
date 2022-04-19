package com.example.homework2.mvp.peoples

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework2.R
import com.example.homework2.databinding.FragmentPeopleBinding
import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.mvp.BaseFragment
import com.example.homework2.mvp.otherprofile.OtherProfileFragment
import com.example.homework2.zulipApp
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class PeoplesFragment : BaseFragment<PeoplesPresenter, FragmentPeopleBinding>(), PeoplesView {

    private val compositeDisposable = CompositeDisposable()
    private val recycleAdapter = PeopleAdapter { member -> openProfileFrag(member) }
    private lateinit var shimmer: ShimmerFrameLayout


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureRecycleAdapter()
        initSearchedTextListener()
        initShimmer()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    private fun openProfileFrag(member: Member) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_holder, OtherProfileFragment.newInstance(member))
            .addToBackStack(null)
            .commit()
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPeopleBinding {
        return FragmentPeopleBinding.inflate(inflater, container, false)
    }

    override fun initPresenter(): PeoplesPresenter {
        return PeoplesPresenterImpl(
            view = this,
            model = PeoplesModelImpl(requireActivity().zulipApp().retrofitService)
        )
    }

    private fun configureRecycleAdapter() {
        binding.recyclePeople.adapter = recycleAdapter
        binding.recyclePeople.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
    }

    private fun initSearchedTextListener() {
        val subject = PublishSubject.create<String>()
        val disposable = subject
            .debounce(1, TimeUnit.SECONDS)
            .filter { it.isNotEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { presenter.onSearchedTextChanged(it.toString()) }

        compositeDisposable.add(disposable)

        binding.searchUsers.addTextChangedListener { text ->
            subject.onNext(text.toString())
        }
    }

    private fun initShimmer() {
        shimmer = binding.shimmerPeople
    }


    override fun showProgress() {
        shimmer.showShimmer(true)
    }

    override fun showError() {
        Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_LONG).show()
        shimmer.hideShimmer()
    }

    override fun showUsers(userList: List<Member>) {
        recycleAdapter.updateProfileList(userList)
        shimmer.hideShimmer()
    }

    companion object {

        fun newInstance(): PeoplesFragment {
            return PeoplesFragment()
        }
    }

}
