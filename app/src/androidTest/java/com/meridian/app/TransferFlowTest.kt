package com.meridian.app

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TransferFlowTest {

    @Test
    fun validTransferShowsConfirmationScreen() {

        ActivityScenario.launch(MainActivity::class.java).use {

            onView(withId(R.id.btnGoToTransfer))
                .perform(click())

            onView(withId(R.id.etRecipientAccount))
                .perform(
                    replaceText("8001234567"),
                    closeSoftKeyboard()
                )

            onView(withId(R.id.etRecipientName))
                .perform(
                    replaceText("Kasun Silva"),
                    closeSoftKeyboard()
                )

            onView(withId(R.id.etAmount))
                .perform(
                    replaceText("2500"),
                    closeSoftKeyboard()
                )

            onView(withId(R.id.btnSubmit))
                .perform(click())

            onView(withId(R.id.tvConfirmRecipient))
                .check(matches(isDisplayed()))

            onView(withId(R.id.tvConfirmAmount))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun invalidAmountKeepsUserOnForm() {

        ActivityScenario.launch(MainActivity::class.java).use {

            onView(withId(R.id.btnGoToTransfer))
                .perform(click())

            onView(withId(R.id.etRecipientAccount))
                .perform(
                    replaceText("8001234567"),
                    closeSoftKeyboard()
                )

            onView(withId(R.id.etRecipientName))
                .perform(
                    replaceText("Kasun Silva"),
                    closeSoftKeyboard()
                )

            onView(withId(R.id.etAmount))
                .perform(
                    replaceText("0"),
                    closeSoftKeyboard()
                )

            onView(withId(R.id.btnSubmit))
                .perform(click())

            // User should remain on Transfer form
            onView(withId(R.id.etAmount))
                .check(matches(isDisplayed()))

            // Correct validation error should be displayed
            onView(withId(R.id.etAmount))
                .check(
                    matches(
                        hasErrorText(
                            "Enter a valid amount greater than 0"
                        )
                    )
                )
        }
    }
}