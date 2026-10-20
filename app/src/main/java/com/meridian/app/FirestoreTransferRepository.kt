
package com.meridian.app

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirestoreTransferRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun saveTransfer(
        transfer: TransferRequest
    ): String {

        val userId = auth.currentUser?.uid
            ?: throw IllegalStateException(
                "Please sign in before submitting a transfer."
            )

        val document = firestore
            .collection("users")
            .document(userId)
            .collection("transfers")
            .document()

        val transferData = hashMapOf(
            "recipientAccount" to transfer.recipientAccount,
            "recipientName" to transfer.recipientName,
            "amount" to transfer.amount,
            "remarks" to transfer.remarks,
            "createdAt" to FieldValue.serverTimestamp(),
            "status" to "SUBMITTED"
        )

        document.set(transferData).await()

        return document.id
    }

    fun observeTransfers(
        onSuccess: (List<FirestoreTransfer>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {

        val userId = auth.currentUser?.uid
            ?: throw IllegalStateException(
                "Please sign in to view transfer history."
            )

        return firestore
            .collection("users")
            .document(userId)
            .collection("transfers")
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                val transfers = snapshot?.documents
                    ?.map { document ->

                        FirestoreTransfer(
                            id = document.id,
                            recipientAccount =
                                document.getString("recipientAccount")
                                    .orEmpty(),
                            recipientName =
                                document.getString("recipientName")
                                    .orEmpty(),
                            amount =
                                document.getDouble("amount") ?: 0.0,
                            remarks =
                                document.getString("remarks").orEmpty(),
                            status =
                                document.getString("status").orEmpty(),
                            createdAt =
                                document.getTimestamp("createdAt")
                        )
                    }
                    .orEmpty()

                onSuccess(transfers)
            }
    }
}
