package com.dailyspend.app.parser

import java.util.regex.Pattern

data class ParsedExpense(
    val amount: Double,
    val merchant: String,
    val bank: String,
    val category: String,
    val isValid: Boolean,
    val filterReason: String? = null
)

object SmsExpenseParser {

    private val EXPENSE_KEYWORDS = listOf("charged", "debited", "spent", "purchase", "transaction", "paid")
    private val IGNORE_KEYWORDS = listOf("otp", "verification code", "code is", "one-time", "secret code", "do not share")

    fun parse(smsBody: String): ParsedExpense? {
        val lower = smsBody.lowercase()

        // 1. Ignore if contains OTP / verification codes
        for (ignore in IGNORE_KEYWORDS) {
            if (lower.contains(ignore)) {
                return null
            }
        }

        // 2. Filter: must contain one of debit keywords
        val hasKeyword = EXPENSE_KEYWORDS.any { lower.contains(it) }
        if (!hasKeyword) {
            return null
        }

        // 3. Amount Regex: $45.00, Rs 450, USD 45.00
        val amount = extractAmount(smsBody) ?: return null

        // 4. Detect Bank
        val bank = detectBank(smsBody)

        // 5. Detect Merchant
        val merchant = extractMerchant(smsBody)

        // 6. Auto-categorize
        val category = categorize(merchant, smsBody)

        return ParsedExpense(
            amount = amount,
            merchant = merchant,
            bank = bank,
            category = category,
            isValid = true
        )
    }

    private fun extractAmount(text: String): Double? {
        val pattern = Pattern.compile("(?:\\$|USD\\s?|Rs\\.?\\s?)([0-9]+(?:,[0-9]{3})*(?:\\.[0-9]{1,2})?)", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(text)
        if (matcher.find()) {
            val numStr = matcher.group(1)?.replace(",", "") ?: return null
            return numStr.toDoubleOrNull()
        }
        return null
    }

    private fun detectBank(text: String): String {
        val lower = text.lowercase()
        return when {
            lower.contains("chase") || lower.contains("jpmorgan") -> "Chase"
            lower.contains("bank of america") || lower.contains("bofa") -> "Bank of America"
            lower.contains("wells fargo") -> "Wells Fargo"
            lower.contains("american express") || lower.contains("amex") -> "American Express"
            lower.contains("citi") -> "Citi"
            lower.contains("capital one") -> "Capital One"
            else -> "Other"
        }
    }

    private fun extractMerchant(text: String): String {
        // Look after "at", "to", "for"
        val pattern = Pattern.compile("\\b(?:at|to|for)\\s+([A-Za-z0-9\\s&'./#\\-_]+?)(?=\\s+(?:on|with|card|ending|from|using|available|\\.|\\,|$))", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(text)
        if (matcher.find()) {
            val raw = matcher.group(1)?.trim()?.replace("[.,:;!]+$".toRegex(), "")
            if (!raw.isNullOrEmpty() && raw.length >= 2) {
                return cleanMerchant(raw)
            }
        }
        return "Unknown Merchant"
    }

    private fun categorize(merchant: String, fullText: String): String {
        val m = merchant.lowercase()
        return when {
            m.contains("starbucks") || m.contains("mcdonald") || m.contains("chipotle") ||
            m.contains("doordash") || m.contains("uber eats") || m.contains("cafe") || m.contains("restaurant") -> "Food & Dining"

            m.contains("whole foods") || m.contains("trader joe") || m.contains("kroger") ||
            m.contains("costco") || m.contains("safeway") || m.contains("grocery") -> "Groceries"

            m.contains("target") || m.contains("walmart") || m.contains("amazon") ||
            m.contains("best buy") || m.contains("zara") || m.contains("nike") -> "Shopping"

            m.contains("uber") || m.contains("lyft") || m.contains("shell") ||
            m.contains("chevron") || m.contains("delta") || m.contains("gas") -> "Travel"

            m.contains("netflix") || m.contains("spotify") || m.contains("hulu") ||
            m.contains("steam") || m.contains("amc") -> "Entertainment"

            m.contains("cvs") || m.contains("walgreens") || m.contains("pharmacy") -> "Health"

            m.contains("at&t") || m.contains("verizon") || m.contains("electric") -> "Utilities"

            else -> "Shopping"
        }
    }

    private fun cleanMerchant(raw: String): String {
        return raw.split(" ")
            .joinToString(" ") { word ->
                if (word.length <= 1) word.uppercase()
                else word.replaceFirstChar { it.uppercase() }
            }
    }
}
