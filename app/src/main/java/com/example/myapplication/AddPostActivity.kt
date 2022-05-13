package com.example.myapplication

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.myapplication.fragments.AddFragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage

class AddPostActivity : AppCompatActivity() {

    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference? = null

    private lateinit var savenewpostbtn: ImageView
    private lateinit var imagepost: ImageView
    private lateinit var descriptionpost: EditText

    //
    private lateinit var pollquestion : EditText
    private lateinit var contestantone : EditText
    private lateinit var contestanttwo : EditText
    //


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        descriptionpost = findViewById(R.id.description_post)
        savenewpostbtn = findViewById(R.id.save_new_post_btn)

        //
        pollquestion = findViewById(R.id.poll_question)
        contestantone = findViewById(R.id.contestant_one)
        contestanttwo = findViewById(R.id.contestant_two)
        //

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("posts Pictures")

        savenewpostbtn.setOnClickListener { uploadImage() }


           CropImage.activity()
          .setAspectRatio(2,1)
          .start(this@AddPostActivity)

    }

    private fun uploadImage()
    {
    when{
        imageUri == null -> Toast.makeText(this,"Please select image first.", Toast.LENGTH_LONG).show()
        TextUtils.isEmpty(descriptionpost.text.toString()) -> Toast.makeText(this, "please write full name first", Toast.LENGTH_LONG).show()

        //
        TextUtils.isEmpty(pollquestion.text.toString()) -> Toast.makeText(this, "please write full name first", Toast.LENGTH_LONG).show()
        TextUtils.isEmpty(contestantone.text.toString()) -> Toast.makeText(this, "please write full name first", Toast.LENGTH_LONG).show()
        TextUtils.isEmpty(contestanttwo.text.toString()) -> Toast.makeText(this, "please write full name first", Toast.LENGTH_LONG).show()
        //
        else -> {

            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Adding New Post")
            progressDialog.setMessage("Please wait, we are adding your picture post...")
            progressDialog.show()

            val fileRef = storagePostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if (!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                        progressDialog.dismiss()
                    }
                }
                return@Continuation fileRef.downloadUrl
            })
                .addOnCompleteListener (OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful)
                    {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                        val postId = ref.push().key

                        val postMap = HashMap<String, Any>()
                        postMap["postid"] = postId!!
                        postMap["description"] = descriptionpost.text.toString().toLowerCase()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postimage"] = myUrl

                        //
                        postMap["pollquestion"] = pollquestion.text.toString().toLowerCase()
                        postMap["contestantone"] = contestantone.text.toString().toLowerCase()
                        postMap["contestanttwo"] = contestanttwo.text.toString().toLowerCase()
                        //

                        ref.child(postId).updateChildren(postMap)

                        Toast.makeText(this,"Post uploaded successfully.", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@AddPostActivity, MainActivity::class.java)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            imagepost = findViewById(R.id.image_post)
            imagepost.setImageURI(imageUri)
        }

    }

}