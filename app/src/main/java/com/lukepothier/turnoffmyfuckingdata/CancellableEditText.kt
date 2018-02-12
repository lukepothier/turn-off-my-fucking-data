package com.lukepothier.turnoffmyfuckingdata

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText

class CancellableEditText : EditText {

    private var _listener: IOnBackButtonListener? = null

    interface IOnBackButtonListener {
        fun onEditTextBackButton(): Boolean
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun setOnBackButtonListener(l: IOnBackButtonListener) {
        _listener = l
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            if (_listener != null && _listener!!.onEditTextBackButton())
                return false
        }

        return super.onKeyPreIme(keyCode, event)
    }
}
