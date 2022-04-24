package com.spacenine.dora.model.request

import com.spacenine.nemo.BuildConfig

data class CellInfo(
    val token: String = BuildConfig.OPENCELLID_API_KEY,
    var radio: String? = null,
    var mcc: Int? = null,
    var mnc: Int? = null,
    var cells: List<Cell> = emptyList(),
    val address: Int = 1
)

data class Cell(
    val lac: Int,
    val cid: Int,
    val psc: Int? = null
)

object RadioType {
    const val GSM = "gsm"
    const val CDMA = "cdma"
    const val UMTS = "umts"
    const val LTE = "lte"
}