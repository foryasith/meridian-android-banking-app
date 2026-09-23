package com.meridian.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SessionReminderService : LifecycleService() {

    companion object {

        const val CHANNEL_ID =
            "session_reminder_channel"

        const val NOTIFICATION_ID =
            1001

        const val ACTION_CANCEL_REMINDER =
            "com.meridian.app.ACTION_CANCEL_REMINDER"

        const val ACTION_SESSION_EXPIRED =
            "com.meridian.app.SESSION_EXPIRED"
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        super.onStartCommand(
            intent,
            flags,
            startId
        )

        // Handle Cancel notification action
        if (
            intent?.action ==
            ACTION_CANCEL_REMINDER
        ) {

            NotificationManagerCompat
                .from(this)
                .cancel(NOTIFICATION_ID)

            stopSelf()

            return START_NOT_STICKY
        }

        lifecycleScope.launch {

            // Required Lab 06 delay
            delay(30_000)

            // Only show reminder.
            // DO NOT automatically logout here.
            postReminderNotification()

            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Session Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {

                    description =
                        "Warns before the banking session expires"
                }

            val manager =
                getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(
                channel
            )
        }
    }

    private fun postReminderNotification() {

        /*
         * Cancel action
         */
        val cancelIntent =
            Intent(
                this,
                SessionReminderService::class.java
            ).apply {

                action =
                    ACTION_CANCEL_REMINDER
            }

        val cancelPendingIntent =
            PendingIntent.getService(
                this,
                100,
                cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )


        /*
         * Optional challenge:
         * Send session-expired broadcast only
         * when the user explicitly taps Sign Out.
         */
        val signOutIntent =
            Intent(
                ACTION_SESSION_EXPIRED
            ).apply {

                setPackage(packageName)
            }

        val signOutPendingIntent =
            PendingIntent.getBroadcast(
                this,
                101,
                signOutIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )


        val notification =
            NotificationCompat.Builder(
                this,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_logo_arrow
                )
                .setContentTitle(
                    "Session expiring soon"
                )
                .setContentText(
                    "Your banking session may expire soon."
                )
                .setPriority(
                    NotificationCompat.PRIORITY_DEFAULT
                )
                .setAutoCancel(true)

                // Challenge 1
                .addAction(
                    R.drawable.ic_logo_arrow,
                    "Cancel",
                    cancelPendingIntent
                )

                // Challenge 2
                .addAction(
                    R.drawable.ic_logo_arrow,
                    "Sign Out",
                    signOutPendingIntent
                )

                .build()


        val hasPermission =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED


        if (hasPermission) {

            NotificationManagerCompat
                .from(this)
                .notify(
                    NOTIFICATION_ID,
                    notification
                )
        }
    }
}