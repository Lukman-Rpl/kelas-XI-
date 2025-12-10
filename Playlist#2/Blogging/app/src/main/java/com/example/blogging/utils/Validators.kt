// utils/Validators.kt
package com.example.blogging.utils

object Validators {
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordStrong(password: String): Boolean {
        return password.length >= 6
    }
}
