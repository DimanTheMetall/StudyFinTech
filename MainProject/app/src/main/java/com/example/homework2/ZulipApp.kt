package com.example.homework2

import android.app.Application
import androidx.fragment.app.FragmentActivity
import com.example.homework2.di.zulipcomponent.DaggerZulipComponent
import com.example.homework2.di.zulipcomponent.ZulipComponent

class ZulipApp : Application() {

    lateinit var zulipComponent: ZulipComponent

    override fun onCreate() {
        super.onCreate()
        zulipComponent =
            DaggerZulipComponent.factory().create(applicationContext)
    }
}

fun FragmentActivity.zulipApp(): ZulipApp {
    return this.application as ZulipApp
}
