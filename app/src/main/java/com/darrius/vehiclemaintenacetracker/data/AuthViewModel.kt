package com.darrius.vehiclemaintenacetracker.data

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.darrius.vehiclemaintenacetracker.models.User
import com.darrius.vehiclemaintenacetracker.navigation.ROUT_HOME
import com.darrius.vehiclemaintenacetracker.navigation.ROUT_ONBOARDING
import com.darrius.vehiclemaintenacetracker.navigation.ROUT_REGISTER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel(var navController: NavController, var context: Context) : ViewModel() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signup(username: String, email: String, password: String, confirmpassword: String) {

        if (email.isBlank() || password.isBlank() || confirmpassword.isBlank()) {
            Toast.makeText(context, "Please email and password cannot be blank", Toast.LENGTH_LONG).show()
        } else if (password != confirmpassword) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_LONG).show()
        } else {

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {

                    val uid = mAuth.currentUser!!.uid
                    val role = "user"

                    // ✅ Using your own User data class
                    val userdata = User(
                        username = username,
                        email = email,
                        password = password,
                        uid = uid,
                        role = role
                    )

                    val regRef = FirebaseDatabase.getInstance().getReference("Users/$uid")

                    regRef.setValue(userdata).addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            Toast.makeText(context, "Registered Successfully", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUT_HOME) // ✅ Navigate after success
                        } else {
                            Toast.makeText(context, "${result.exception!!.message}", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUT_REGISTER)
                        }
                    }

                } else {
                    Toast.makeText(context, "${it.exception?.message}", Toast.LENGTH_LONG).show()
                    navController.navigate(ROUT_REGISTER)
                }
            }
        }
    }

    fun login(email: String, password: String) {

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Please email and password cannot be blank", Toast.LENGTH_LONG).show()
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val uid = mAuth.currentUser!!.uid
                    val userRef = FirebaseDatabase.getInstance().getReference("Users/$uid")

                    userRef.get().addOnSuccessListener { snapshot ->
                        val role = snapshot.child("role").value?.toString() ?: "user"

                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()

                        if (role == "admin") {
                            navController.navigate(ROUT_ONBOARDING)
                        } else {
                            navController.navigate(ROUT_HOME)
                        }

                    }.addOnFailureListener {
                        Toast.makeText(context, "Failed to fetch user role", Toast.LENGTH_SHORT).show()
                        navController.navigate(ROUT_HOME)
                    }

                } else {
                    Toast.makeText(context, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun logout() {
        mAuth.signOut()
        navController.navigate(ROUT_HOME)
    }

    fun isLoggedIn(): Boolean = mAuth.currentUser != null
}