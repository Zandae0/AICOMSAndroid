package com.example.aicomsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aicomsapp.R
import com.example.aicomsapp.viewmodels.response.ImprestFund

class ImprestFundAdapter(private val imprestFunds: List<ImprestFund>) :
    RecyclerView.Adapter<ImprestFundAdapter.ImprestFundViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImprestFundViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_view_imprest_admin, parent, false)
        return ImprestFundViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImprestFundViewHolder, position: Int) {
        val imprestFund = imprestFunds[position]
        holder.bind(imprestFund)
    }

    override fun getItemCount(): Int {
        return imprestFunds.size
    }

    class ImprestFundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userIdTextView: TextView = itemView.findViewById(R.id.textViewUserId)
        private val inputDateTextView: TextView = itemView.findViewById(R.id.textViewInputDate)
        private val transactionDateTextView: TextView = itemView.findViewById(R.id.textViewTransDate)
        private val amountTextView: TextView = itemView.findViewById(R.id.textViewAmount)

        fun bind(imprestFund: ImprestFund) {
            userIdTextView.text = "User ID: ${imprestFund.name}"
            inputDateTextView.text = "Tanggal Input: ${imprestFund.inputDate}"
            transactionDateTextView.text = "Tanggal Transaksi: ${imprestFund.transactionDate}"
            amountTextView.text = "Jumlah Uang: Rp ${imprestFund.amount}"

            // Update status based on transactionType or some other logic
        }
    }
}
