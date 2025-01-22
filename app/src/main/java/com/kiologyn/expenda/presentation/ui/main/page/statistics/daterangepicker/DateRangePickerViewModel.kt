package com.kiologyn.expenda.presentation.ui.main.page.statistics.daterangepicker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.kiologyn.expenda.presentation.ui.main.page.statistics.daterangepicker.page.DateRangePickerPage
import java.time.LocalDate
import kotlin.reflect.full.createInstance


class DateRangePickerViewModel : ViewModel() {
    private var pages: List<DateRangePickerPage> =
        DateRangePickerPage::class.sealedSubclasses.map { it.createInstance() }

    var page: DateRangePickerPage by mutableStateOf(pages.first())
        private set

    var dateRange: ClosedRange<LocalDate>
        get() = page.dateRange.value
        set(value) {
            page.dateRange.value = value
        }

    fun getPageCount() = pages.size

    fun getPage(index: Int) = pages[index]

    fun changePage(index: Int) {
        page = pages[index]
    }
}