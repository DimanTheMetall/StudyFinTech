package com.example.homework2

object Constants {

    const val myId = 490112
    const val PROFILE_KEY = "Profile key"
    const val ALL_OR_SUBSCRIBED_KEY = "all or subscribed"
    const val TOPIC_KEY = "topic key"
    const val STREAM_KEY = "stream key"

    const val AUTHORIZATION_HEADER = "Authorization"

    const val STREAM = "stream"
    const val TOPIC = "topic"
    const val DBNAME = "zulipDataBase"

    const val MESSAGES_COUNT_PAGINATION = 20
    const val MESSAGE_COUNT_FOR_REQUEST_LOAD = 5
    const val LIMIT_MESSAGE_COUNT_FOR_TOPIC = 50
    const val DOWNLOAD_MESSAGES_PERIOD = 10L
    const val INIT_REFRESHER_DELAY = 3L

    const val NOT_EXIST_TOPIC = "notexist_key44ffrrtaff"

    object Status {
        const val ACTIVE = "active"
        const val IDLE = "idle"
        const val OFFLINE = "offline"
        const val TIME_FOR_ON_ACTIVE_STATUS = 500
    }

    object Anchors {
        const val NEWEST = "newest"
    }

    object LogTag {
        const val TOPIC_AND_STREAM = "TOPIC and STREAM"
        const val MESSAGES_AND_REACTIONS = "MESSAGES and REACTIONS"
        const val PEOPLES = "PEOPLES"
    }

    object LogMessage {
        const val INSERT_ERROR = "INSERT ERROR"

        const val UPDATE_ERROR = "UPDATE ERROR"

        const val DELETE_ERROR = "DELETE ERROR"
    }

    //URL
    const val BASE_URL = "https://tinkoff-android-spring-2022.zulipchat.com/api/v1/"
}
