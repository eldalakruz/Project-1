package com.example.myapplication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var signuplinkbtn : TextView
    private lateinit var loginbtn : Button
    private lateinit var emaillogin : EditText
    private lateinit var passwordlogin : EditText
    private lateinit var forgotpassword : TextView

    lateinit var binding : ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        signuplinkbtn = findViewById(R.id.signup_link_btn)
        signuplinkbtn.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        loginbtn = findViewById(R.id.login_btn)
        loginbtn.setOnClickListener {
            loginUser()
        }

        forgotpassword = findViewById(R.id.forgot_password)
        forgotpassword.setOnClickListener {
            startActivity(Intent(this,ForgotPasswordActivity::class.java))
        }


    }

    private fun loginUser() {
        emaillogin = findViewById(R.id.email_login)
        val emaillogin = emaillogin.text.toString()
        passwordlogin = findViewById(R.id.password_login)
        val passwordlogin = passwordlogin.text.toString()

        when
        {
            TextUtils.isEmpty(emaillogin) -> Toast.makeText(this,"Email is required", Toast.LENGTH_LONG)
            TextUtils.isEmpty(passwordlogin) -> Toast.makeText(this,"Password is required", Toast.LENGTH_LONG)

            else ->
            {
                val progressDialog = ProgressDialog(this@SignInActivity)
                progressDialog.setTitle("Login")
                progressDialog.setMessage("Please wait, this may take a while.....")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth : FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.signInWithEmailAndPassword(emaillogin, passwordlogin).addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        progressDialog.dismiss()

                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        val message = task.exception!!.toString()
                        Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG)
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null){

            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}