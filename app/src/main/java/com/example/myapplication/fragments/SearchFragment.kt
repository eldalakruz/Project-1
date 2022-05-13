package com.example.myapplication.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.UserAdapter
import com.example.myapplication.Model.User
import com.example.myapplication.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<User>? = null

    private lateinit var searchedittext : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_scarch)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)


        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it, mUser as ArrayList<User>, true)  }
        recyclerView?.adapter = userAdapter

        searchedittext = view.findViewById(R.id.search_edit_text)
        searchedittext.addTextChangedListener( object: TextWatcher
        {
            override fun afterTextChanged(s: Editable?)
            {

            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (searchedittext.text.toString() == "")
                {

                }
                else
                {
                    recyclerView?.visibility = View.VISIBLE

                    retrieveUsers()
                    searchUser(s.toString().toLowerCase())
                }
            }


        })
        return view
    }

    private fun searchUser(input: String)
    {
        val query = FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("fullname")
            .startAt(input)
            .endAt(input + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                mUser?.clear()

                for (snapshot in dataSnapshot.children)
                {
                    val user =  snapshot.getValue(User::class.java)
                    if (user != null)
                    {
                        mUser?.add(user)
                    }

                }
                userAdapter?.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError)
            {

            }
        })

    }

    private fun retrieveUsers()
    {
      val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
      usersRef.addValueEventListener(object : ValueEventListener
      {
          override fun onDataChange(dataSnapshot: DataSnapshot)
          {
              if (searchedittext.text.toString() == "")
              {
                  mUser?.clear()

                  for (snapshot in dataSnapshot.children)
                  {
                      val user =  snapshot.getValue(User::class.java)
                      if (user != null)
                      {
                          mUser?.add(user)
                      }
                  }
                  userAdapter?.notifyDataSetChanged()
              }
          }
          override fun onCancelled(error: DatabaseError)
          {

          }
      })
    }
}