package com.meridian.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import com.google.android.material.switchmaterial.SwitchMaterial

class DashboardFragment : Fragment() {

    companion object {

        private const val PREFS_NAME =
            "banking_app_prefs"

        private const val KEY_DARK_MODE =
            "dark_mode_enabled"
    }

    /*
     * Android 13+ notification permission launcher.
     *
     * If the user grants permission, start the
     * SessionReminderService.
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

        return inflater.inflate(
            R.layout.fragment_dashboard,
            container,
            false
        )
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
         * LAB 05
         * Fragment Navigation
         * ------------------------------------------
         */

        // Transfer quick action
        view.findViewById<View>(
            R.id.btnGoToTransfer
        ).setOnClickListener {

            (requireActivity() as MainActivity)
                .showTransferFragment()
        }

        // History quick action
        view.findViewById<View>(
            R.id.btnGoToHistory
        ).setOnClickListener {

            (requireActivity() as MainActivity)
                .showHistoryFragment()
        }

        // "See all" also opens History
        view.findViewById<View?>(
            R.id.btnSeeAllHistory
        )?.setOnClickListener {

            (requireActivity() as MainActivity)
                .showHistoryFragment()
        }


        /*
         * ------------------------------------------
         * LAB 06
         * Session Reminder Service
         * ------------------------------------------
         */

        setupSessionReminder()


        /*
         * ------------------------------------------
         * LAB 06 OPTIONAL CHALLENGE
         * Dark Mode SharedPreferences
         * ------------------------------------------
         */

        setupDarkMode(view)
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
                 * Ask the user for permission.
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
     * Start the LifecycleService.
     *
     * The service waits 30 seconds and then
     * posts the session reminder notification.
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


    /*
     * =========================================================
     * DARK MODE
     * =========================================================
     */

    private fun setupDarkMode(
        view: View
    ) {

        val prefs =
            requireContext()
                .getSharedPreferences(
                    PREFS_NAME,
                    Context.MODE_PRIVATE
                )

        val darkModeSwitch =
            view.findViewById<SwitchMaterial>(
                R.id.switchDarkMode
            )

        /*
         * Get previously saved preference.
         */
        val darkModeEnabled =
            prefs.getBoolean(
                KEY_DARK_MODE,
                false
            )

        /*
         * Display the saved state in the switch.
         */
        darkModeSwitch.isChecked =
            darkModeEnabled


        /*
         * Save and apply the theme whenever
         * the switch changes.
         */
        darkModeSwitch
            .setOnCheckedChangeListener {
                    _,
                    isChecked ->

                /*
                 * Save preference.
                 */
                prefs.edit()
                    .putBoolean(
                        KEY_DARK_MODE,
                        isChecked
                    )
                    .apply()


                /*
                 * Apply theme.
                 */
                if (isChecked) {

                    AppCompatDelegate
                        .setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_YES
                        )

                } else {

                    AppCompatDelegate
                        .setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_NO
                        )
                }
            }
    }
}