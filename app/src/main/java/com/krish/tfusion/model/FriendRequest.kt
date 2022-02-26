package com.krish.tfusion.model

data class FriendRequest(
    var senderUID: String? = null,
    var receiverUID: String? = null,
    var creationTimeMs : String? = null
)
