package com.example.homework2.dataclasses

data class Reaction(
    var emoji: String,
    var count: Int,
    val userId: Int = 0
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Reaction

        if (emoji != other.emoji) return false
        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = emoji.hashCode()
        result = 31 * result + userId
        return result
    }
}
