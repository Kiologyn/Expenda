package com.kiologyn.expenda.ui.navigation.page.statistics.component.balancetrend

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.kiologyn.expenda.R
import com.kiologyn.expenda.adjustToNearestDay
import com.kiologyn.expenda.formatDate
import com.kiologyn.expenda.toLocalDateTime


class BalanceMarkerView(context: Context) : MarkerView(context, R.layout.balance_marker) {

    private val textView: TextView

    init {
        addView(TextView(context).apply {
            textView = this

            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(Color.WHITE)
            textSize = 10f
            textAlignment = TEXT_ALIGNMENT_CENTER
        })
    }

    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        if (entry == null) return

        val date = entry.x.toLong().toLocalDateTime().adjustToNearestDay()
        val balance = entry.y

        textView.text = "$balance\n${date.formatDate()}"

        super.refreshContent(entry, highlight)
    }

    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        return MPPointF(-width/2f, -height*1.3f)
    }
}