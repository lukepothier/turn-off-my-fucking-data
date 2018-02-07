package com.lukepothier.turnoffmyfuckingdata

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText


class CancellableEditText (context: Context, attrs: AttributeSet)
    : EditText(context, attrs) {

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            return super.dispatchKeyEvent(event)
        }
        return super.dispatchKeyEvent(event)
    }
}