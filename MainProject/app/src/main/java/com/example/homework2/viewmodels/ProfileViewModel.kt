package com.example.homework2.viewmodels

import androidx.lifecycle.ViewModel
import com.example.homework2.dataclasses.Member
import com.example.homework2.dataclasses.ResultMember
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ProfileViewModel() : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private var profileList = emptyList<Member>()


    val memberObservable: Observable<ResultMember> get() = profileSubject
    private val profileSubject = BehaviorSubject.create<ResultMember>().apply {
        onNext(ResultMember.Success(profileList)) //Затычка
    }

    fun loadAllUsers(retrofitService: RetrofitService) {
        profileSubject.onNext(ResultMember.Progress)
        val profilesDisposable = retrofitService.getUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                profileSubject.onNext(ResultMember.Success(it.members))
            }, {
                profileSubject.onNext(ResultMember.Error)
            })
        compositeDisposable.add(profilesDisposable)
    }

    fun onSearchProfile(searchText: String) {
        profileSubject.onNext(ResultMember.Progress)
        val d = Observable.timer(2, TimeUnit.SECONDS) //Эмуляция запроса в сеть
            .subscribeOn(Schedulers.io())
            .subscribe {
                val error = Random.nextBoolean() //Эмуляция ошибки
                if (!error) {
                    profileSubject.onNext(
                        com.example.homework2.dataclasses.ResultMember.Success(
                            profileList.filter {
                                it.full_name.contains(
                                    searchText
                                )
                            })
                    )
                } else {
                    profileSubject.onNext(com.example.homework2.dataclasses.ResultMember.Error)
                }
            }
        compositeDisposable.add(d)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
