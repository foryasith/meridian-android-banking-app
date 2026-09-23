package com.meridian.app

object TransferValidator {

    /**
     * Returns null if the input is valid,
     * or an error message naming the first problem found.
     */
    fun validate(
        account: String,
        name: String,
        amountText: String
    ): String? {

        if (account.isBlank()) {
            return "Enter a recipient account number"
        }

        if (name.isBlank()) {
            return "Enter a recipient name"
        }

        val amount = amountText.toDoubleOrNull()

        if (amount == null || amount <= 0.0) {
            return "Enter a valid amount greater than 0"
        }

        return null
    }
}