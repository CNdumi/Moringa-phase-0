package com.example.agri_biasharaapp

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import java.util.Date

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener


import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class ProfileFragment : Fragment() {
    private val GALLERY_REQUEST_CODE = 2345
    private lateinit var btnUpdate: FloatingActionButton
    private lateinit var productName: EditText
    private lateinit var productCategory: EditText
    private lateinit var productQuantity: EditText
    private lateinit var productPrice: EditText
    private lateinit var productsList: RecyclerView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var v: View
    private lateinit var d: Dialog
    private var imageUri: Uri? = null
    private lateinit var profileImage: ImageView
    private lateinit var myProductsDatabase: DatabaseReference
    private lateinit var mStorageReference: StorageReference
    private lateinit var pd: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false)
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        mAuth = FirebaseAuth.getInstance()
        myProductsDatabase =
            FirebaseDatabase.getInstance().reference.child("ENACTUS UOE").child("Products")

        mStorageReference =
            FirebaseStorage.getInstance().reference.child("ENACTUS UOE").child("Product Images")

        btnUpdate = v.findViewById(R.id.fb)
        d = Dialog(requireContext())
        d.setContentView(R.layout.post_dialog)
        profileImage = d.findViewById(R.id.productImage)
        productName = d.findViewById(R.id.product_title)
        productCategory = d.findViewById(R.id.product_category)
        productQuantity = d.findViewById(R.id.product_quantity)
        productPrice = d.findViewById(R.id.product_price)
        productsList = v.findViewById(R.id.productsList)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        productsList.layoutManager = gridLayoutManager
        userId = mAuth.currentUser?.uid.orEmpty()
        pd = ProgressDialog(requireContext())
        pd.setTitle("Uploading Product")
        pd.setMessage("Please wait...")
        btnUpdate.setOnClickListener {
            bringPostDialog()
        }
        return v
    }

    private fun bringPostDialog() {
        val btnPost = d.findViewById<Button>(R.id.btnUpload)

        profileImage.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }
        btnPost.setOnClickListener {
            val name = productName.text.toString().trim()
            val category = productCategory.text.toString().trim()
            val quantity = productQuantity.text.toString().trim()
            val price = productPrice.text.toString().trim()


            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(category) && !TextUtils.isEmpty(
                    quantity
                ) && !TextUtils.isEmpty(price) && imageUri != null
            ) {
                pd.show()
                val currentDateTimeString = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Date())
                val productMap: HashMap<String, String> = HashMap()
                productMap["name"] = name
                productMap["category"] = category
                productMap["quantity"] = quantity
                productMap["price"] = price
                productMap["date"] = currentDateTimeString
                productMap["userId"] = userId

                val productPushId = myProductsDatabase.push().key
                val filePath = mStorageReference.child("$productPushId.jpg")
                filePath.putFile(imageUri!!)
                    .addOnCompleteListener(OnCompleteListener<UploadTask.TaskSnapshot> { task ->
                        if (task.isSuccessful) {
                            filePath.downloadUrl.addOnSuccessListener(OnSuccessListener<Uri> { uri ->
                                val downloadUrl = uri.toString()
                                productMap["image"] = downloadUrl

                                myProductsDatabase.child(productPushId!!).setValue(productMap)
                                    .addOnCompleteListener(OnCompleteListener<Void> { task ->
                                        if (task.isSuccessful) {
                                            pd.dismiss()
                                            d.dismiss()

                                            Toast.makeText(
                                                requireContext(),
                                                "Product uploaded successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            pd.dismiss()
                                            Toast.makeText(
                                                requireContext(),
                                                "Failed to upload product. Please try again.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            })
                        } else {
                            pd.dismiss()
                            Toast.makeText(
                                requireContext(),
                                "Failed to upload product image. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please fill all the fields and select an image.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
}


