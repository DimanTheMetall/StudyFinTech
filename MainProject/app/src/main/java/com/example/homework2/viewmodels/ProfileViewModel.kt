package com.example.homework2.viewmodels

import androidx.lifecycle.ViewModel
import com.example.homework2.dataclasses.Member
import com.example.homework2.dataclasses.chatdataclasses.Presence
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

open class ProfileViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    val myProfileObservable: Observable<Member> get() = subjectMyProfile
    private val subjectMyProfile = BehaviorSubject.create<Member>()

    val otherProfilePresenceObservable: Observable<Presence> get() = subjectOtherProfilePresence
    private val subjectOtherProfilePresence = BehaviorSubject.create<Presence>()

    fun loadMyProfile(retrofitService: RetrofitService) {

        val myProfileDisposable = retrofitService.getOwnUser()
            .subscribeOn(Schedulers.io())
            .flatMap { member ->
                retrofitService.getPresence(member.email)
                    .flatMap { Single.just(member.copy(website = it.presence.website)) }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                subjectMyProfile.onNext(it)
            }, {

            })
        compositeDisposable.add(myProfileDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun loadOtherProfile(retrofitService: RetrofitService, member: Member) {
        val profileDisposable = retrofitService.getPresence(member.email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                subjectOtherProfilePresence.onNext(it.presence)
            }, {

            })

        compositeDisposable.add(profileDisposable)
    }

}
