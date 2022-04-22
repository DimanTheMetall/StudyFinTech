package com.example.homework2.di.chatcomponent

import com.example.homework2.di.zulipcomponent.ZulipComponent
import com.example.homework2.mvp.chat.ChatFragment
import dagger.Component
import javax.inject.Scope

@ChatScope
@Component(
    modules = [ChatModule::class],
    dependencies = [ZulipComponent::class]
)
interface ChatComponent {

    fun inject(chatFragment: ChatFragment)

    @Component.Factory
    interface Factory {
        fun create(zulipComponent: ZulipComponent): ChatComponent
    }
}

@Scope
annotation class ChatScope
