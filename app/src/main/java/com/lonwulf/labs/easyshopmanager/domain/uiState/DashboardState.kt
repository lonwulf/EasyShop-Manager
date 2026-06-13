package com.lonwulf.labs.easyshopmanager.domain.uiState

import com.lonwulf.labs.easyshopmanager.core.domain.model.Product
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.time.Clock

data class DashboardState(
//    val productsTotal: Int = 0,
//    val lowStockTotal: Int = 0,
//    val inventoryValue: Double = 0.0,
//    val lowStock: List<Product> = emptyList(),
//    val recentActivity: List<RecentActivity> = emptyList()

    //dummy
    val productsTotal: Int = 1000,
    val lowStockTotal: Int = 13,
    val inventoryValue: Double = 400044020.0,
    val lowStock: List<Product> = listOf(
        Product(
            id = 1,
            name = "Awei headphones",
            categoryId = 10,
            subCategoryId = 20,
            price = 1000.0,
            buyingPrice = 650.0,
            quantity = 2
        ),
        Product(
            id = 1,
            name = "Awei headphones",
            categoryId = 10,
            subCategoryId = 20,
            price = 1000.0,
            buyingPrice = 650.0,
            quantity = 2
        )
    ),
    val recentActivity: List<RecentActivity> = listOf(
        RecentActivity(
            productName = "Awei headphones",
            qty = 2,
            dateTime = "2023-01-01 12:00:00",
            action = ActivityAction.QTY_INCREASE
        ),
        RecentActivity(
            productName = "Awei headphones",
            qty = 3,
            dateTime = "2023-01-01 12:00:00",
            action = ActivityAction.QTY_DECREASE
        ),
        RecentActivity(
            productName = "Awei headphones",
            qty = 3,
            dateTime = "2023-01-01 12:00:00",
            action = ActivityAction.DESCRIPTION
        )
    )
)

data class RecentActivity(
    val productName: String,
    val qty: Int,
    val dateTime: String,
    val action: ActivityAction
)

fun Number.thousandFormatter(decimalPlaces: Int = 2): String {
    val pattern = if (decimalPlaces <= 0) {
        "#,###"
    } else {
        "#,###." + "0".repeat(decimalPlaces)
    }

    val formatter = DecimalFormat(
        pattern,
        DecimalFormatSymbols(Locale.getDefault())
    )

    return formatter.format(this)
}
fun String.editedHowLongAgo(): String {
    try {
        // 1. Convert your custom "yyyy-MM-dd HH:mm:ss" space format to ISO-8601 (replace space with 'T')
        // kotlinx-datetime requires ISO format: "2023-01-01T12:00:00"
        val isoString = this.replace(" ", "T")

        val pastDateTime = LocalDateTime.parse(isoString)
        val systemZone = TimeZone.currentSystemDefault()

        // 2. Convert to Instants to find the exact duration gap
        val pastInstant = pastDateTime.toInstant(systemZone)
        val nowInstant = Clock.System.now()

        val duration = nowInstant - pastInstant

        // 3. Break down the duration components
        val seconds = duration.inWholeSeconds
        val minutes = duration.inWholeMinutes
        val hours = duration.inWholeHours
        val days = duration.inWholeDays

        // 4. Return relative string
        return when {
            seconds < 60 -> "just now"
            minutes == 1L -> "1 minute ago"
            minutes < 60 -> "$minutes minutes ago"
            hours == 1L -> "1 hour ago"
            hours < 24 -> "$hours hours ago"
            days == 1L -> "1 day ago"
            days < 30 -> "$days days ago"
            days < 365 -> "${days / 30} months ago"
            else -> "${days / 365} years ago"
        }
    } catch (e: Exception) {
        return "recently"
    }
}

enum class ActivityAction(val actionName: String) {
    QTY_INCREASE("quantity increased"), QTY_DECREASE("quantity decreased"),
    DESCRIPTION("updated product description"), IMAGE_INPUT("updated product image"),
    PRICE_CHANGE("price change");
}

