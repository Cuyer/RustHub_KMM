package pl.cuyer.rusthub.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual fun formatLocalDate(date: LocalDate): String {
    val formatter = NSDateFormatter()
    formatter.dateStyle = NSDateFormatterMediumStyle
    formatter.timeStyle = NSDateFormatterNoStyle
    formatter.locale = NSLocale.currentLocale
    val instant = date.atStartOfDayIn(TimeZone.currentSystemDefault())
    val nsDate = instant.toNSDate()
    return formatter.stringFromDate(nsDate)
}
