package com.example.myapplication

import java.time.LocalTime

object RFIDManager {
    private val openStartTime = LocalTime.of(6, 30)
    private val openEndTime = LocalTime.of(12, 0)
    private val closeStartTime = LocalTime.of(13, 0)
    private val closeEndTime = LocalTime.of(16, 30)

    fun isValidScanTime(): Boolean {
        val currentTime = LocalTime.now()
        return (currentTime.isAfter(openStartTime) && currentTime.isBefore(openEndTime)) ||
                (currentTime.isAfter(closeStartTime) && currentTime.isBefore(closeEndTime))
    }

    fun isOpenScanTime(): Boolean {
        val currentTime = LocalTime.now()
        return currentTime.isAfter(openStartTime) && currentTime.isBefore(openEndTime)
    }

    fun isCloseScanTime(): Boolean {
        val currentTime = LocalTime.now()
        return currentTime.isAfter(closeStartTime) && currentTime.isBefore(closeEndTime)
    }
}