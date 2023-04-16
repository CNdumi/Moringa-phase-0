package com.example.agri_biasharaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SetupActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var btnSetup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference.child("ENACTUS UOE").child("Users")
        btnSetup = findViewById(R.id.btnSetup)

        btnSetup.setOnClickListener {
            val currentUser = mAuth.currentUser
            val userId = currentUser?.uid
            mDatabase.child(userId!!).child("setup").setValue(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val mainIntent = Intent(this@SetupActivity, MainActivity::class.java)
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(mainIntent)
                    finish()
                } else {
                    // Handle setup error
                }
            }
        }
    }
}
