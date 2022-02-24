package com.example.myapplication

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
        val chatFragment = ChatFragment()
        val addFragment = AddFragment()
        val profileFragment = ProfileFragment()
        val searchFragment = SearchFragment()

        setThatFragment(homeFragment)

        bottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.miHome ->{
                    setThatFragment(homeFragment)
                }
                R.id.miChat ->{
                    setThatFragment(chatFragment)
                }
                R.id.miadd ->{
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