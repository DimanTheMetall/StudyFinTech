package com.example.homework2.dataclasses

data class Reaction(
    var emoji_code: String,
    val emoji_name: String,
    val user_id: Int = 0,
    val reaction_type: String

) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Reaction

        if (emoji_code != other.emoji_code) return false
        if (user_id != other.user_id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = emoji_code.hashCode()
        result = 31 * result + user_id
        return result
    }
}
