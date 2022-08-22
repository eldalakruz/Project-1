package com.example.myapplication

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*

import com.example.myapplication.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignUpActivity : AppCompatActivity() {


    private lateinit var signinlinkbtn : TextView
    private lateinit var signupbtn : Button
    private lateinit var fullnamesignup : EditText
    private lateinit var usernamesignup : EditText
    private lateinit var emailsignup : EditText
    private lateinit var passwordsignup : EditText

    private lateinit var rg_gender : RadioGroup
    private lateinit var rv_male : RadioButton
    private lateinit var rv_female : RadioButton
    private lateinit var txt_gender : TextView

    lateinit var binding : ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signinlinkbtn = findViewById(R.id.signin_link_btn)
        signinlinkbtn.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))
        }

        signupbtn = findViewById(R.id.signup_btn)
        signupbtn.setOnClickListener {
            CreatAccount()
        }
    }

    private fun CreatAccount() {

        Toast.makeText(this,"A_LOG entring create Account:",Toast.LENGTH_LONG)
        Log.e(TAG," A_LOG entring create Account")

        fullnamesignup = findViewById(R.id.full_name_signup)
        val fullName = fullnamesignup.text.toString()
        usernamesignup = findViewById(R.id.username_signup)
        val userName = usernamesignup.text.toString()
        emailsignup = findViewById(R.id.email_signup)
        val emailSignUp = emailsignup.text.toString()
        passwordsignup = findViewById(R.id.password_signup)
        val passwordSignUp = passwordsignup.text.toString()

 /*       rg_gender = findViewById(R.id.rg_gender)
        rv_male = findViewById(R.id.btn_male)
        rv_female = findViewById(R.id.btn_female)


        rg_gender.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.btn_male ->
                R.id.btn_female ->
            }
        }      */

        when{

            TextUtils.isEmpty(fullName) -> Toast.makeText(this,"Full Name is required",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this,"User Name is required",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(emailSignUp) -> Toast.makeText(this,"Email is required",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(passwordSignUp) -> Toast.makeText(this,"Password is required",Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("Please wait, this may take a while.....")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth : FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(emailSignUp,passwordSignUp).addOnCompleteListener { task ->

                    Log.e(TAG," A_LOG before if user")

                        if (task.isSuccessful)
                        {
                            Log.e(TAG," A_LOG  user if")

                            saveUserInfo(fullName, userName, emailSignUp, progressDialog)
                        }
                        else
                        {
                            Log.e(TAG," A_LOG  user else")

                            val message = task.exception!!.toString()

                            Log.e(TAG," A_LOG  user mess $message")

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

        Log.e(TAG," A_LOG  i am user Data")

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
                    Log.w(TAG," A_LOG  i am in IF case")

                    progressDialog.dismiss()
                    Toast.makeText(this,"Account has been created successfully.",Toast.LENGTH_LONG).show()


//                    FirebaseDatabase.getInstance().reference
//                        .child("Follow").child(currentUserID)
//                        .child("Following").child(currentUserID)
//                        .setValue(true)

                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
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