package com.meridian.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * SupportActivity — UI-only. No navigation, no backend, no click handlers.
 * Only inflates its layout, per lab requirement.
 */
class SupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)
    }
}
