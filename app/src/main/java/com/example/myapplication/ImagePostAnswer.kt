package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File

class ImagePostAnswer : AppCompatActivity() {

    private lateinit var uploadimageBtn: ImageView
    private lateinit var pickImageFab: FloatingActionButton
    private lateinit var image: ImageView
    private lateinit var titleEt: EditText

    private val VIDEO_PICK_GALLERY_CODE = 101
    private val VIDEO_PICK_CAMERA_CODE = 102

    private val CAMERA_REQUEST_CODE = 103

    private var imageUri: Uri? = null

    private var title: String = ""

    private lateinit var cameraPermissions: Array<String>

    private lateinit var progressDialog: ProgressDialog

    private var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_post_answer)

//        val intent = intent
//        id = intent.getStringExtra("id").toString()
//        Log.e("sample","test:$id")


        cameraPermissions =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("please wait")
        progressDialog.setMessage("Uploading Image...")
        progressDialog.setCanceledOnTouchOutside(false)

        uploadimageBtn = findViewById(R.id.uploadimageBtn)
        uploadimageBtn.setOnClickListener {
            titleEt = findViewById(R.id.titleEt)
            title = titleEt.text.toString().trim()
            if (TextUtils.isEmpty(title)) {
                //no title is entered
                Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show()
            } else if (imageUri == null) {
                //video is not picked
                Toast.makeText(this, "Pick the image first", Toast.LENGTH_SHORT).show()
            } else {
                //title entered,video picked, so now upload video
                uploadImageFirebase()


            }
        }
        pickImageFab = findViewById(R.id.pickImageFab)
        pickImageFab.setOnClickListener {
            imagePickDialog()
        }


    }

    private fun imagePickDialog() {

        val options = arrayOf("Camere", "Gallery")

        //alart dialog
        val builder = AlertDialog.Builder(this)
        //title
        builder.setTitle("Pick Image From")
            .setItems(options){ dialogInterface, i->
                if (i==0){
                    //camera clicked
                    if (!checkCameraPermissions()){
                        //permission was not allowed, request
                        requestCameraPermission()
                    }
                    else{
                        //permission was allowed,pick video
                        imagePickCamera()
                    }
                }
                else{
                    //gallery clicked
                    imagePickGallery()
                }
            }
            .show()

    }

    private fun imagePickGallery() {
        val intent = Intent()
        intent.type ="image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(
            Intent.createChooser(intent, "Choose video"),
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

    private fun uploadImageFirebase() {

        val path = getExternalFilesDir(null)
        val folder = File(path,"image")
        println(folder.exists())
        val file = File(folder, "postid.txt")
        val connt = file.readText()

        progressDialog.show()

        val timestamp = "" + System.currentTimeMillis()

        val filePathAndName = "Image/mani_$timestamp"

        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)

        storageReference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);


                val downloadUri = uriTask.result
                if (uriTask.isSuccessful) {
                    //video url is received successfully
                    val dbReference = FirebaseDatabase.getInstance().getReference("Answer").child(connt)
                       // .child("image")
                    //now we can add video details to firebase db
                    val hashMap = HashMap<String, Any>()
                     val postId = dbReference.push().key

                    val mani = FirebaseAuth.getInstance().currentUser!!.uid

                    // ref.removeValue()

                    hashMap["id"] = postId!!
                    hashMap["title"] = "$title"
                    hashMap["timestamp"] = "$timestamp"
                    hashMap["imageUri"] = "$downloadUri"
                   // hashMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                   // hashMap["postid"] = "$connt"

                   // Log.e("sample", "test:$connt")

                    //put the above info to db
                    dbReference.child(postId)
                        .setValue(hashMap)
                        .addOnSuccessListener { taskSnapshot ->
                            //video info added successfully
                            progressDialog.dismiss()
                            Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()


                        }

                        .addOnFailureListener { e ->
                            //failed adding video info
                            progressDialog.dismiss()
                            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()

                        }

                }
            }


    }

    private fun setImageToView() {

        image = findViewById(R.id.image)
        val mediaController = MediaController(this)

        mediaController.setAnchorView(image)

        image.setImageURI(imageUri)

        image.setImageURI(imageUri)

        image.requestFocus()

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
}