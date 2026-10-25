package com.example.patik.ui.component

import androidx.annotation.DrawableRes
import com.example.patik.R
import com.example.patik.model.SockType

@DrawableRes
fun SockType.toDrawableRes(): Int = when (this) {
    SockType.BABET_INCE -> R.drawable.sock_babet_ince
    SockType.BABET_SPOR -> R.drawable.sock_babet_spor
    SockType.YAGMUR_INCE -> R.drawable.sock_yagmur_ince
    SockType.YAGMUR_CIZME -> R.drawable.sock_yagmur_cizme
    SockType.KLASIK_PAMUKLU -> R.drawable.sock_klasik_pamuklu
    SockType.KALIN_BOGAZLI -> R.drawable.sock_kalin_bogazli
    SockType.YUN_PATIK_BOT -> R.drawable.sock_yun_patik
}
