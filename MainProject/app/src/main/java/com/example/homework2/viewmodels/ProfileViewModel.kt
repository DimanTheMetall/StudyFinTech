package com.example.homework2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.homework2.dataclasses.Profile
import com.example.homework2.dataclasses.ResultChannel
import com.example.homework2.dataclasses.ResultProfile
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ProfileViewModel() : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private var profileList =
        listOf(
            Profile("Name 1", 1, "emailemail1@email.email", true),
            Profile("Name 2", 1, "emailemail2@email.email", true),
            Profile("Name 3", 1, "emailemail3@email.email", false),
            Profile("Name 4", 1, "emailemail4@email.email", true),
            Profile("Name 5", 1, "emailemail5@email.email", false),
            Profile("Name 6", 1, "emailemail6@email.email", true)
        )

    val profileObservable: Observable<ResultProfile> get() = profileSubject
    private val profileSubject = BehaviorSubject.create<ResultProfile>().apply {
        onNext(ResultProfile.Success(profileList)) //Затычка
    }

    fun onSearchProfile(searchText: String) {
        profileSubject.onNext(ResultProfile.Progress)
        val d = Observable.timer(2, TimeUnit.SECONDS) //Эмуляция запроса в сеть
            .subscribeOn(Schedulers.io())
            .subscribe {
                val error = Random.nextBoolean() //Эмуляция ошибки
                if (!error) {
                    profileSubject.onNext(
                        com.example.homework2.dataclasses.ResultProfile.Success(
                            profileList.filter {
                                it.name.contains(
                                    searchText
                                )
                            })
                    )
                } else {
                    profileSubject.onNext(com.example.homework2.dataclasses.ResultProfile.Error)
                }
            }
        compositeDisposable.add(d)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
