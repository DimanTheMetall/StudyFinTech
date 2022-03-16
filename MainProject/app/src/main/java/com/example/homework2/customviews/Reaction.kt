package com.example.homework2.customviews

data class Reaction(
    var emoji: String,
    var count: Int,
    val userId: Int = 0
)
