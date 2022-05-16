package com.example.homework2.uitest

import androidx.test.core.app.ActivityScenario
import com.example.homework2.MainActivity
import com.example.homework2.screens.StreamListScreen
import com.example.homework2.screens.StreamListScreen.streamList
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Test

class RecycleStreamsScreenTest : TestCase() {

    @Test
    fun openAndCheckVisibilityOfTopicsList() = run {
        val checkablePosition = 0
        ActivityScenario.launch(MainActivity::class.java)

        step("Отображается список стримов") {
            streamList.isDisplayed()
        }

        step("Клик на открытие списка топиков") {
            streamList.childAt<StreamListScreen.StreamItem>(0) { click() }
        }

        step("Отображается списко топиков") {
            streamList.childAt<StreamListScreen.StreamItem>(checkablePosition) {
                topicList.isVisible()
            }
        }

        step("Проверка количества элементов") {
            streamList.hasSize(3)
        }
    }
}
