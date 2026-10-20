
package com.meridian.app

import com.google.firebase.Timestamp

data class FirestoreTransfer(
    val id: String = "",
    val recipientAccount: String = "",
    val recipientName: String = "",
    val amount: Double = 0.0,
    val remarks: String = "",
    val status: String = "",
    val createdAt: Timestamp? = null
)
