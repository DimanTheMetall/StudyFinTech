package com.example.homework2.mvp.peoples

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework2.Constance
import com.example.homework2.R
import com.example.homework2.databinding.FragmentPeopleBinding
import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.di.peoplescomponent.DaggerPeoplesComponent
import com.example.homework2.di.peoplescomponent.PeoplesComponent
import com.example.homework2.mvp.BaseFragment
import com.example.homework2.mvp.otherprofile.OtherProfileFragment
import com.example.homework2.zulipApp
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PeoplesFragment : BaseFragment<PeoplesPresenter, FragmentPeopleBinding>(), PeoplesView {

    private val compositeDisposable = CompositeDisposable()
    private val recycleAdapter = PeopleAdapter { member -> openProfileFrag(member) }
    private lateinit var shimmer: ShimmerFrameLayout

    @Inject
    override lateinit var presenter: PeoplesPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val peoplesComponent: PeoplesComponent =
            DaggerPeoplesComponent.factory().create(requireActivity().zulipApp().zulipComponent)
        peoplesComponent.inject(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureRecycleAdapter()
        initSearchedTextListener()
        initShimmer()
        super.onViewCreated(view, savedInstanceState)
        initCancelClickListener()
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

    private fun configureRecycleAdapter() {
        binding.recyclePeople.adapter = recycleAdapter
        binding.recyclePeople.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
    }

    private fun initCancelClickListener() {
        binding.cancelImage.setOnClickListener {
            presenter.onInit()
            binding.searchUsers.setText("")
        }
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

            if (!text.isNullOrEmpty()) {
                binding.cancelImage.visibility = View.VISIBLE
            } else {
                binding.cancelImage.visibility = View.GONE
            }
        }
    }

    private fun initShimmer() {
        shimmer = binding.shimmerPeople
    }


    override fun showProgress() {
        shimmer.showShimmer(true)
    }

    override fun showError(throwable: Throwable) {
        Log.e(Constance.LogTag.PEOPLES, getString(R.string.error), throwable)
        Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
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
