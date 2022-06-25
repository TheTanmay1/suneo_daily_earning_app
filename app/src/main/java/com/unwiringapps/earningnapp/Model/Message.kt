package com.unwiringapps.earningnapp.model

import com.google.firebase.Timestamp

data class Message(
    var title: String? = null,
    var content: String? = null,
    var time: Timestamp? = null
) {
    fun isDataValid(): Boolean {
        return content != null && time != null
    }

    companion object {
        fun isValidMessage(message: Message?): Boolean {
            return (message != null && message.isDataValid())
        }
    }
}
