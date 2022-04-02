package com.example.homework2.dataclasses.chatdataclasses

import com.google.gson.Gson

data class Narrow(val narrow: List<Filter>)

fun Narrow.toJson(): String{
    return Gson().toJson(narrow)
}

data class Filter(
    val operator: String,
    val operand: String,
)
