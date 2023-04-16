package com.example.agri_biasharaapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var profileLabel: TextView
    private lateinit var usersLabel: TextView
    private lateinit var notificationLabel: TextView
    private lateinit var mViewPager: ViewPager
    private lateinit var mAuth: FirebaseAuth
    private lateinit var myUsersDatabase: DatabaseReference
    private lateinit var tvUsername: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        myUsersDatabase =
            FirebaseDatabase.getInstance().reference.child("ENACTUS UOE").child("Users")
        myUsersDatabase.keepSynced(true)
        profileLabel = findViewById(R.id.profileLabel)
        usersLabel = findViewById(R.id.usersLabel)
        notificationLabel = findViewById(R.id.notificationLabel)
        mViewPager = findViewById(R.id.mainViewPager)
        tvUsername = findViewById(R.id.username)

        profileLabel.setOnClickListener {
            mViewPager.currentItem = 0
        }
        usersLabel.setOnClickListener {
            mViewPager.currentItem = 1
        }
        notificationLabel.setOnClickListener {
            mViewPager.currentItem = 2
        }
    }

    private fun changeTabs(position: Int) {
        if (position == 0) {
            profileLabel.setTextColor(resources.getColor(R.color.black))
            profileLabel.textSize = 22f
            usersLabel.setTextColor(resources.getColor(R.color.black))
            usersLabel.textSize = 16f
            notificationLabel.setTextColor(resources.getColor(R.color.black))
        }
        if (position == 1) {
            profileLabel.setTextColor(resources.getColor(R.color.black))
            profileLabel.textSize = 16f
            usersLabel.setTextColor(resources.getColor(R.color.black))
            usersLabel.textSize = 22f
            notificationLabel.setTextColor(resources.getColor(R.color.black))
            notificationLabel.textSize = 16f
        }
        if (position == 2) {
            profileLabel.setTextColor(resources.getColor(R.color.black))
            profileLabel.textSize = 16f
            usersLabel.setTextColor(resources.getColor(R.color.black))
            usersLabel.textSize = 16f
            notificationLabel.setTextColor(resources.getColor(R.color.black))
            notificationLabel.textSize = 22f
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            sendToLogin()
        } else {
            verifyUserDetails()
        }
    }

    private fun verifyUserDetails() {
        val userId = mAuth.currentUser?.uid
        myUsersDatabase.child(userId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val name = dataSnapshot.child("name").value.toString()
                    tvUsername.text = "You are signed in as:  ${name.uppercase(Locale.getDefault())}  Sign out ?"
                } else {
                    sendToSetup()
                }
            }

            override fun  onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun sendToLogin() {
        val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(loginIntent)
        finish()
    }

    private fun sendToSetup() {
        val setupIntent = Intent(this@MainActivity, SetupActivity::class.java)
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(setupIntent)
        finish()
    }

    private fun showLogoutDialog() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                mAuth.signOut()
                sendToLogin()
            }
            .setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()
    }
}
