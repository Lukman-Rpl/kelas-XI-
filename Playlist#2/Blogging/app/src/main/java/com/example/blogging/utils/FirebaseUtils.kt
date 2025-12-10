// utils/FirebaseUtils.kt
package com.example.blogging.utils


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage



    object FirebaseUtils {
        fun getCurrentUserId(): String? {
            return FirebaseAuth.getInstance().currentUser?.uid
        }

        fun getDatabaseRef(path: String) = FirebaseDatabase.getInstance().getReference(path)

        fun getStorageRef(path: String) = FirebaseStorage.getInstance().getReference(path)
    }

