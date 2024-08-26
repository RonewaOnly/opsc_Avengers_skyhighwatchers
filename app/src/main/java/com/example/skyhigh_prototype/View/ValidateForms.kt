@file:Suppress("PackageName")

package com.example.skyhigh_prototype.View

import android.util.Patterns

class ValidateForms {

    //
    companion object {

        //functions to validate inputs
        //function to validate first name of the user
        fun validateFirstName(firstName: String): String {
            return if (firstName.length < 3) {
                "At least 3 letters"

            } else if (firstName.any { !it.isLetter() }) {
                "Must contain only letters"

            } else {
                ""
            }
        }//end

        //
        // Function to validate the last name
        fun validateLastName(name: String): String {
            return if (name.length < 3) {
                "At least 3 letters"

            } else if (name.any { !it.isLetter() }) {
                "Must contain only letters"

            } else {
                ""

            }
        }//end

        //function to validate email
        fun validateEmail(email: String): String {
            return if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                "Invalid email address."

            } else {
                ""
            }
        }//end


        //function for validating password
        fun validatePassword(password: String): String {
            return if (password.length < 8) {
                "Must contain at least 8 characters"

            } else if (!password.any { it.isDigit() }) {
                "Must contain at least a number"

            } else if (!password.any { it.isLowerCase() }) {
                "Must contain at least a lowercase letter"

            } else if (!password.any { it.isUpperCase() }) {
                "Must contain at least a uppercase letter"

            } else if (!password.any { !it.isLetterOrDigit() }) {
                "Must contain at least special characters"

            } else {
                ""
            }
        }//end

        //function to validate confirm password to check if passwords match
        fun validateConfirmPassword(password: String, confirmPassword: String): String {
            return if (password != confirmPassword) {
                "Passwords do not match."

            } else {
                ""
            }
        }
    }
}