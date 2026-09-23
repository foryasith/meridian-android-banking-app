package com.meridian.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

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

        recyclerView.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        lifecycleScope.launch {

            val transfers =
                AppDatabase
                    .getInstance(requireContext())
                    .transferDao()
                    .getAll()

            recyclerView.adapter =
                TransferHistoryAdapter(
                    transfers
                )
        }
    }
}