package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.Adapter.MyAdapter
import com.google.android.material.tabs.TabLayout
import java.io.File

class CompaignPage : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    private var postId   = ""
     var publisherId = ""
     var contestantone = ""
     var contestanttwo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compaign_page)
        tabLayout = findViewById(R.id.tablayout)
        viewPager = findViewById(R.id.viewPager)

     val intent = intent
        postId = intent.getStringExtra("postId").toString()
        publisherId = intent.getStringExtra("publisherId").toString()
        contestantone = intent.getStringExtra("contestantone").toString()
        contestanttwo = intent.getStringExtra("contestanttwo").toString()

        tabLayout.addTab(tabLayout.newTab().setText("$contestantone"))
        tabLayout.addTab(tabLayout.newTab().setText("$contestanttwo"))

        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = MyAdapter(this,supportFragmentManager,
              tabLayout.tabCount)
        viewPager.adapter = adapter


        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

        //File and Folder create in Internal storage
        val path = this.getExternalFilesDir(null)
        val folder = File(path,"temp")

        if (folder.exists()){
         val delete =   folder.deleteRecursively()
            Log.e("test","file:$delete")

        }
            folder.mkdirs()
            val file = File(folder, "postid.txt")
            file.appendText("$postId")


    }

}