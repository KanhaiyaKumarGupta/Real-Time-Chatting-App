package com.example.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.whatsappclone.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgetPassword : AppCompatActivity() {
    private lateinit var binding:ActivityForgetPasswordBinding
    private lateinit var firebaseAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forget_password)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.forgetPassword.setOnClickListener{
            val email = binding.email.editText?.text.toString()
            if(email.isEmpty())
            {
                Toast.makeText(this,"Please enter Your email", Toast.LENGTH_LONG).show()
            }
            else
            {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener{task->
                    if(task.isSuccessful)
                    {
                        Toast.makeText(this,"Email sent successfully to reset your email", Toast.LENGTH_LONG).show()
                        val intent = Intent(this,LoginPageActivity::class.java)
                        startActivity(intent)
                    }
                    else
                    {
                        Toast.makeText(this,task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                    }

                }
            }
        }
    }
}