package com.example.myapplication

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplication.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class SignInActivity : AppCompatActivity()  {


    private lateinit var signuplinkbtn : TextView
    private lateinit var loginbtn : Button
    private lateinit var emaillogin : EditText
    private lateinit var passwordlogin : EditText
    private lateinit var forgotpassword : TextView
    private lateinit var googleSignin : ImageView


//for google

    lateinit var  googleSignINClient : GoogleSignInClient
    lateinit var fireBaseAuth : FirebaseAuth


//------------
    lateinit var binding : ActivitySignInBinding

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // for google

        fireBaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignINClient = GoogleSignIn.getClient(this,gso)

        googleSignin = findViewById(R.id.google_login)
        googleSignin.setOnClickListener {
               signInGoogle()
        }

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

    private fun signInGoogle() {

        val signInIntent = googleSignINClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {

        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUI(account: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(account.idToken , null)

        fireBaseAuth.signInWithCredential(credential).addOnCompleteListener{
            if (it.isSuccessful){

                val intent = Intent(this, GoogleSignUpActivity::class.java)

                //intent.putExtra
                startActivity(intent)

            }else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
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


    // for google signIn

//       override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//           super.onActivityResult(requestCode, resultCode, data)
//
//           if (requestCode == 10001)
//           {
//               val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//               val account = task.getResult(ApiException::class.java)
//               val credential = GoogleAuthProvider.getCredential(account.idToken,null)
//               FirebaseAuth.getInstance().signInWithCredential(credential)
//                   .addOnCompleteListener { task ->
//                       if (task.isSuccessful){
//
//                           val i = Intent(this,AccountSettingsActivity::class.java)
//                           startActivity(i)
//
//                       }else{
//                           Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
//                       }
//                   }
//               }
//       }

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