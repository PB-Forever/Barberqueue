package com.example.barberqueue

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class DetailTransaksiActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var uploadProofButton: Button
    private val PICK_IMAGE_REQUEST = 71

    private var transactionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_transaksi)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        transactionId = intent.getStringExtra("transactionId")

        val transactionImageView: ImageView = findViewById(R.id.transaction_image_view)
        val timestampTextView: TextView = findViewById(R.id.timestamp_text_view)
        val timeTextView: TextView = findViewById(R.id.time_text_view)
        val barberTextView: TextView = findViewById(R.id.barber_text_view)
        val totalPaymentTextView: TextView = findViewById(R.id.total_payment_text_view)
        val serviceChoiceTextView: TextView = findViewById(R.id.service_choice_text_view)
        uploadProofButton = findViewById(R.id.upload_proof_button)

        if (transactionId != null) {
            fetchTransactionData(transactionId!!, transactionImageView, timestampTextView, timeTextView, barberTextView, totalPaymentTextView, serviceChoiceTextView)
        } else {
            Toast.makeText(this, "Transaction ID is missing", Toast.LENGTH_SHORT).show()
            finish()
        }

        uploadProofButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }
        val backButton : Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val filePath = data.data
            uploadImage(filePath)
        }
    }

    private fun uploadImage(filePath: Uri?) {
        if (filePath != null) {
            val ref = storage.reference.child("transImage/${UUID.randomUUID()}")
            ref.putFile(filePath)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        saveImageUrlToFirestore(uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        if (transactionId != null) {
            val transactionData = mapOf(
                "buktiTransUrl" to imageUrl
            )
            firestore.collection("transactions").document(transactionId!!)
                .update(transactionData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
                    fetchTransactionData(transactionId!!, findViewById(R.id.transaction_image_view), findViewById(R.id.timestamp_text_view), findViewById(R.id.time_text_view), findViewById(R.id.barber_text_view), findViewById(R.id.total_payment_text_view), findViewById(R.id.service_choice_text_view))
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving image URL to Firestore: " + e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun fetchTransactionData(transactionId: String, transactionImageView: ImageView, timestampTextView: TextView, timeTextView: TextView, barberTextView: TextView, totalPaymentTextView: TextView, serviceChoiceTextView: TextView) {
        firestore.collection("transactions").document(transactionId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val barberId = document.getString("barberId") ?: ""
                    val buktiTransUrl = document.getString("buktiTransUrl") ?: ""
                    val servicesId = document.get("servicesId") as? List<String> ?: listOf()
                    val timeDate = document.getTimestamp("time_date")?.toDate()
                    val userId = document.getString("userId") ?: ""

                    if (buktiTransUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(buktiTransUrl)
                            .into(transactionImageView)
                    }

                    timestampTextView.text = "Timestamp: ${timeDate?.toString() ?: "N/A"}"
                    timeTextView.text = "Time: ${timeDate?.toString() ?: "N/A"}"
                    barberTextView.text = "Barber: $barberId"
                    totalPaymentTextView.text = "Total Payment: 50000" // Static total payment for now
                    serviceChoiceTextView.text = "Service Choice: ${servicesId.joinToString(", ")}"
                } else {
                    Toast.makeText(this, "Transaction data not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching transaction data: " + e.message, Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}