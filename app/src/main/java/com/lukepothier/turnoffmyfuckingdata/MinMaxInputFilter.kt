package com.lukepothier.turnoffmyfuckingdata

import android.text.InputFilter
import android.text.Spanned

class MinMaxInputFilter(min: Int, max: Int) : InputFilter {

    private var min: Int = 0
    private var max: Int = 0

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        try {
            if (source.count() == 1 && source[0] == '-')
                return null

            // TODO [LP] :: Allow '.' character once per input

            val input = Integer.parseInt(dest.toString() + source.toString())

            if (isInRange(min, max, input))
                return null
        } catch (nfe: NumberFormatException) {
        }

        return ""
    }

    private fun isInRange(min: Int, max: Int, input: Int): Boolean {
        return if (max > min)
            input in min..max
        else
            input in max..min
    }

    init {
        this.min = min
        this.max = max
    }
}
