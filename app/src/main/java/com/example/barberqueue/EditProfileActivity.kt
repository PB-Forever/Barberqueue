package com.example.barberqueue

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.mindrot.jbcrypt.BCrypt

class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var profileImageView: ImageView
    private lateinit var uploadImageButton: Button
    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var nomorTeleponInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var saveButton: Button

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        profileImageView = findViewById(R.id.profileImageView)
        uploadImageButton = findViewById(R.id.uploadImageButton)
        usernameInput = findViewById(R.id.usernameInput)
        emailInput = findViewById(R.id.emailInput)
        nomorTeleponInput = findViewById(R.id.nomorTeleponInput)
        passwordInput = findViewById(R.id.passwordInput)
        saveButton = findViewById(R.id.saveButton)

        loadProfileData()

        uploadImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        saveButton.setOnClickListener {
            saveProfileData()
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
                R.id.buttonProfile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    finish()
                    true
                }
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

                    usernameInput.setText(username)
                    emailInput.setText(email)
                    nomorTeleponInput.setText(nomorTelepon)

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

    private fun saveProfileData() {
        val userId = auth.currentUser?.uid
        val username = usernameInput.text.toString()
        val email = emailInput.text.toString()
        val nomorTelepon = nomorTeleponInput.text.toString()
        val password = passwordInput.text.toString()

        val userUpdates = hashMapOf<String, Any>()

        if (username.isNotEmpty()) {
            userUpdates["username"] = username
        }

        if (email.isNotEmpty()) {
            userUpdates["email"] = email
        }

        if (nomorTelepon.isNotEmpty()) {
            userUpdates["nomorTelepon"] = nomorTelepon
        }

        if (password.isNotEmpty() && password.length >= 8) {
            val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
            userUpdates["password"] = hashedPassword
        }

        imageUri?.let {
            val ref = storageReference.child("profileImage/$userId")
            ref.putFile(it).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    userUpdates["profileImageRef"] = ref.path
                    updateUserDocument(userId, userUpdates)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        } ?: updateUserDocument(userId, userUpdates)
    }

    private fun updateUserDocument(userId: String?, userUpdates: Map<String, Any>) {
        userId?.let {
            db.collection("users").document(it).update(userUpdates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            profileImageView.setImageURI(imageUri)
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 71
    }
}