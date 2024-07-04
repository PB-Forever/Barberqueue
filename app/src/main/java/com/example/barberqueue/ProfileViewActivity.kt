package com.example.barberqueue

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class ProfileViewActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var editButton: Button
    private lateinit var usernameView: TextView
    private lateinit var emailView: TextView
    private lateinit var nomorTeleponView: TextView
    private lateinit var genderView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_view)

        profileImageView = findViewById(R.id.profileImageView)
        editButton = findViewById(R.id.editButton)
        usernameView = findViewById(R.id.usernameView)
        emailView = findViewById(R.id.emailView)
        nomorTeleponView = findViewById(R.id.nomorTeleponView)
        genderView = findViewById(R.id.genderView)

        // Load profile image
        Glide.with(this)
            .load("https://example.com/profile.jpg") // Replace with your image URL
            .into(profileImageView)

        // Retrieve and display profile data from intent extras
        intent?.let {
            usernameView.text = it.getStringExtra("username") ?: "john_doe"
            emailView.text = it.getStringExtra("email") ?: "john@example.com"
            nomorTeleponView.text = it.getStringExtra("nomor_telepon") ?: "081234567890"
            genderView.text = it.getStringExtra("gender") ?: "Male"
        }

        editButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivityForResult(intent, 2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            usernameView.text = data.getStringExtra("username")
            emailView.text = data.getStringExtra("email")
            nomorTeleponView.text = data.getStringExtra("nomor_telepon")
            genderView.text = data.getStringExtra("gender")
        }
    }
}
