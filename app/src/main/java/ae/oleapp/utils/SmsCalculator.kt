package ae.oleapp.utils

data class SmsCalculationResult(
    val totalSms: Int,
    val totalCost: Double,
    val hasEnoughSms: Boolean
)

object SmsCalculator {

    /**
     * Calculate SMS count and cost based on message, recipient count, SMS cost, and user SMS balance.
     */
    fun calculateSmsDetails(
        message: String,
        recipientCount: Int,
        smsCost: Double,
        userAvailableSms: Int
    ): SmsCalculationResult {

        if (message.isEmpty() || recipientCount == 0) {
            return SmsCalculationResult(0, 0.0, true)
        }

        val (charCount, charsPerSms) = countCharsAndSmsLimit(message)
        val smsCount = ((charCount - 1) / charsPerSms) + 1

        val totalSms = smsCount * recipientCount
        val totalCost = totalSms * smsCost
        val hasEnoughSms = userAvailableSms >= totalSms

        return SmsCalculationResult(totalSms, totalCost, hasEnoughSms)
    }

    /**
     * Count message characters respecting Arabic & emoji rules.
     * Returns Pair<effectiveCharCount, charsAllowedPerSms>
     */
    private fun countCharsAndSmsLimit(message: String): Pair<Int, Int> {
        var charCount = 0
        var containsArabic = false

        message.forEach { ch ->
            when {
                isArabicChar(ch) -> {
                    charCount += 2
                    containsArabic = true
                }
                isEmoji(ch) -> {
                    charCount += 2
                }
                else -> {
                    charCount += 1
                }
            }
        }

        val charsPerSms = if (containsArabic) 60 else 140
        return Pair(charCount, charsPerSms)
    }

    /**
     * Detect if a char is Arabic based on Unicode blocks
     */
    private fun isArabicChar(ch: Char): Boolean {
        val block = Character.UnicodeBlock.of(ch)
        return block == Character.UnicodeBlock.ARABIC ||
                block == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A ||
                block == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B
    }

    /**
     * Simple emoji detection using Unicode ranges
     */
    private fun isEmoji(ch: Char): Boolean {
        val type = Character.getType(ch)
        // Unicode categories for emojis (some common ones)
        return (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt())
    }
}

