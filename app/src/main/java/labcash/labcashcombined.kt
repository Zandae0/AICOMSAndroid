package com.example.aicomsapp.viewmodels.labcash


import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.aicomsapp.R
import com.example.aicomsapp.viewmodels.response.LabCash
import java.text.SimpleDateFormat
import java.util.Locale

class LabCashAdapter(
    private var labCashList: List<LabCash>,
    private val onItemClick: (LabCash) -> Unit,
    private val onPictureClick: (LabCash) -> Unit,
    private val onPhotoSudahClick: (LabCash) -> Unit,
    private val onTransactionTypeChange: (LabCash, String) -> Unit// Pass the clicked item data
) : RecyclerView.Adapter<LabCashAdapter.LabCashViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabCashViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_view_labcash, parent, false)
        return LabCashViewHolder(view)
    }

    override fun onBindViewHolder(holder: LabCashViewHolder, position: Int) {
        val labCash = labCashList[position]
        holder.bind(labCash)

        // Set the click listener on the ViewHolder
        holder.itemView.setOnClickListener {
            onItemClick(labCash) // Pass the clicked item to the listener
        }
        holder.picture.setOnClickListener {
            onPictureClick(labCash) // Add this line to handle picture click
        }
        holder.photoSudah.setOnClickListener {
            onPhotoSudahClick(labCash)
        }
        holder.itemView.setOnLongClickListener {
            val newTransactionType = if (labCash.transactionType == "out") "in" else "out"
            onTransactionTypeChange(labCash, newTransactionType)
            true
        }
        val context = holder.itemView.context
        if (position == 0) {
            holder.itemView.background =
                AppCompatResources.getDrawable(context, R.drawable.rounder_top)
        } else {
            holder.itemView.background =
                AppCompatResources.getDrawable(context, R.drawable.no_rounded)
        }
    }
    fun updateData(newLabCashList: List<LabCash>) {
        labCashList = newLabCashList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return labCashList.size
    }


    class LabCashViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userIdTextView: TextView = itemView.findViewById(R.id.textViewUserId)
        private val inputDateTextView: TextView = itemView.findViewById(R.id.textViewInputDate)
        private val amountTextView: TextView = itemView.findViewById(R.id.textViewAmount)
        public val picture: View = itemView.findViewById(R.id.imageViewPic)
        public val photoSudah: View = itemView.findViewById(R.id.imageViewphotosudah)
        private val masuk: View = itemView.findViewById(R.id.masukBackground)
        private val keluar: View = itemView.findViewById(R.id.keluarBackground)
        private val donasi: View = itemView.findViewById(R.id.donasiBackground)
        private val masukTextView: TextView = itemView.findViewById(R.id.textViewMasuk)
        private val keluarTextView: TextView = itemView.findViewById(R.id.textViewKeluar)
        private val donasiTextView: TextView = itemView.findViewById(R.id.textViewDonasi)

        fun bind(labCash: LabCash) {
            userIdTextView.text = "User ID: ${labCash.name}"

            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            // Parse tanggal input
            val inputDate = inputDateFormat.parse(labCash.inputDate)
            val formattedInputDate = outputDateFormat.format(inputDate)
            inputDateTextView.text = "Tanggal Input: $formattedInputDate"
            amountTextView.text = "Jumlah Uang: Rp ${labCash.amount}"
            when (labCash.transactionType) {
                "in" -> {
                    masuk.visibility = View.VISIBLE
                    masukTextView.setTextColor(Color.WHITE)

                    keluar.visibility = View.GONE
                    keluarTextView.setTextColor(Color.BLACK)

                    donasi.visibility = View.GONE
                    donasiTextView.setTextColor(Color.BLACK)

                    picture.visibility = View.GONE
                    photoSudah.visibility = View.VISIBLE// Show image for "in" transaction
                }

                "out" -> {
                    keluar.visibility = View.VISIBLE
                    keluarTextView.setTextColor(Color.WHITE)

                    masuk.visibility = View.GONE
                    masukTextView.setTextColor(Color.BLACK)

                    donasi.visibility = View.GONE
                    donasiTextView.setTextColor(Color.BLACK)

                    picture.visibility = View.VISIBLE
                    photoSudah.visibility = View.GONE// Show image for "out" transaction

                }

                "donation" -> {
                    donasi.visibility = View.VISIBLE
                    donasiTextView.setTextColor(Color.WHITE)

                    masuk.visibility = View.GONE
                    masukTextView.setTextColor(Color.BLACK)

                    keluar.visibility = View.GONE
                    keluarTextView.setTextColor(Color.BLACK)

                    picture.visibility = View.GONE
                    photoSudah.visibility = View.GONE// Hide image for "donation" transaction

                }
            }
        }
    }
    fun updateTransactionType(labCash: LabCash, newTransactionType: String) {
        onTransactionTypeChange(labCash, newTransactionType) // Panggil callback saat transaksi berubah
    }
}
