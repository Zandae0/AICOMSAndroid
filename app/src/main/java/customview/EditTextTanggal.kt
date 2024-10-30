package customview


import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditTextTanggal : AppCompatEditText {

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
        // Mengatur format tanggal
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Calendar.getInstance().time)

        // Mengisi EditText dengan tanggal saat ini
        setText(currentDate)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak perlu melakukan apa-apa sebelum teks berubah
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Tidak perlu melakukan apa-apa saat teks sedang berubah
            }

            override fun afterTextChanged(s: Editable?) {
                // Anda bisa menambahkan validasi atau logika tambahan di sini jika diperlukan
            }
        })
    }
}