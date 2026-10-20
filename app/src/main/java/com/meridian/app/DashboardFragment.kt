
package com.meridian.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                startSessionReminder()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {

            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {

                val uiState by viewModel.uiState
                    .collectAsStateWithLifecycle()

                MaterialTheme {

                    DashboardScreen(
                        uiState = uiState,

                        onTransferClick = {
                            (requireActivity() as MainActivity)
                                .showTransferFragment()
                        },

                        onHistoryClick = {
                            (requireActivity() as MainActivity)
                                .showHistoryFragment()
                        },

                        onSignOutClick = {
                            (requireActivity() as MainActivity)
                                .signOutAndOpenLogin()
                        }
                    )
                }
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.refreshUser()

        setupSessionReminder()
    }

    private fun setupSessionReminder() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            val permissionGranted =
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            if (permissionGranted) {
                startSessionReminder()
            } else {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }

        } else {
            startSessionReminder()
        }
    }

    private fun startSessionReminder() {

        val serviceIntent = Intent(
            requireContext(),
            SessionReminderService::class.java
        )

        requireContext().startService(serviceIntent)
    }
}
