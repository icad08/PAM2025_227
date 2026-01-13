package com.example.nguliner.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    fun toRupiah(price: Int): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 0
        return numberFormat.format(price).replace("Rp", "Rp ")
    }
}