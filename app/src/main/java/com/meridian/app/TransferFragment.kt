package com.meridian.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import com.meridian.app.databinding.FragmentTransferBinding

class TransferFragment : Fragment() {

    private var _binding: FragmentTransferBinding? = null

    private val binding
        get() = _binding!!

    companion object {

        private const val MAX_TRANSFER_AMOUNT = 500000.0

        private const val PREFS_NAME =
            "banking_app_prefs"

        private const val KEY_LAST_RECIPIENT_ACCOUNT =
            "last_recipient_account"

        private const val KEY_LAST_RECIPIENT_NAME =
            "last_recipient_name"
    }

    /*     * Challenge Exercise:
     * Runtime permission launcher for READ_CONTACTS     */
    private val contactsPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                loadContacts()

            }
        }


    /*     * Create Fragment View     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentTransferBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }


    /* * Fragment View Setup     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )


        /*
         * Submit / Review Transfer
         */
        binding.btnSubmit.setOnClickListener {

            onSubmitTransfer()
        }


        /*
         * Cancel Transfer
         */
        binding.btnCancel.setOnClickListener {

            requireActivity()
                .supportFragmentManager
                .popBackStack()
        }


        /*
         * Back Button
         */
        binding.btnBack.setOnClickListener {

            requireActivity()
                .supportFragmentManager
                .popBackStack()
        }


        /*         * LAB 06:
         * Load the last-used recipient
         */

        loadLastRecipient()


        /*
         * CHALLENGE EXERCISE:
         * Load device contacts
         */

        checkContactsPermission()
    }


    /*     * SHARED PREFERENCES     *
     * Reads the recipient saved by ConfirmationFragment.
     */
    private fun loadLastRecipient() {

        val prefs =
            requireContext()
                .getSharedPreferences(
                    PREFS_NAME,
                    Context.MODE_PRIVATE
                )

        val lastRecipientAccount =
            prefs.getString(
                KEY_LAST_RECIPIENT_ACCOUNT,
                ""
            ) ?: ""

        val lastRecipientName =
            prefs.getString(
                KEY_LAST_RECIPIENT_NAME,
                ""
            ) ?: ""


        /*
         * Only recipient information is restored.
         *
         * Amount and remarks remain empty.
         */
        binding.etRecipientAccount
            .setText(
                lastRecipientAccount
            )

        binding.etRecipientName
            .setText(
                lastRecipientName
            )
    }


    /*     * CONTACTS PERMISSION     */
    private fun checkContactsPermission() {

        val permissionStatus =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            )

        if (
            permissionStatus ==
            PackageManager.PERMISSION_GRANTED
        ) {

            /*
             * Permission already granted.
             */
            loadContacts()

        } else {

            /*
             * Request contacts permission.
             */
            contactsPermissionLauncher.launch(
                Manifest.permission.READ_CONTACTS
            )
        }
    }


    /*     * CONTENT RESOLVER     *
     * Reads contact names from the device and adds them
     * to the recipient-name AutoCompleteTextView.
     */
    private fun loadContacts() {

        /*
         * Safety check before accessing contacts.
         */
        if (
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }


        val contactNames =
            mutableListOf<String>()


        /*
         * Query the Android Contacts Provider
         * through ContentResolver.
         */
        val cursor =
            requireContext()
                .contentResolver
                .query(
                    ContactsContract
                        .Contacts
                        .CONTENT_URI,

                    arrayOf(
                        ContactsContract
                            .Contacts
                            .DISPLAY_NAME_PRIMARY
                    ),

                    null,
                    null,

                    ContactsContract
                        .Contacts
                        .DISPLAY_NAME_PRIMARY +
                            " ASC"
                )


        /*
         * cursor.use automatically closes the Cursor.
         */
        cursor?.use {

            val nameIndex =
                it.getColumnIndex(
                    ContactsContract
                        .Contacts
                        .DISPLAY_NAME_PRIMARY
                )


            while (
                it.moveToNext()
            ) {

                if (
                    nameIndex >= 0
                ) {

                    val contactName =
                        it.getString(
                            nameIndex
                        )


                    if (
                        !contactName.isNullOrBlank()
                    ) {

                        contactNames.add(
                            contactName
                        )
                    }
                }
            }
        }


        /*
         * Remove duplicate contact names.
         */
        val uniqueContactNames =
            contactNames
                .distinct()


        /*
         * Create suggestion adapter.
         */
        val contactAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout
                    .simple_dropdown_item_1line,
                uniqueContactNames
            )


        /*
         * Attach suggestions to recipient name field.
         *
         * etRecipientName must be an AutoCompleteTextView
         * in fragment_transfer.xml.
         */
        binding.etRecipientName
            .setAdapter(
                contactAdapter
            )


        /*
         * Start showing suggestions after
         * the user types one character.
         */
        binding.etRecipientName.threshold = 1
    }


    /*     * TRANSFER VALIDATION     */
    private fun onSubmitTransfer() {

        /*
         * Clear previous validation errors.
         */
        binding.etRecipientAccount.error = null
        binding.etRecipientName.error = null
        binding.etAmount.error = null


        /*
         * Read entered values.
         */
        val account =
            binding.etRecipientAccount
                .text
                .toString()
                .trim()

        val name =
            binding.etRecipientName
                .text
                .toString()
                .trim()

        val amountText =
            binding.etAmount
                .text
                .toString()
                .trim()

        val remarks =
            binding.etRemarks
                .text
                .toString()
                .trim()


        /*         * Recipient Account Validation         */
        if (
            account.isEmpty()
        ) {

            binding.etRecipientAccount.error =
                "Enter a recipient account number"

            binding.etRecipientAccount
                .requestFocus()

            return
        }

        /*         * Recipient Name Validation         */
        if (
            name.isEmpty()
        ) {

            binding.etRecipientName.error =
                "Enter a recipient name"

            binding.etRecipientName
                .requestFocus()

            return
        }

        /*         * Amount Validation         */

        val amount =
            amountText.toDoubleOrNull()


        if (
            amount == null ||
            amount <= 0.0
        ) {

            binding.etAmount.error =
                "Enter a valid amount greater than 0"

            binding.etAmount
                .requestFocus()

            return
        }

        /*         * Maximum Transfer Limit         */
        if (
            amount > MAX_TRANSFER_AMOUNT
        ) {

            binding.etAmount.error =
                "Maximum transfer amount is LKR 500,000"

            binding.etAmount
                .requestFocus()

            return
        }


        /*         * Create TransferRequest         */

        val request =
            TransferRequest(
                recipientAccount = account,
                recipientName = name,
                amount = amount,
                remarks = remarks
            )

        /*
         * Navigate to ConfirmationFragment.
         */
        (requireActivity() as MainActivity)
            .showConfirmationFragment(
                request
            )
    }

    /*     * CLEAN UP VIEW BINDING     */
    override fun onDestroyView() {

        super.onDestroyView()

        /*
         * Fragment can outlive its View.
         *
         * Setting the binding reference to null prevents
         * the Fragment from retaining the destroyed View.
         */
        _binding = null
    }
}