package com.meridian.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SessionExpiredReceiver :
    BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        if (
            intent.action ==
            SessionReminderService.ACTION_SESSION_EXPIRED
        ) {

            val loginIntent =
                Intent(
                    context,
                    LoginActivity::class.java
                ).apply {

                    flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

            context.startActivity(
                loginIntent
            )
        }
    }
}