package com.example.aicomsapp.viewmodels.dashboard

import android.graphics.Color
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.aicomsapp.R
import com.example.aicomsapp.viewmodels.response.ImprestFund
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImprestFundAdapterDashboard(
    private var imprestFundsList: List<ImprestFund>,
    private val onItemClick: (ImprestFund) -> Unit,
    private val onPictureClick: (ImprestFund) -> Unit,
    private val onPhotoSudahClick: (ImprestFund) -> Unit,
    private val onTransactionTypeChange: (ImprestFund, String) -> Unit// Pass the clicked item data
) : RecyclerView.Adapter<ImprestFundAdapterDashboard.ImprestFundViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImprestFundViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_view_imprest_admin, parent, false)
        return ImprestFundViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImprestFundViewHolder, position: Int) {
        val imprestFund = imprestFundsList[position]
        holder.bind(imprestFund)

        // Set the click listener on the ViewHolder
        holder.itemView.setOnClickListener {
            onItemClick(imprestFund) // Pass the clicked item to the listener
        }
        holder.picture.setOnClickListener {
            onPictureClick(imprestFund) // Add this line to handle picture click
        }
        holder.photoSudah.setOnClickListener {
            onPhotoSudahClick(imprestFund)
        }
        holder.itemView.setOnLongClickListener {
            val newTransactionType = if (imprestFund.transactionType == "out") "in" else "out"
            onTransactionTypeChange(imprestFund, newTransactionType)
            true
        }
        val context = holder.itemView.context
        if (position == 0) {
            holder.itemView.background = AppCompatResources.getDrawable(context, R.drawable.rounder_top)
        } else {
            holder.itemView.background = AppCompatResources.getDrawable(context, R.drawable.no_rounded)
        }
    }
    fun updateData(newImprestFundsList: List<ImprestFund>) {
        imprestFundsList = newImprestFundsList
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return imprestFundsList.size
    }

    class ImprestFundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userIdTextView: TextView = itemView.findViewById(R.id.textViewUserId)
        private val inputDateTextView: TextView = itemView.findViewById(R.id.textViewInputDate)
        private val transactionDateTextView: TextView = itemView.findViewById(R.id.textViewTransDate)
        private val amountTextView: TextView = itemView.findViewById(R.id.textViewAmount)
        public val picture: View = itemView.findViewById(R.id.imageViewPic)
        public val photoSudah: View = itemView.findViewById(R.id.imageViewphotosudah)
        private val sudah: View = itemView.findViewById(R.id.sudahBackground)
        private val belum: View = itemView.findViewById(R.id.belumBackground)
        private val sudahTextView: TextView = itemView.findViewById(R.id.textViewSudah)
        private val belumTextView: TextView = itemView.findViewById(R.id.textViewBelum)

        fun bind(imprestFund: ImprestFund) {
            userIdTextView.text = "User ID: ${imprestFund.name}"

            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            // Parse tanggal input
            val inputDate = inputDateFormat.parse(imprestFund.inputDate)
            val formattedInputDate = outputDateFormat.format(inputDate)
            inputDateTextView.text = "Tanggal Input: $formattedInputDate"

            // Parse tanggal transaksi
            val transactionDate = inputDateFormat.parse(imprestFund.transactionDate)
            val formattedTransactionDate = outputDateFormat.format(transactionDate)
            transactionDateTextView.text = "Tanggal Transaksi: $formattedTransactionDate"
            amountTextView.text = "Jumlah Uang: Rp ${imprestFund.amount}"
            Log.d("StatusCheck", "Status: ${imprestFund.status}")
            when (imprestFund.status) {
                "done" -> {
                    sudah.visibility = View.VISIBLE
                    sudahTextView.setTextColor(Color.WHITE)

                    belum.visibility = View.GONE
                    belumTextView.setTextColor(Color.BLACK)

                    picture.visibility = View.GONE
                    photoSudah.visibility = View.VISIBLE
                }

                "undone" -> {
                    belum.visibility = View.VISIBLE
                    belumTextView.setTextColor(Color.WHITE)

                    sudah.visibility = View.GONE
                    sudahTextView.setTextColor(Color.BLACK)

                    picture.visibility = View.VISIBLE
                    photoSudah.visibility = View.GONE

                }
        }
    }
}
}
