package com.unwiringapps.earningnapp.model

data class UserModel(
    val uid: String? = null,
    val name: String? = null,
    val imageURL: String? = null,
    val number: String? = null,
    val referCode: String? = "No Refer Code",
    val coins: Double? = 0.0,
    val myReferCode: String? = null,
    val limits: UserLimits = UserLimits(),
    val rankerscoins: Double? = 0.0
)