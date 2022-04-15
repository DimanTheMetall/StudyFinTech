package com.example.homework2.mvp.peoples

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework2.R
import com.example.homework2.adapters.PeopleAdapter
import com.example.homework2.databinding.FragmentPeopleBinding
import com.example.homework2.dataclasses.Member
import com.example.homework2.fragments.OtherProfileFragment
import com.example.homework2.mvp.BaseFragment
import com.example.homework2.zulipApp
import com.facebook.shimmer.ShimmerFrameLayout

class PeoplesFragment : BaseFragment<PeoplesPresenter, FragmentPeopleBinding>(), PeoplesView {

    private val recycleAdapter = PeopleAdapter { member -> openProfileFrag(member) }
    private lateinit var shimmer: ShimmerFrameLayout

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

    override fun configureRecycleAdapter() {
        binding.recyclePeople.adapter = recycleAdapter
        binding.recyclePeople.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
    }

    override fun initSearchedTextListener() {
        binding.searchUsers.addTextChangedListener { text ->
            presenter.onSearchedTextChanged(text.toString())
        }
    }

    override fun initShimmer() {
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
