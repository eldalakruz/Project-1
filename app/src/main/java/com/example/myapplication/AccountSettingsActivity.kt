package com.example.myapplication

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.Model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageActivity


class AccountSettingsActivity : AppCompatActivity() {


    private lateinit var logoutbtn : Button
    private lateinit var saveinforprofilebtn : ImageView

    private lateinit var profileimageviewprofilefrag : de.hdodenhof.circleimageview.CircleImageView
    private lateinit var usernameprofilefrag : TextView
    private lateinit var fullnameprofilefrag : TextView
    private lateinit var bioprofilefrag : TextView
    private lateinit var changeimagetextbtn : TextView

    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri : Uri? = null
    private var storageProfilePicRef : StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)
        supportActionBar?.hide()

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        logoutbtn = findViewById(R.id.logout_btn)
        logoutbtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@AccountSettingsActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        changeimagetextbtn = findViewById(R.id.change_image_text_btn)
        changeimagetextbtn.setOnClickListener {

            checker = "clicked"

            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@AccountSettingsActivity)
        }

        saveinforprofilebtn = findViewById(R.id.save_infor_profile_btn)
        saveinforprofilebtn.setOnClickListener {
            if (checker == "clicked")
            {
                uploadImageAndUpdateInfo()
            }
            else
            {
                updateUserInfoOnly()
            }

        }

        userInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profileimageviewprofilefrag.setImageURI(imageUri)
        }
    }


    private fun updateUserInfoOnly() {

        when {
            TextUtils.isEmpty(fullnameprofilefrag.text.toString()) -> {
                Toast.makeText(this,"Please write full name first.",Toast.LENGTH_LONG).show()
            }
            usernameprofilefrag.text.toString() == "" -> {
                Toast.makeText(this,"Please write user name first.",Toast.LENGTH_LONG).show()
            }
            bioprofilefrag.text.toString() == "" -> {
                Toast.makeText(this,"Please write bio first.",Toast.LENGTH_LONG).show()
            }
            else -> {

                val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

                val userMap = HashMap<String, Any>()
                userMap["fullname"] = fullnameprofilefrag.text.toString().toLowerCase()
                userMap["username"] = usernameprofilefrag.text.toString().toLowerCase()
                userMap["bio"] = bioprofilefrag.text.toString().toLowerCase()

                usersRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this,"Account information has been updated successfully.",Toast.LENGTH_LONG).show()

                val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)

                    profileimageviewprofilefrag = findViewById(R.id.profile_image_view_profile_frag)
                    usernameprofilefrag = findViewById(R.id.username_profile_frag)
                    fullnameprofilefrag = findViewById(R.id.full_name_profile_frag)
                    bioprofilefrag = findViewById(R.id.bio_profile_frag)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileimageviewprofilefrag)
                    usernameprofilefrag.setText(user!!.getUsername())
                    fullnameprofilefrag.text = user!!.getFullname()
                    bioprofilefrag.text = user!!.getBio()


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun uploadImageAndUpdateInfo()
    {
       when
       {
           TextUtils.isEmpty(fullnameprofilefrag.text.toString()) -> {
               Toast.makeText(this,"Please write full name first.",Toast.LENGTH_LONG).show()
           }
           usernameprofilefrag.text.toString() == "" -> {
               Toast.makeText(this,"Please write user name first.",Toast.LENGTH_LONG).show()
           }
           bioprofilefrag.text.toString() == "" -> {
               Toast.makeText(this,"Please write bio first.",Toast.LENGTH_LONG).show()
           }
           imageUri == null  -> {
               Toast.makeText(this, "Please select image first.", Toast.LENGTH_LONG).show()
           }
           else ->
           {
               val progressDialog = ProgressDialog(this)
               progressDialog.setTitle("Account Settings")
               progressDialog.setMessage("Please wait, we are updating your profile...")
               progressDialog.show()

               val fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid + "jpg")

               var uploadTask : StorageTask<*>
               uploadTask = fileRef.putFile(imageUri!!)

               uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                   if (!task.isSuccessful)
                   {
                       task.exception?.let {
                           throw it
                           progressDialog.dismiss()
                       }
                   }
                   return@Continuation fileRef.downloadUrl
               }).addOnCompleteListener (OnCompleteListener<Uri> {task ->
                   if (task.isSuccessful)
                   {
                       val downloadUrl = task.result
                       myUrl  = downloadUrl.toString()

                       val ref = FirebaseDatabase.getInstance().reference.child("Users")

                       val userMap = HashMap<String, Any>()
                       userMap["fullname"] = fullnameprofilefrag.text.toString().toLowerCase()
                       userMap["username"] = usernameprofilefrag.text.toString().toLowerCase()
                       userMap["bio"] = bioprofilefrag.text.toString().toLowerCase()
                       userMap["image"] = myUrl

                       ref.child(firebaseUser.uid).updateChildren(userMap)

                       Toast.makeText(this,"Account information has been updated successfully.",Toast.LENGTH_LONG).show()

                       val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                       startActivity(intent)
                       finish()

                       progressDialog.dismiss()
                   }
                   else
                   {
                       progressDialog.dismiss()
                   }
               })
           }
       }

    }

}