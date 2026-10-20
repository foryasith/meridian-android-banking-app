
package com.meridian.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Locale

class ConfirmationFragment : Fragment() {

    companion object {
        const val ARG_TRANSFER_REQUEST = "transfer_request"
    }

    private lateinit var transferRequest: TransferRequest

    private val firestoreRepository =
        FirestoreTransferRepository()

    private var isSubmitting = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_confirmation,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        transferRequest = BundleCompat.getSerializable(
            requireArguments(),
            ARG_TRANSFER_REQUEST,
            TransferRequest::class.java
        )!!

        val formattedAmount = String.format(
            Locale.US,
            "LKR %,.2f",
            transferRequest.amount
        )

        view.findViewById<TextView>(
            R.id.tvConfirmRecipient
        ).text = "${transferRequest.recipientName} " +
                "(${transferRequest.recipientAccount})"

        view.findViewById<TextView>(
            R.id.tvConfirmAmount
        ).text = formattedAmount

        view.findViewById<TextView>(
            R.id.tvConfirmRemarks
        ).text = transferRequest.remarks.ifEmpty {
            "No remarks"
        }

        val confirmButton = view.findViewById<Button>(
            R.id.btnConfirm
        )

        val editButton = view.findViewById<Button>(
            R.id.btnEditTransfer
        )

        editButton.setOnClickListener {
            requireActivity()
                .supportFragmentManager
                .popBackStack()
        }

        confirmButton.setOnClickListener {

            if (isSubmitting) {
                return@setOnClickListener
            }

            isSubmitting = true
            confirmButton.isEnabled = false
            editButton.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {

                try {

                    // 1. Save the demo transfer to Firestore.
                    firestoreRepository.saveTransfer(
                        transferRequest
                    )

                    // 2. Preserve existing Room history.
                    AppDatabase
                        .getInstance(requireContext())
                        .transferDao()
                        .insert(transferRequest)

                    // 3. Save last-used recipient.
                    val prefs = requireContext()
                        .getSharedPreferences(
                            "banking_app_prefs",
                            Context.MODE_PRIVATE
                        )

                    prefs.edit()
                        .putString(
                            "last_recipient_account",
                            transferRequest.recipientAccount
                        )
                        .putString(
                            "last_recipient_name",
                            transferRequest.recipientName
                        )
                        .apply()

                    // 4. Show success message.
                    Toast.makeText(
                        requireContext(),
                        "Transfer record saved successfully",
                        Toast.LENGTH_LONG
                    ).show()

                    // 5. Return to Dashboard.
                    (requireActivity() as MainActivity)
                        .showDashboardFragment()

                } catch (e: Exception) {

                    Toast.makeText(
                        requireContext(),
                        "Unable to save transfer: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()

                    isSubmitting = false
                    confirmButton.isEnabled = true
                    editButton.isEnabled = true
                }
            }
        }
    }
}
