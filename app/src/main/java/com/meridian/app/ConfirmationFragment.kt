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

        // Get TransferRequest passed from TransferFragment
        transferRequest =
            BundleCompat.getSerializable(
                requireArguments(),
                ARG_TRANSFER_REQUEST,
                TransferRequest::class.java
            )!!

        // Format amount for display
        val formattedAmount = String.format(
            Locale.US,
            "LKR %,.2f",
            transferRequest.amount
        )

        // Display recipient details
        view.findViewById<TextView>(
            R.id.tvConfirmRecipient
        ).text =
            "${transferRequest.recipientName} " +
                    "(${transferRequest.recipientAccount})"

        // Display amount
        view.findViewById<TextView>(
            R.id.tvConfirmAmount
        ).text = formattedAmount

        // Display remarks
        view.findViewById<TextView>(
            R.id.tvConfirmRemarks
        ).text =
            transferRequest.remarks.ifEmpty {
                "No remarks"
            }

        // Edit transfer
        view.findViewById<Button>(
            R.id.btnEditTransfer
        ).setOnClickListener {

            requireActivity()
                .supportFragmentManager
                .popBackStack()
        }

        // Confirm transfer
        view.findViewById<Button>(
            R.id.btnConfirm
        ).setOnClickListener {

            lifecycleScope.launch {

                // 1. Save transfer into Room database
                AppDatabase
                    .getInstance(requireContext())
                    .transferDao()
                    .insert(transferRequest)

                // 2. Save last-used recipient using SharedPreferences
                val prefs =
                    requireContext().getSharedPreferences(
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

                // 3. Show confirmation message
                Toast.makeText(
                    requireContext(),
                    "Transfer submitted",
                    Toast.LENGTH_LONG
                ).show()

                // 4. Return to Dashboard
                (requireActivity() as MainActivity)
                    .showDashboardFragment()
            }
        }
    }
}