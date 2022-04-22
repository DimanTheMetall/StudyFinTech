package com.example.homework2.di.peoplescomponent

import com.example.homework2.di.zulipcomponent.ZulipComponent
import com.example.homework2.mvp.peoples.PeoplesFragment
import dagger.Component
import javax.inject.Scope

@PeoplesScope
@Component(
    modules = [PeoplesModule::class],
    dependencies = [ZulipComponent::class]
)
interface PeoplesComponent {

    fun inject(peoplesFragment: PeoplesFragment)

    @Component.Factory
    interface Factory {
        fun create(zulipComponent: ZulipComponent): PeoplesComponent
    }

}

@Scope
annotation class PeoplesScope
