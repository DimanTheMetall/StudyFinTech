package com.example.homework2.di.myprofilecomponent

import com.example.homework2.di.zulipcomponent.ZulipComponent
import com.example.homework2.mvp.myprofile.MyProfileFragment
import dagger.Component
import javax.inject.Scope

@MyProfileScope
@Component(
    modules = [MyProfileModule::class],
    dependencies = [ZulipComponent::class]
)
interface MyProfileComponent {

    fun inject(myProfileFragment: MyProfileFragment)

    @Component.Factory
    interface Factory {
        fun create(zulipComponent: ZulipComponent): MyProfileComponent
    }
}

@Scope
annotation class MyProfileScope
