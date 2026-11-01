package com.dailyspend.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity): Long

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllExpensesFlow(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date = :todayDate ORDER BY createdAt DESC")
    fun getTodayExpensesFlow(todayDate: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date LIKE :monthPrefix || '%' ORDER BY date DESC, time DESC")
    fun getExpensesForMonthFlow(monthPrefix: String): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date = :todayDate")
    fun getTodayTotalSpend(todayDate: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE date LIKE :monthPrefix || '%'")
    fun getMonthlyTotalSpend(monthPrefix: String): Flow<Double?>

    @Query("DELETE FROM expenses")
    suspend fun clearAllExpenses()
}
