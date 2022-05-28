package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class Add_questiom : AppCompatActivity() {

    private lateinit var write_question: EditText
    private lateinit var uploadquestionBtn: ImageView
    private lateinit var imageforquestion: ImageView
    private lateinit var addimageFab: FloatingActionButton

    private val VIDEO_PICK_GALLERY_CODE = 101
    private val VIDEO_PICK_CAMERA_CODE = 102
    private val CAMERA_REQUEST_CODE = 103

    private var imageUri: Uri? = null

    private lateinit var cameraPermissions: Array<String>

    private lateinit var progressDialog: ProgressDialog

    private lateinit var cardView: CardView

    private var Title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_questiom)


        cameraPermissions =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("please wait")
        progressDialog.setMessage("Uploading Question...")
        progressDialog.setCanceledOnTouchOutside(false)

        uploadquestionBtn = findViewById(R.id.uploadquestionBtn)
        uploadquestionBtn.setOnClickListener {

            write_question = findViewById(R.id.write_question)

            Title = write_question.text.toString().trim()

            if (TextUtils.isEmpty(Title)) {
                Toast.makeText(this, "Question is Empty", Toast.LENGTH_SHORT).show()
            } else if (imageUri == null) {
                Toast.makeText(this, "Pick the image first", Toast.LENGTH_SHORT).show()
            } else {
                uploadQuestionFirebase()
            }
        }

        addimageFab = findViewById(R.id.addimageFab)
        addimageFab.setOnClickListener {
            imagePickDialog()
        }

        /* uploadquestionBtn.setOnClickListener { object : View.OnClickListener{
            @SuppressLint("ResourceAsColor")
            override fun onClick(p0: View?) {
                cardView = findViewById(R.id.cardView)
                cardView.setBackgroundColor(R.color.colorGray)
            }

        }
            write_question = findViewById(R.id.write_question)

            Title = write_question.text.toString().trim()

            if (TextUtils.isEmpty(Title)){
                Toast.makeText(this, "Question is Empty", Toast.LENGTH_SHORT).show()
            }
            else{
                uploadQuestionFirebase()
            }

        }*/
    }

    private fun imagePickDialog() {
        val options = arrayOf("Camere", "Gallery")

        //alart dialog
        val builder = AlertDialog.Builder(this)
        //title
        builder.setTitle("Pick Image From")
            .setItems(options) { dialogInterface, i ->
                if (i == 0) {
                    //camera clicked
                    if (!checkCameraPermissions()) {
                        //permission was not allowed, request
                        requestCameraPermission()
                    } else {
                        //permission was allowed,pick video
                        imagePickCamera()
                    }
                } else {
                    //gallery clicked
                    imagePickGallery()
                }
            }
            .show()
    }

    private fun imagePickGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(
            Intent.createChooser(intent, "Choose image"),
            VIDEO_PICK_GALLERY_CODE
        )
    }

    private fun imagePickCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE)
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            cameraPermissions,
            CAMERA_REQUEST_CODE
        )
    }

    private fun checkCameraPermissions(): Boolean {

        val result1 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val result2 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        //return result as true/false
        return result1 && result2

    }

    private fun uploadQuestionFirebase() {

        progressDialog.show()

        val timestamp = "" + System.currentTimeMillis()

        val filePathAndName = "QuestionImage/mani_$timestamp"

        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)

        storageReference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);


                val downloadUri = uriTask.result
                if (uriTask.isSuccessful) {

        val dbReference = FirebaseDatabase.getInstance().reference.child("Question")

        val hashMap = HashMap<String, Any>()
        val postId = dbReference.push().key

        hashMap["id"] = "$postId"
        hashMap["Question"] = "$Title"
        hashMap["timestamp"] = "$timestamp"
        hashMap["imageUri"]  = "$downloadUri"


        dbReference.child(postId!!).setValue(hashMap)
            .addOnSuccessListener { taskSnapshot ->

                progressDialog.dismiss()

                Toast.makeText(this, "Question Uploaded", Toast.LENGTH_SHORT).show()
                finish()
            }

            .addOnFailureListener { e ->
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
            }

         }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK){
            //image is picked from camera or gallery
            if (requestCode == VIDEO_PICK_CAMERA_CODE){
                //image picked from camera
                imageUri == data!!.data
                setImageToView()
            }
            else if (requestCode == VIDEO_PICK_GALLERY_CODE){
                //image picked from gallery
                imageUri = data!!.data
                setImageToView()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setImageToView() {
        imageforquestion = findViewById(R.id.imageforquestion)
        val mediaController = MediaController(this)

        mediaController.setAnchorView(imageforquestion)

        imageforquestion.setImageURI(imageUri)

        imageforquestion.setImageURI(imageUri)

        imageforquestion.requestFocus()
    }

}