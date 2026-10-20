
package com.meridian.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ListenerRegistration

class HistoryFragment : Fragment() {

    private val firestoreRepository =
        FirestoreTransferRepository()

    private var transferListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_history,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView =
            view.findViewById<RecyclerView>(
                R.id.rvHistory
            )

        val progressBar =
            view.findViewById<ProgressBar>(
                R.id.progressHistory
            )

        val statusText =
            view.findViewById<TextView>(
                R.id.tvHistoryStatus
            )

        val adapter = TransferHistoryAdapter()

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        recyclerView.adapter = adapter

        progressBar.visibility = View.VISIBLE
        statusText.visibility = View.GONE

        try {
            transferListener = firestoreRepository.observeTransfers(
                onSuccess = { transfers ->

                    if (viewLifecycleOwner.lifecycle.currentState ==
                        androidx.lifecycle.Lifecycle.State.DESTROYED
                    ) {
                        return@observeTransfers
                    }

                    progressBar.visibility = View.GONE
                    adapter.updateTransfers(transfers)

                    if (transfers.isEmpty()) {
                        statusText.text =
                            "No transfers found yet."
                        statusText.visibility = View.VISIBLE
                    } else {
                        statusText.visibility = View.GONE
                    }
                },

                onError = { error ->

                    if (viewLifecycleOwner.lifecycle.currentState ==
                        androidx.lifecycle.Lifecycle.State.DESTROYED
                    ) {
                        return@observeTransfers
                    }

                    progressBar.visibility = View.GONE

                    statusText.text =
                        "Unable to load history: ${error.localizedMessage}"

                    statusText.visibility = View.VISIBLE
                }
            )
        } catch (e: Exception) {
            progressBar.visibility = View.GONE
            statusText.text =
                "Unable to load history: ${e.localizedMessage}"
            statusText.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        transferListener?.remove()
        transferListener = null
        super.onDestroyView()
    }
}
