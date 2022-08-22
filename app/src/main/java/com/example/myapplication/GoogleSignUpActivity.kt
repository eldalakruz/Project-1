package com.example.myapplication

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.databinding.ActivityGoogleSignUpBinding
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GoogleSignUpActivity : AppCompatActivity() {

    private lateinit var signupbtn : Button
    private lateinit var fullnamesignup : EditText
    private lateinit var usernamesignup : EditText
    private lateinit var emailsignup : EditText
    private lateinit var passwordsignup : EditText

    private lateinit var binding : ActivityGoogleSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signupbtn = findViewById(R.id.signup_btn)
        signupbtn.setOnClickListener {
            CreatAccount()
        }
    }

    private fun  CreatAccount(){

        fullnamesignup = findViewById(R.id.full_name_signup)
        val fullName = fullnamesignup.text.toString()
        usernamesignup = findViewById(R.id.username_signup)
        val userName = usernamesignup.text.toString()
        emailsignup = findViewById(R.id.email_signup)
        val emailSignUp = emailsignup.text.toString()
        passwordsignup = findViewById(R.id.password_signup)
        val passwordSignUp = passwordsignup.text.toString()

        when{
            TextUtils.isEmpty(fullName) -> Toast.makeText(this,"Full Name is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this,"User Name is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(emailSignUp) -> Toast.makeText(this,"Email is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(passwordSignUp) -> Toast.makeText(this,"Password is required",Toast.LENGTH_LONG).show()

            else -> {

                val progressDialog = ProgressDialog(this@GoogleSignUpActivity)
                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("Please wait, this may take a while.....")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth : FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(emailSignUp,passwordSignUp).addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        saveUserInfo(fullName, userName, emailSignUp, progressDialog)
                    } else {
                        val message = task.exception!!.toString()
                        Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG)
                        mAuth.signOut()
                        progressDialog.dismiss()
                    }

                }
                }
        }
    }

    private fun saveUserInfo(fullName: String, userName: String, emailSignUp: String, progressDialog: ProgressDialog,) {

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance("https://my-application-4aa96-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullName.toLowerCase()
        userMap["username"] = userName.toLowerCase()
        userMap["email"] = emailSignUp
        userMap["bio"] = "hay i am using this app i am cool....."
        userMap["image"] = ""

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->

                if (task.isSuccessful)
                {
                    progressDialog.dismiss()
                    Toast.makeText(this,"Account has been created successfully.",Toast.LENGTH_LONG).show()

                    val intent = Intent(this@GoogleSignUpActivity, MainActivity::class.java)
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