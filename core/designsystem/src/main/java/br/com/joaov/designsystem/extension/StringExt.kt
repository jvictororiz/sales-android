package br.com.joaov.designsystem.extension

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale


fun Double.toMoney(): String {
    val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    format.currency = Currency.getInstance("BRL")
    return format.format(this.toFloat()).orEmpty()
}

fun String.moneyToDouble(): Double {
    try {
        return if (this.contains("$")) {
            val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
            format.currency = Currency.getInstance("BRL")
            return format.parse(this)?.toDouble() ?: 0.0
        } else {
            this.toDouble()
        }
    } catch (exception: Exception) {
        return 0.0
    }
}