package com.example.homework2.viewmodels

import androidx.lifecycle.ViewModel
import com.example.homework2.dataclasses.Member
import com.example.homework2.dataclasses.ResultMember
import com.example.homework2.dataclasses.chatdataclasses.JsonPresense
import com.example.homework2.dataclasses.chatdataclasses.Presence
import com.example.homework2.dataclasses.chatdataclasses.Website
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class PeoplesViewModel() : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private var profileList = emptyList<Member>()

    val peoplesObservable: Observable<ResultMember> get() = peoplesSubject
    private val peoplesSubject = BehaviorSubject.create<ResultMember>().apply {
        onNext(ResultMember.Success(profileList)) //Затычка
    }

    fun loadAllUsers(retrofitService: RetrofitService) {
        peoplesSubject.onNext(ResultMember.Progress)
        val profilesDisposable = retrofitService.getUsers()
            .subscribeOn(Schedulers.io())
            .flatMapObservable { Observable.fromIterable(it.members) }
            .flatMap { member ->
                retrofitService.getPresence(member.user_id)
                    .onErrorReturnItem(
                        JsonPresense(
                            "msg",
                            Presence(Website("offline", -1)),
                            "result"
                        )
                    )
                    .map { presence ->
                        member.copy(website = presence.presence.website)
                    }.toObservable()
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                peoplesSubject.onNext(ResultMember.Success(it))
            }, {
                peoplesSubject.onNext(ResultMember.Error)
            })

        compositeDisposable.add(profilesDisposable)
    }

    fun onSearchProfile(searchText: String, retrofitService: RetrofitService) {
        peoplesSubject.onNext(ResultMember.Progress)
        val d = retrofitService.getUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Json ->
                peoplesSubject.onNext(
                    ResultMember.Success(Json.members.filter { it.full_name.contains(searchText) })
                )
            }, {
                peoplesSubject.onNext(ResultMember.Error)
            })

        compositeDisposable.add(d)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
