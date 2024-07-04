package com.example.barberqueue

import BarberAdapter
import DateAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReserveTimeBarberActivity : AppCompatActivity(), DateOnClickListener, BarberOnClickListener{

    private lateinit var recyclerView1: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var adapter1: DateAdapter
    private lateinit var adapter2: BarberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve_time_barber)

        recyclerView1 = findViewById(R.id.recycle_date)
        recyclerView1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.addItemDecoration(MarginItemDecoration(20))

        recyclerView2 = findViewById(R.id.recycle_barber)
        recyclerView2.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView2.addItemDecoration(MarginItemDecoration(20))

        val itemList1 = getDateList()
        val itemList2 = getBarberList()

        adapter1 = DateAdapter(itemList1, this)
        recyclerView1.adapter = adapter1

        adapter2 = BarberAdapter(itemList2, this)
        recyclerView2.adapter = adapter2

        val button: Button = findViewById(R.id.choose_services_btn)
        button.setOnClickListener {
            val intent = Intent(this, ChooseSerivesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getBarberList(): List<Barber> {
        return mutableListOf(
            Barber("Mas Agus", "mas_agus.jpeg", "@mas_agus"),
            Barber("Mas Agus", "mas_agus.jpeg", "@mas_agus"),
            Barber("Mas Agus", "mas_agus.jpeg", "@mas_agus"),
            Barber("Mas Agus", "mas_agus.jpeg", "@mas_agus"),
            Barber("Mas Agus", "mas_agus.jpeg", "@mas_agus"),
            Barber("Mas Agus", "mas_agus.jpeg", "@mas_agus"),
        )
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

    override fun onClick(date: Date) {
            val data = SimpleDateFormat("EEEE\nd MMM", Locale.getDefault()).format(date)
            Toast.makeText(this, data,Toast.LENGTH_SHORT).show()
    }

    override fun onClick(name: String) {
        Toast.makeText(this, name,Toast.LENGTH_SHORT).show()
    }

}