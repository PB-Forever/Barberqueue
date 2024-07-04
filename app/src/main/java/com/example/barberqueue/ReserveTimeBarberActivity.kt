package com.example.barberqueue

import BarberAdapter
import DateAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ReserveTimeBarberActivity : AppCompatActivity(), DateOnClickListener, BarberOnClickListener {

    private lateinit var recyclerView1: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var adapter1: DateAdapter
    private lateinit var adapter2: BarberAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var flexboxLayout: FlexboxLayout
    private var selectedRadioButton: RadioButton? = null
    private lateinit var proceedButton: Button

    private var selectedDate: Date? = null
    private var selectedTime: String? = null
    private var selectedBarber: String? = null
    private var totalPayment: String = "50000" // Contoh total pembayaran
    private var serviceChoice: String = "Haircut" // Contoh pilihan layanan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve_time_barber)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        recyclerView1 = findViewById(R.id.recycle_date)
        recyclerView1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.addItemDecoration(MarginItemDecoration(20))

        recyclerView2 = findViewById(R.id.recycle_barber)
        recyclerView2.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView2.addItemDecoration(MarginItemDecoration(20))

        flexboxLayout = findViewById(R.id.flexbox_layout)
        addTimeRadioButtons()

        val itemList1 = getDateList()
        adapter1 = DateAdapter(itemList1, this)
        recyclerView1.adapter = adapter1
        selectFirstDate()

        val initialBarberList = listOf<Barber>() // Empty initial list
        adapter2 = BarberAdapter(initialBarberList, this)
        recyclerView2.adapter = adapter2

        proceedButton = findViewById(R.id.choose_services_btn)
        proceedButton.setOnClickListener {
            proceedToDetailTransaction()
        }
    }

    private fun getDateList(): List<Date> {
        val dates = mutableListOf<Date>()

        val calendar = Calendar.getInstance()
        val today = calendar.time

        // Get the day of the month for today
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Set the calendar to the next month
        calendar.add(Calendar.MONTH, 1)
        // Adjust if the current month has fewer days than the next month
        val maxDayInNextMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        if (currentDay > maxDayInNextMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, maxDayInNextMonth)
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, currentDay)
        }

        val nextMonthSameDay = calendar.time

        // Add dates from today to the same date next month
        calendar.time = today
        while (calendar.time.before(nextMonthSameDay)) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        dates.add(nextMonthSameDay)

        return dates
    }

    @SuppressLint("ResourceType")
    private fun addTimeRadioButtons() {
        val startHour = 9
        val endHour = 22
        for (hour in startHour..endHour) {
            val radioButton = RadioButton(this).apply {
                val margin = dpToPx(10)
                layoutParams = FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(margin, margin, margin, margin)
                    flexGrow = 1.0F
                }
                text = String.format(Locale.getDefault(), "%02d:00", hour)
                setBackgroundResource(R.drawable.date_reserve_selector)
                setTextColor(ContextCompat.getColorStateList(context, R.drawable.text_date_selector))
                minHeight = dpToPx(48)
                minWidth = dpToPx(72)
                setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12))
                setButtonDrawable(android.R.color.transparent)
                gravity = android.view.Gravity.CENTER
                textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER
                setOnClickListener {
                    selectedRadioButton?.isChecked = false
                    selectedRadioButton = this
                    isChecked = true
                    selectedTime = text.toString()
                    checkAndFetchAvailableBarbers()
                }
            }
            flexboxLayout.addView(radioButton)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onClick(date: Date) {
        val data = SimpleDateFormat("EEEE\nd MMM", Locale.getDefault()).format(date)
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show()
        selectedDate = date
        checkAndFetchAvailableBarbers()
    }

    override fun onClick(name: String) {
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
    }

    private fun checkAndFetchAvailableBarbers() {
        if (selectedDate != null && selectedTime != null) {
            fetchAvailableBarbersForDateAndTime(selectedDate!!, selectedTime!!)
        }
    }

    private fun fetchAvailableBarbersForDateAndTime(date: Date, time: String) {
        lifecycleScope.launch {
            try {
                val barbers = withContext(Dispatchers.IO) {
                    fetchBarbersForDateAndTime(date, time)
                }
                adapter2.updateBarberList(barbers)
            } catch (e: Exception) {
                Toast.makeText(this@ReserveTimeBarberActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun fetchBarbersForDateAndTime(date: Date, time: String): List<Barber> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = Timestamp(calendar.time)

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endOfDay = Timestamp(calendar.time)

        val transactions = firestore.collection("transactions")
            .whereGreaterThanOrEqualTo("time_date", startOfDay)
            .whereLessThanOrEqualTo("time_date", endOfDay)
            .whereEqualTo("time", time) // Assuming you have a field "time" in your transactions
            .get()
            .await()
        val bookedBarberIds = transactions.documents.mapNotNull { it.getString("barberId") }

        val barbers = firestore.collection("barbers").get().await()
        return barbers.documents.map { document ->
            val name = document.getString("name") ?: ""
            val imageUrlRef = document.get("photo") as? String
            val instagramHandle = document.getString("ig") ?: ""
            val isAvailable = !bookedBarberIds.contains(document.id)

            Barber(document.id, name, instagramHandle, imageUrlRef ?: "", isAvailable)
        }
    }

    private fun selectFirstDate() {
        if (adapter1.itemCount > 0) {
            val firstDate = adapter1.getItem(0)
            onClick(firstDate)
            // Update the adapter or view to show the selected state
            // Assuming the adapter has a method to set the selected position
            adapter1.setSelectedPosition(0)
        }
    }

    private fun proceedToDetailTransaction() {
        val transactionData = mapOf(
            "barberId" to selectedBarber,
            "time_date" to Timestamp(selectedDate!!),
            "time" to selectedTime,
            "servicesId" to listOf(serviceChoice),
            "status" to 1,
            "userId" to "current_user_id" // Replace with the actual user ID
        )

        firestore.collection("transactions")
            .add(transactionData)
            .addOnSuccessListener { documentReference ->
                val intent = Intent(this, DetailTransaksiActivity::class.java)
                intent.putExtra("transactionId", documentReference.id)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving transaction: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}