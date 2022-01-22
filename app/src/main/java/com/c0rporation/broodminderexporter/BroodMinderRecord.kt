package com.c0rporation.broodminderexporter

data class BroodMinderRecord(val deviceId: String, val timestamp: Int, val temperature: Float, val humidity: Int, val battery: Int)
