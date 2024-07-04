package com.example.barberqueue

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var profileImageView: ImageView
    private lateinit var editButton: Button
    private lateinit var usernameView: TextView
    private lateinit var emailView: TextView
    private lateinit var nomorTeleponView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        profileImageView = findViewById(R.id.profileImageView)
        editButton = findViewById(R.id.editButton)
        usernameView = findViewById(R.id.usernameView)
        emailView = findViewById(R.id.emailView)
        nomorTeleponView = findViewById(R.id.nomorTeleponView)

        loadProfileData()
        editButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivityForResult(intent, 2)
            finish()
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.buttonProfile

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.buttonHome -> {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.buttonPesan -> {
                    startActivity(Intent(applicationContext, ReserveServicesActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.buttonTransaksi -> {
                    startActivity(Intent(applicationContext, DetailTransaksiActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.buttonProfile -> true
                else -> false
            }
        }
    }
    private fun loadProfileData() {
        val userId = auth.currentUser?.uid
        userId?.let {
            db.collection("users").document(it).get().addOnSuccessListener { document ->
                if (document != null) {
                    val username = document.getString("username")
                    val email = document.getString("email")
                    val nomorTelepon = document.getString("nomorTelepon")
                    val profileImageRef = document.getString("profileImageRef")

                    usernameView.text = (username)
                    emailView.text = (email)
                    nomorTeleponView.text = (nomorTelepon)

                    // Load profile image
                    profileImageRef?.let { path ->
                        val ref = storage.getReference(path)
                        ref.downloadUrl.addOnSuccessListener { uri ->
                            Glide.with(this).load(uri).into(profileImageView)
                        }.addOnFailureListener {
                            // Handle any errors
                            Toast.makeText(this, "Failed to load profile image", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            usernameView.text = data.getStringExtra("username")
            emailView.text = data.getStringExtra("email")
            nomorTeleponView.text = data.getStringExtra("nomor_telepon")
        }
    }
}