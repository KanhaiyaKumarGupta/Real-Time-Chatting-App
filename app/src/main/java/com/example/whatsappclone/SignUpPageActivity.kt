package com.example.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.whatsappclone.databinding.ActivitySignUpPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignUpPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up_page)
        supportActionBar?.hide()
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            Toast.makeText(this, "User already logged in", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.signUp.setOnClickListener {
            val name = binding.userName.editText?.text.toString()
            val email = binding.email.editText?.text.toString()
            val password = binding.password.editText?.text.toString()
            val confirmPassword = binding.confirmPassword.editText?.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter Your Name", Toast.LENGTH_LONG).show()
            } else if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your Email", Toast.LENGTH_LONG).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_LONG).show()
            } else if (password.length < 8) {
                Toast.makeText(this, "Minimum password length should be 8", Toast.LENGTH_LONG)
                    .show()
            } else {
                val hasNumeric = password.any { it.isDigit() }
                val specialCharacters = "!@#$%^&*()_-+=<>?/[]{},.:;|"
                val hasSpecialCharacter = password.any { specialCharacters.contains(it) }

                if (!hasNumeric) {
                    Toast.makeText(
                        this,
                        "Password should contain at least 1 numeric character",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (!hasSpecialCharacter) {
                    Toast.makeText(
                        this,
                        "Password should contain at least 1 special character",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Please Enter your confirm password", Toast.LENGTH_LONG)
                        .show()
                } else if (password != confirmPassword) {
                    Toast.makeText(
                        this,
                        "Password and confirm Password do not match",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    // Perform signup
                    signup(name, email, password)
                }
            }
        }
        binding.goToLoginBtn.setOnClickListener{
            startActivity(Intent(this,LoginPageActivity::class.java))
        }

    }

    private fun signup(name: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this)
        { task ->
            if (task.isSuccessful) {

                addUserToDatabase(name,email,firebaseAuth.currentUser?.uid!!)
                Toast.makeText(this, "Signup Successfully", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                val errorMessage = when (task.exception) {
                    is FirebaseAuthWeakPasswordException -> "Weak password. Password should be at least 8 characters."
                    is FirebaseAuthInvalidCredentialsException -> "Invalid email address."
                    is FirebaseAuthUserCollisionException -> "This email is already registered."
                    else -> "Signup failed. Please try again."
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
          databaseReference = FirebaseDatabase.getInstance().getReference()
          databaseReference.child("user").child(uid).setValue(User(name,email,uid))
    }
}

