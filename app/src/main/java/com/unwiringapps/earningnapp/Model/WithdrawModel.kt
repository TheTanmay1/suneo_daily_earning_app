package com.unwiringapps.earningnapp.model

data class WithdrawModel(
    val name: String = "",
    val number: String = "",
    val coins: Int = 0,
    val authid: String = "",
    val status: String = "",
    val paymentMode: String = "",
    val timeStemp: String = ""
)
