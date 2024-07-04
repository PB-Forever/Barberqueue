package com.example.barberqueue

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ReserveServicesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve_services)

        val button: Button = findViewById(R.id.reserve_btn)
        button.setOnClickListener {
            val intent = Intent(this, ReserveTimeBarberActivity::class.java)
            startActivity(intent)
        }
    }
}