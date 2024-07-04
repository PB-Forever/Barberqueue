package com.example.barberqueue

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.barberqueue.databinding.ActivityMainBinding
import com.example.barberqueue.slidepromo.BarberAdapter
import com.example.barberqueue.slidepromo.PromoAdapter
import com.example.barberqueue.slidepromo.PromoList
import com.example.barberqueue.sliderservice.ServiceAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var sliderHandler: Handler = Handler()
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val sliderItem: List<PromoList> = listOf(
            PromoList(R.drawable.promo_banner),
            PromoList(R.drawable.promo_banner1),
            PromoList(R.drawable.promo_banner),
            PromoList(R.drawable.promo_banner1),
        )
        binding.viewPromoPager.adapter = PromoAdapter(sliderItem, binding.viewPromoPager)
        binding.viewPromoPager.clipToPadding = false
        binding.viewPromoPager.clipChildren = false
        binding.viewPromoPager.offscreenPageLimit = 5
        binding.viewPromoPager.getChildAt(0).overScrollMode = ViewPager2.OVER_SCROLL_NEVER
        binding.indicatorPromo.setViewPager(binding.viewPromoPager)
        binding.viewPromoPager.registerOnPageChangeCallback( object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 5000)
            }
        })
        val sliderBarberItem: List<Int> = listOf(
            R.drawable.lilul,
            R.drawable.lilul,
            R.drawable.lilul,
        )
        binding.rvBarber.adapter = BarberAdapter(sliderBarberItem)
        binding.rvBarber.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        val sliderServiceItem: List<String> = listOf(
            "Haircut",
            "Hair Styling",
            "Hair Treatment",
            "Head Massage",
            "Shampoo"
        )
        binding.rvService.adapter = ServiceAdapter(sliderServiceItem)
        binding.rvService.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.buttonHome

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.buttonHome -> true
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
                R.id.buttonProfile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                else -> false
            }
        }
        loadUser()
    }

    private fun loadUser() {
        val userId = auth.currentUser?.uid
        userId?.let {
            db.collection("users").document(it).get().addOnSuccessListener { document ->
                if (document != null) {
                    val username = document.getString("username")
                    val usernameTopBanner: TextView = findViewById(R.id.userTv)
                    usernameTopBanner.text = username
                }
            }
        }
    }

    private var sliderRunnable: Runnable = object : Runnable {
        override fun run() {
            var isLastItem:Boolean = false
            if (binding.viewPromoPager.currentItem == binding.viewPromoPager.adapter?.itemCount!! - 1){
                isLastItem = true
            } else if (binding.viewPromoPager.currentItem == 0){
                isLastItem = false
            }
            if (isLastItem){
                binding.viewPromoPager.setCurrentItem(0, true)
            } else {
                binding.viewPromoPager.setCurrentItem(binding.viewPromoPager.currentItem + 1, true)
            }
        }
    }
}