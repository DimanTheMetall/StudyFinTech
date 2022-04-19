package com.example.homework2

object Constance {
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

    object Status {
        const val ACTIVE = "active"
        const val IDLE = "idle"
        const val OFFLINE = "offline"
        const val TIME_FOR_ON_ACTIVE_STATUS = 500
    }

    object Anchors {
        const val NEWEST = "newest"
        const val OLDEST = "oldest"
    }

    object Log {
        const val TOPIC_AND_STREAM = "TOPIC and STREAM"
        const val MESSAGES_AND_REACTIONS = "MESSAGES and REACTIONS"
    }

    //URL
    const val BASE_URL = "https://tinkoff-android-spring-2022.zulipchat.com/api/v1/"
}
