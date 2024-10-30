package customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.aicomsapp.R

class EditTextPassword : AppCompatEditText {

    private var isPasswordVisible: Boolean = false
    private lateinit var eyeIcon: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForDrawables", "ClickableViewAccessibility")
    private fun init() {
        eyeIcon = ContextCompat.getDrawable(context, R.drawable.ic_eye)!!
        eyeIcon.setBounds(0, 0, eyeIcon.intrinsicWidth, eyeIcon.intrinsicHeight)
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, eyeIcon, null)

        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (right - compoundPaddingRight)) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length < 8) {
                    setError(context.getString(R.string.passwordwarning), null)
                } else {
                    error = null
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun togglePasswordVisibility() {
        val cursorPosition = selectionStart
        isPasswordVisible = !isPasswordVisible
        transformationMethod = if (isPasswordVisible) {
            null
        } else {
            PasswordTransformationMethod.getInstance()
        }
        setSelection(cursorPosition)
    }
}
