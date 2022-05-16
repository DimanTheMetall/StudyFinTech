package com.example.homework2.dataclasses.chatdataclasses

import com.google.gson.annotations.SerializedName

data class Reaction(
    @SerializedName("emoji_code")
    var emojiCode: String,

    @SerializedName("emoji_name")
    val emojiName: String,

    @SerializedName("user_id")
    val userId: Int = 0,

    @SerializedName("reaction_type")
    val reactionType: String,

    ) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Reaction

        if (emojiCode != other.emojiCode) return false
        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = emojiCode.hashCode()
        result = 31 * result + userId
        return result
    }
}
