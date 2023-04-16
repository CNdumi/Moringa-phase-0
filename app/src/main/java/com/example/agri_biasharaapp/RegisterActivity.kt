package com.example.agri_biasharaapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.agri_biasharaapp.LoginActivity
import com.example.agri_biasharaapp.R
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private var etAdmNo: EditText? = null
    private var etPass: EditText? = null
    private var etPassOne: EditText? = null
    private var btnRegister: Button? = null
    private var mAuth: FirebaseAuth? = null
    private var pd: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        etAdmNo = findViewById(R.id.etAdm)
        etPass = findViewById(R.id.etPassword)
        etPassOne = findViewById(R.id.etPassword)
        pd = ProgressDialog(this)
        pd!!.setTitle("Creating Account")
        pd!!.setMessage("Just a moment...")
        mAuth = FirebaseAuth.getInstance()
        btnRegister = findViewById(R.id.btnRegister)
        btnRegister!!.setOnClickListener {
            pd!!.show()
            val adm = etAdmNo!!.text.toString().trim { it <= ' ' }
            val pass = etPass!!.text.toString().trim { it <= ' ' }
            val pass1 = etPassOne!!.text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(adm) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(pass1)) {
                if (pass == pass1) {
                    //toast("Data ready for upload....");
                    mAuth!!.createUserWithEmailAndPassword(adm, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                toast("Account created Successfully, Login to verify")
                                pd!!.dismiss()
                                mAuth!!.signOut()
                                startActivity(
                                    Intent(
                                        this@RegisterActivity,
                                        LoginActivity::class.java
                                    )
                                )
                                finish()
                            } else {
                                pd!!.dismiss()
                                toast(task.exception!!.message)
                            }
                        }
                } else {
                    pd!!.dismiss()
                    toast("Passwords don't match....")
                }
            } else {
                pd!!.dismiss()
                toast("You left a blank !")
            }
        }
    }

    fun signin(view: View?) {
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        finish()
    }

    private fun toast(s: String?) {
        Toast.makeText(this, "Message: $s", Toast.LENGTH_SHORT).show()
    }
}