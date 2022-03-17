package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

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
                    startActivity(Intent(this@MainActivity, AddPostActivity::class.java))

                    setThatFragment(addFragment)
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