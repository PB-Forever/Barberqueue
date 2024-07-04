package com.example.barberqueue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date

data class Transaction(
    val timeStamp: Date,
    val fee: String,
    val barberName:String
)
//class TransactionAdapter(private var transactionList: List<Transaction>) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
//
//    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val dateTextView: TextView = view.findViewById(R.id.transaction_date_time)
//        val feeTextView: TextView = view.findViewById(R.id.transaction_fee)
//        val barberNameTextView: TextView = view.findViewById(R.id.transaction_barber_name)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
//    }
//
//    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
//        val transaction = transactionList[position]
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
//
//        holder.dateTextView.text = dateFormat.format(transaction.timeStamp)
//        holder.feeTextView.text = transaction.fee
//        holder.barberNameTextView.text = transaction.barberName
//    }
//
//    override fun getItemCount(): Int {
//        return transactionList.size
//    }
//
//    fun updateTransactionList(newTransactionList: List<Transaction>) {
//        transactionList = newTransactionList
//        notifyDataSetChanged()
//    }
//}