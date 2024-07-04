package com.example.barberqueue

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

//class TransactionListActivity : AppCompatActivity() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: TransactionAdapter
//    private lateinit var firestore: FirebaseFirestore
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_transaction_list)
//
//        firestore = FirebaseFirestore.getInstance()
//
//        recyclerView = findViewById(R.id.transaction_recycler_view)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.addItemDecoration(MarginItemDecoration(20))
//
//        adapter = TransactionAdapter(listOf())
//        recyclerView.adapter = adapter
//
//        fetchTransactions()
//    }
//
//    private fun fetchTransactions() {
//        lifecycleScope.launch {
//            try {
//                val transactions = withContext(Dispatchers.IO) {
//                    fetchTransactionList()
//                }
//                adapter.updateTransactionList(transactions)
//            } catch (e: Exception) {
//                Toast.makeText(this@TransactionListActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private suspend fun fetchTransactionList(): List<Transaction> {
//        val transactions = firestore.collection("transactions")
//            .get()
//            .await()
//
//        return transactions.documents.mapNotNull { document ->
//            val timeStamp = document.getTimestamp("time_date")?.toDate()
//            val fee = document.getString("fee")
//            val barberName = document.getString("barberName")
//
//            if (timeStamp != null && fee != null && barberName != null) {
//                Transaction(timeStamp, fee, barberName)
//            } else {
//                null
//            }
//        }
//    }
//}