package com.example.homework2.viewmodels

import android.database.Observable
import androidx.lifecycle.ViewModel
import com.example.homework2.dataclasses.Member
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class MyProfileViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val subjectProfile = BehaviorSubject.create<Member>()

    fun loadMyProfile(retrofitService: RetrofitService) {

        val myProfileDisposalbe = retrofitService.getOwnUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                subjectProfile.onNext(it)
            }, {

            })
        compositeDisposable.add(myProfileDisposalbe)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


}