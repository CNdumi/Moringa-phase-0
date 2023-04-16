package com.example.agri_biasharaapp

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import com.google.android.material.snackbar.Snackbar

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import com.squareup.picasso.Picasso


class DetailActivity : AppCompatActivity() {
    private var name: String? = null
    private var qty: String? = null
    private var price: String? = null
    private var image: String? = null
    private var poster: String? = null
    private var time: String? = null
    private var postId: String? = null
    private var category: String? = null
    private var tvDetailTitle: TextView? = null
    private var tvDetailViews: TextView? = null
    private var tvDetailLikes: TextView? = null
    private var tvDetailQty: TextView? = null
    private var tvDetailCategory: TextView? = null
    private var tvDetailPoster: TextView? = null
    private var tvDetailPrice: TextView? = null
    private var tvDetailTime: TextView? = null
    private var tvPosterPhone: TextView? = null
    private var tvPosterCounty: TextView? = null
    private var tvPosterSubcounty: TextView? = null
    private var tvPosterScaleType: TextView? = null
    private var ivImage: ImageView? = null
    private var myUsersDatabase: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var userId: String? = null
    private var myProductsDatabase: DatabaseReference? = null
    private var myCartDatabase: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        tvDetailTitle = findViewById(R.id.tvDetailTitle)
        tvDetailQty = findViewById(R.id.tvDetailQty)
        tvDetailCategory = findViewById(R.id.tvDetailCategory)
        tvDetailPoster = findViewById(R.id.tvDetailPoster)
        tvDetailPrice = findViewById(R.id.tvDetailPrice)
        tvDetailTime = findViewById(R.id.tvDetailTime)
        tvPosterPhone = findViewById(R.id.tvDetailPosterPhone)
        tvPosterCounty = findViewById(R.id.tvDetailPosterCounty)
        tvPosterSubcounty = findViewById(R.id.tvDetailPosterSubcounty)
        tvPosterScaleType = findViewById(R.id.tvDetailPosterScaletype)
        tvDetailViews = findViewById(R.id.tvDetailViews)
        tvDetailLikes = findViewById(R.id.tvDetailLikes)
        mAuth = FirebaseAuth.getInstance()
        myCartDatabase = FirebaseDatabase.getInstance().reference.child("ENACTUS UOE").child("Cart")
        myUsersDatabase =
            FirebaseDatabase.getInstance().reference.child("ENACTUS UOE").child("Users")
        myProductsDatabase =
            FirebaseDatabase.getInstance().reference.child("ENACTUS UOE").child("Products")
        userId = mAuth!!.currentUser!!.uid
        ivImage = findViewById(R.id.ivDetailImage)
        name = intent.getStringExtra("name")
        qty = intent.getStringExtra("qty")
        price = intent.getStringExtra("price")
        image = intent.getStringExtra("image")
        poster = intent.getStringExtra("poster")
        time = intent.getStringExtra("time")
        postId = intent.getStringExtra("postId")
        category = intent.getStringExtra("category")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = name
        tvDetailTitle!!.text = "" + name
        tvDetailQty!!.text = "$qty Kgs "
        tvDetailPrice!!.text = "Ksh: $price"
        tvDetailTime!!.text = "Uploaded at $time"
        tvDetailCategory!!.text = category
        Picasso.get().load(image).placeholder(R.drawable.ic_photo_library_black_24dp).into(ivImage)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener(View.OnClickListener { view ->
            sendProductToCart(
                view,
                name,
                qty,
                price,
                poster,
                postId
            )
        })
        setupUploader()
        setUpViewsandLikes()
        tvDetailLikes!!.setOnClickListener {
            myProductsDatabase!!.child(postId!!).child("product_likes").child(userId!!)
                .setValue("1").addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@DetailActivity,
                            "You Liked this product...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun sendProductToCart(
        view: View,
        name: String?,
        qty: String?,
        price: String?,
        poster: String?,
        postId: String?
    ) {
        val newCartID = myCartDatabase!!.child(userId!!).push().key
        val newCart = myCartDatabase!!.child(userId!!).child(newCartID!!)
        val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss ")
        val date = Date()
        val tim = dateFormat.format(date)
        val myMap = HashMap<String, Any?>()
        myMap["name"] = name
        myMap["qty"] = qty
        myMap["price"] = price
        myMap["poster_id"] = poster
        myMap["post_id"] = postId
        myMap["time"] = tim
        myMap["post_image"] = image
        newCart.updateChildren(myMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Snackbar.make(view, "You added $name to your cart. ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }

    private fun setUpViewsandLikes() {
        myProductsDatabase!!.child(postId!!).child("product_views")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val views = dataSnapshot.childrenCount
                        tvDetailViews!!.text = "" + views
                    }
                }

                override fun onCancelled(@NonNull databaseError: DatabaseError) {}
            })
        myProductsDatabase!!.child(postId!!).child("product_likes")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val likes = dataSnapshot.childrenCount
                        tvDetailLikes!!.text = "" + likes
                    }
                }

                override fun onCancelled(@NonNull databaseError: DatabaseError) {}
            })
    }

    private fun setupUploader() {
        myUsersDatabase!!.child(poster!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val name = dataSnapshot.child("name").value.toString()
                    val phon = dataSnapshot.child("phone").value.toString()
                    val coun = dataSnapshot.child("county").value.toString()
                    val subcou = dataSnapshot.child("subcounty").value.toString()
                    val scalet = dataSnapshot.child("type_scale").value.toString()
                    tvDetailPoster!!.text = "Product Sold  By: $name"
                    tvPosterPhone!!.text = phon
                    tvPosterCounty!!.text = coun
                    tvPosterSubcounty!!.text = subcou
                    tvPosterScaleType!!.text = scalet
                }
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }

    override fun onStart() {
        super.onStart()
        myProductsDatabase!!.child(postId!!).child("product_views").child(userId!!).setValue("1")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Toast.makeText(DetailActivity.this, "You Liked this product...", Toast.LENGTH_SHORT).show();
                }
            }
    }
}
