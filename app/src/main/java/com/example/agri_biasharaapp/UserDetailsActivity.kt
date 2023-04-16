package com.example.agri_biasharaapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class UserDetailsActivity : AppCompatActivity() {
    private var etName: EditText? = null
    private var etPhone: EditText? = null
    private var etCounty: EditText? = null
    private var etSubcounty: EditText? = null
    private var rbCropGrowing: RadioButton? = null
    private var rbAnimalRearing: RadioButton? = null
    private var rbSmallScale: RadioButton? = null
    private var rbLargeScale: RadioButton? = null
    private var btnUpdate: Button? = null
    private var type_farming = ""
    private var type_scale = ""
    private var myUsersDatabase: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var userId: String? = null
    private var pd: ProgressDialog? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etCounty = findViewById(R.id.etCounty)
        etSubcounty = findViewById(R.id.etSubCounty)
        pd = ProgressDialog(this)
        pd!!.setTitle("Completing profile setup")
        pd!!.setMessage("Just a moment")
        rbCropGrowing = findViewById(R.id.cropGrowing)
        rbAnimalRearing = findViewById(R.id.animalRearing)
        rbSmallScale = findViewById(R.id.smallScale)
        rbLargeScale = findViewById(R.id.largeScale)
        btnUpdate = findViewById(R.id.btnUpdate)
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth!!.currentUser!!.uid
        myUsersDatabase =
            FirebaseDatabase.getInstance().reference.child("ENACTUS UOE").child("Users")
        btnUpdate!!.setOnClickListener {
            pd!!.show()
            if (rbCropGrowing!!.isChecked) {
                type_farming = "Crop Growing"
            }
            if (rbAnimalRearing!!.isChecked) {
                type_farming = "Animal Rearing"
            }
            if (rbSmallScale!!.isChecked) {
                type_scale = "Small Scale"
            }
            if (rbLargeScale!!.isChecked) {
                type_scale = "Large Scale"
            }
            val name = etName!!.text.toString().trim { it <= ' ' }
            val phone = etPhone!!.text.toString().trim { it <= ' ' }
            val county = etCounty!!.text.toString().trim { it <= ' ' }
            val subCounty = etSubcounty!!.text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)
                && !TextUtils.isEmpty(county) && !TextUtils.isEmpty(name)
                && !TextUtils.isEmpty(subCounty)
                && !TextUtils.isEmpty(type_scale) && !TextUtils.isEmpty(type_farming)
            ) {
                //toast(name+type_farming+type_scale);
                uploadData(name, phone, county, subCounty, type_scale, type_farming)
            } else {
                pd!!.dismiss()
                toast("You left some blanks")
            }
        }
    }

    private fun uploadData(
        name: String,
        phone: String,
        county: String,
        subCounty: String,
        type_scale: String,
        type_farming: String
    ) {
        val newUser = myUsersDatabase!!.child(userId!!)
        val myMap = HashMap<String, Any>()
        myMap["name"] = name
        myMap["phone"] = phone
        myMap["county"] = county
        myMap["subcounty"] = subCounty
        myMap["type_scale"] = type_scale
        myMap["type_farming"] = type_farming
        newUser.updateChildren(myMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                pd!!.dismiss()
                startActivity(Intent(this@UserDetailsActivity, MainActivity::class.java))
                finish()
            } else {
                pd!!.dismiss()
                toast(task.exception!!.message)
            }
        }
    }

    private fun toast(s: String?) {
        Toast.makeText(this, "Message: $s", Toast.LENGTH_SHORT).show()
    }
}
