package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {


    private lateinit var bottomNavView : BottomNavigationView
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        bottomNavView = binding.bottomNavView


        val homeFragment = HomeFragment()
        val notificationsFragment = NotificationsFragment()
        val addFragment = AddFragment()
        val profileFragment = ProfileFragment()
        val searchFragment = SearchFragment()

        setThatFragment(homeFragment)

        bottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.miHome ->{
                    setThatFragment(homeFragment)
                }
                R.id.miNotifications ->{
                    setThatFragment(notificationsFragment)
                }
                R.id.miadd ->{
                    it.isChecked = false

                    val view = layoutInflater.inflate(R.layout.fragment_add, null)
                    val dialog = BottomSheetDialog(this)
                    val btnClose = view.findViewById<Button>(R.id.item_1)
                    btnClose.setOnClickListener {
                  //      Toast.makeText(this,"you pressed on button", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@MainActivity, AddPostActivity::class.java))
                    }
                    dialog.setContentView(view)
                    dialog.show()

                    //startActivity(Intent(this@MainActivity, AddPostActivity::class.java))
                   // setThatFragment(addFragment)
                }

                R.id.miProfile ->{
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
            commit()
        }

}