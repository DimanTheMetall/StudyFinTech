package com.example.homework2.di.otherprofilecomponent

import com.example.homework2.di.zulipcomponent.ZulipComponent
import com.example.homework2.mvp.otherprofile.OtherProfileFragment
import dagger.Component
import javax.inject.Scope

@OtherProfileScope
@Component(
    modules = [OtherProfileModule::class],
    dependencies = [ZulipComponent::class]
)
interface OtherProfileComponent {

    fun inject(otherProfileFragment: OtherProfileFragment)

    @Component.Factory
    interface Factory {
        fun create(zulipComponent: ZulipComponent): OtherProfileComponent
    }
}

@Scope
annotation class OtherProfileScope
