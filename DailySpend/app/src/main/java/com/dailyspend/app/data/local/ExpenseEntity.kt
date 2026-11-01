package com.dailyspend.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val merchant: String,
    val bank: String,
    val category: String,
    val date: String,       // Format: YYYY-MM-DD
    val time: String,       // Format: hh:mm a (e.g., "02:45 PM")
    val fullSms: String,
    val isAuto: Boolean,    // true if parsed automatically from incoming SMS
    val createdAt: Long = System.currentTimeMillis()
)
