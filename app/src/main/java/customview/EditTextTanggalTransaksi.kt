package customview

import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.DatePicker
import androidx.appcompat.widget.AppCompatEditText
import com.example.aicomsapp.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditTextTanggalTransaksi : AppCompatEditText, DatePickerDialog.OnDateSetListener {

    private val calendar = Calendar.getInstance()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        // Menetapkan format input untuk dd/MM/yyyy
        filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            val input = (dest as? Editable)?.toString() ?: ""
            val newInput = source.toString()
            val combinedInput = input + newInput

            // Validasi format dd/MM/yyyy
            if (combinedInput.matches(Regex("\\d{0,2}/?\\d{0,2}/?\\d{0,4}"))) {
                null // Input diperbolehkan
            } else {
                "" // Input tidak diperbolehkan
            }
        })

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.length == 2 || text.length == 5) {
                    if (!text.endsWith("/")) {
                        s?.insert(s.length - 1, "/")
                    }
                }
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            showDatePicker()
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, this, year, month, day).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        setText(dateFormat.format(calendar.time))
    }
}
