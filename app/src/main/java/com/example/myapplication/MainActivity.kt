package com.example.myapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.fragments.*
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity()  {

/*companion object
{
    const val TAG = "MainActivity"
    const val ANONYMOUS = "anonymous"
}  */
/*    private var userName :String? = null
    private var userPhotoUrl : String? = null
    private var googleApiClient : GoogleApiClient? = null
    private var firebaseAuth : FirebaseAuth? = null
    private var firebaseUser : FirebaseUser? = null   */

 /*   override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG,"onConnectionFailed $connectionResult")

        Toast.makeText(this,"Google Play Services error", Toast.LENGTH_SHORT).show()
    }   */

    private lateinit var bottomNavView : BottomNavigationView
    lateinit var binding: ActivityMainBinding

    //for Update
    private var appUpdate : AppUpdateManager? = null
    private var REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        bottomNavView = binding.bottomNavView



        //Update
        appUpdate =  AppUpdateManagerFactory.create(this)
        checkUpate()



       // for google
/*        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this,this)
            .addApi(Auth.GOOGLE_SIGN_IN_API)
            .build()

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.currentUser

        if (firebaseUser == null){
            val intent = Intent(this@MainActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            userName = firebaseUser!!.displayName

            if (firebaseUser!!.photoUrl != null)
            {
                userPhotoUrl = firebaseUser!!.photoUrl!!.toString()
            }
        }     */
       //---------------------------


        val homeFragment = HomeFragment()
        val newpostFragment = NewpostFragment()
        val addFragment = AddFragment()
//        val profileFragment = ProfileFragment()
        val profileFragment = ProfileFrament_New()
        val searchFragment = SearchFragment()

        setThatFragment(homeFragment)

        bottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.miHome ->{
                    setThatFragment(homeFragment)
                }
                R.id.miPost ->{
                    setThatFragment(newpostFragment)
                }
                R.id.miadd ->{
                    setThatFragment(addFragment)
//                    it.isChecked = false

//                    val view = layoutInflater.inflate(R.layout.fragment_add, null)
//                    val dialog = BottomSheetDialog(this)
//                    val btnClose = view.findViewById<Button>(R.id.item_1)
//                    btnClose.setOnClickListener {
//                        Toast.makeText(this,"you pressed on button", Toast.LENGTH_LONG).show()
//                        startActivity(Intent(this@MainActivity, AddPostActivity::class.java))
//                    }
//                    val btnpost = view.findViewById<TextView>(R.id.item_2)
//                    btnpost.setOnClickListener {
//                        startActivity(Intent(this@MainActivity, PostActivity::class.java))
//                    }
//                    dialog.setContentView(view)
//                    dialog.show()
                    //startActivity(Intent(this@MainActivity, AddPostActivity::class.java))
                   // setThatFragment(addFragment)
                }

                R.id.miProfile ->{

 /*                   it.isChecked = false
                    val view = layoutInflater.inflate(R.layout.fragment_profile_frament__new, null)
                    val dialog = BottomSheetDialog(this)

                    dialog.setContentView(view)
                    dialog.show()   */
                    setThatFragment(profileFragment)
                }
                R.id.miSearch ->{
                    setThatFragment(searchFragment)
                }
            }
            true
        }

    }

    private fun setThatFragment(fragment : Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame,fragment)
     //       addToBackStack(null)
            commit()
        }

    override fun onBackPressed() {
        if (bottomNavView.selectedItemId == R.id.miHome)
        {
            super.onBackPressed()
            finish()
        }
        else
        {
            bottomNavView.selectedItemId = R.id.miHome
        }
    }


    override fun onResume() {
        super.onResume()
        inProgrssUpdate()

    }

    fun checkUpate(){

        appUpdate?.appUpdateInfo?.addOnSuccessListener { updateInfo ->

            if (updateInfo.updateAvailability()== UpdateAvailability.UPDATE_AVAILABLE
                && updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){

                appUpdate?.startUpdateFlowForResult(updateInfo, AppUpdateType.IMMEDIATE,this,REQUEST_CODE)
            }
        }
    }

    fun inProgrssUpdate(){

        appUpdate?.appUpdateInfo?.addOnSuccessListener { updateInfo ->

            if (updateInfo.updateAvailability()== UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){

                appUpdate?.startUpdateFlowForResult(updateInfo, AppUpdateType.IMMEDIATE,this,REQUEST_CODE)
            }
        }

    }

}



