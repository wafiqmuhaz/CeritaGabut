package com.example.ceritagabut.customwidgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.ceritagabut.R

class CustomWidgetEmail : AppCompatEditText {
    private lateinit var iconFormInput: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        iconFormInput = ContextCompat.getDrawable(context, R.drawable.baseline_alternate_email_24)!!
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                showIconFormInput()
                val errorMessage = if (s.isNotEmpty() && !isValidEmail(s)) {
                    context.getString(R.string.user_invalid_email)
                } else null
                error = errorMessage
            }

            override fun afterTextChanged(s: Editable?) {}

            private fun isValidEmail(email: CharSequence): Boolean {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        })
    }

    private fun showIconFormInput() {
        setCompoundDrawablesWithIntrinsicBounds(iconFormInput, null, null, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        context.apply {
            setTextColor(ContextCompat.getColor(this, R.color.blue_4))
            setHintTextColor(ContextCompat.getColor(this, R.color.blue_4))
            background = ContextCompat.getDrawable(this, R.drawable.custom_widget_form_input)
        }
        isSingleLine = true
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}