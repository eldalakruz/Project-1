package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var submitbtn : Button
    private lateinit var forgotpassword : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        forgotpassword = findViewById(R.id.forgot_password)
        submitbtn = findViewById(R.id.submit_btn)
        submitbtn.setOnClickListener {
            val email: String = forgotpassword.text.toString().trim{ it <= ' '}
            if (email.isEmpty()){
                Toast.makeText(this@ForgotPasswordActivity, "Please enter email address.", Toast.LENGTH_SHORT).show()
            }
            else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            Toast.makeText(this@ForgotPasswordActivity, "Email sent successfully to reset your password!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        else
                        {
                            Toast.makeText(this@ForgotPasswordActivity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

    }
}