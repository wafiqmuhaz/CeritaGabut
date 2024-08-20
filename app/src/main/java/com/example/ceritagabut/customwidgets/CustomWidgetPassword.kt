package com.example.ceritagabut.customwidgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.ceritagabut.R

class CustomWidgetPassword : AppCompatEditText {
    private var iconFormInput: Drawable? = null

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
        iconFormInput = ContextCompat.getDrawable(context, R.drawable.baseline_phonelink_lock_24)
        showIconFormInput()
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val charLength = s?.length ?: 0
                error = if (!s.isNullOrEmpty() && charLength < 8) {
                    context.getString(R.string.user_invalid_password)
                } else null
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showIconFormInput() {
        iconFormInput?.let { setCompoundDrawablesWithIntrinsicBounds(it, null, null, null) }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        context.apply {
            setTextColor(ContextCompat.getColor(this, R.color.blue_4))
            setHintTextColor(ContextCompat.getColor(this, R.color.blue_4))
            background = ContextCompat.getDrawable(this, R.drawable.custom_widget_form_input)
        }
        maxLines = 1
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        transformationMethod = PasswordTransformationMethod.getInstance()
    }
}