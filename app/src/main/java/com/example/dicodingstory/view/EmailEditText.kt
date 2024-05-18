package com.example.dicodingstory.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.dicodingstory.R

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val emailPattern = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")

        if (!s.toString().matches(emailPattern)) {
            setError(context.getString(R.string.invalid_email), null)
        } else {
            error = null
        }
    }
}