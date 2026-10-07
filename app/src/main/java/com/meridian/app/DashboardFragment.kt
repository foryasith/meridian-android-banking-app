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

    /*
     * DashboardViewModel owns the state used by
     * the Compose dashboard screen.
     */
    private val viewModel: DashboardViewModel by viewModels()

    /*
     * Android 13+ notification permission launcher.
     *
     * If permission is granted, the existing
     * SessionReminderService is started.
     */
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

        /*
         * Lab 09:
         * The Dashboard no longer inflates
         * fragment_dashboard.xml.
         *
         * ComposeView is used instead.
         */
        return ComposeView(requireContext()).apply {

            /*
             * Dispose the Compose composition when
             * the Fragment's view lifecycle is destroyed.
             */
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {

                /*
                 * Observe StateFlow in a lifecycle-aware way.
                 */
                val uiState by viewModel.uiState
                    .collectAsStateWithLifecycle()

                MaterialTheme {

                    DashboardScreen(
                        uiState = uiState,

                        /*
                         * Existing Transfer navigation
                         * is preserved using a callback.
                         */
                        onTransferClick = {
                            (requireActivity() as MainActivity)
                                .showTransferFragment()
                        },

                        /*
                         * Existing History navigation
                         * is preserved using a callback.
                         */
                        onHistoryClick = {
                            (requireActivity() as MainActivity)
                                .showHistoryFragment()
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

        super.onViewCreated(
            view,
            savedInstanceState
        )

        /*
         * ------------------------------------------
         * LAB 06
         * Session Reminder Service
         * ------------------------------------------
         *
         * This functionality is preserved after
         * migrating the Dashboard to Compose.
         */
        setupSessionReminder()
    }

    /*
     * =========================================================
     * SESSION REMINDER
     * =========================================================
     */

    private fun setupSessionReminder() {

        /*
         * Android 13 / API 33 and above requires
         * POST_NOTIFICATIONS runtime permission.
         */
        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            val permissionGranted =
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            if (permissionGranted) {

                /*
                 * Permission was already granted.
                 */
                startSessionReminder()

            } else {

                /*
                 * Ask the user for notification permission.
                 */
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }

        } else {

            /*
             * Android 12 and below do not require
             * runtime notification permission.
             */
            startSessionReminder()
        }
    }

    /*
     * Start the existing SessionReminderService.
     *
     * The service waits and posts the
     * session reminder notification.
     */
    private fun startSessionReminder() {

        val serviceIntent =
            Intent(
                requireContext(),
                SessionReminderService::class.java
            )

        requireContext().startService(
            serviceIntent
        )
    }
}