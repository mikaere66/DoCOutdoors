package com.michaelrmossman.docoutdoors.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.core.net.toUri
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.model.Alert
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Extension functions used throughout the app
 */

@Composable
fun Alert.getRegionText(
    showAll: Boolean
) : AnnotatedString {
    val regions = StringBuilder()
    this.regions.forEachIndexed { index, region ->
        if (region.regionName.isNotBlank()) {
            if (index > 0) {
                regions.append(ITEM_SEPARATOR)
            }
            regions.append(region.regionName)
        }
    }
    if (regions.toString().isBlank()) {
        regions.append(stringResource(R.string.region_unknown))
    }
    val regionLabel = pluralStringResource(
        R.plurals.common_regions,
        count = this.regions.size
    )
    return buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = when (showAll) {
                    true -> FontWeight.Normal
                    else -> FontWeight.Bold
                }
            )
        ) {
            append(regionLabel)
        }
        append (" ")
        append(regions.toString())
    }
}

fun Collection<LatLng>.toLatLngBounds(): LatLngBounds {
    if (isEmpty()) error("Cannot create LatLngBounds from an empty list")

    return LatLngBounds.builder().apply {
        for (latLng in this@toLatLngBounds) { include(latLng) }
    }.build()
}

fun ComponentActivity.setEdgeToEdgeConfig() {
    enableEdgeToEdge()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Force the 3-button navigation bar to be transparent
        window.isNavigationBarContrastEnforced = false
    }
}

fun Context.showAdvSearchNotAvailToast() {
    Toast.makeText(
        this,
        R.string.search_advanced_not_available,
        Toast.LENGTH_LONG
    ).show()
}

fun Context.openUrlInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = url.toUri()
    }
    startActivity(intent)
}

fun Double.isNegative(): Boolean {
    return this < 0.0
}

fun Long.parseMillisToKiwiDate(): String {
    return SimpleDateFormat(
        /* UK uses lowercase AM/PM */
        KIWI_UPDATE_FORMAT, Locale.UK
    ).format(Date(this))
}

@Composable
fun String.markerColors(): IconColor {
    val backgroundAlpha = MAP_MARKER_BACKGROUND_ALPHA
    return when (this.startsWith("CL")) {
        true -> IconColor(
            iconColor = MaterialTheme.colorScheme.secondary, // CLSD
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                alpha = backgroundAlpha
            ),
            borderColor = MaterialTheme.colorScheme.secondary
        )
        else -> IconColor(
            iconColor = MaterialTheme.colorScheme.onPrimary, // OPEN
            backgroundColor = MaterialTheme.colorScheme.primary.copy(
                alpha = backgroundAlpha
            ),
            borderColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun String.fromHtml(): AnnotatedString {
    return AnnotatedString.Companion.fromHtml(
        htmlString = this,
        linkStyles = TextLinkStyles(
            style = SpanStyle(
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.primary
            )
        )
    )
}

fun String.parseKiwiDateToMillis(): Long {
    val dateFormat = SimpleDateFormat(
        KIWI_UPDATE_FORMAT, Locale.getDefault()
    )
    val date = dateFormat.parse(this)
    return date?.time ?: 0L // Note elvis op
}

fun String.parseStringDateToMillis(): Long {
    return try {
        val dateFormat = SimpleDateFormat(
            LAST_UPDATE_FORMAT, Locale.getDefault()
        )
        val date = dateFormat.parse(this)
        date?.time ?: 0L // Note elvis op
    }
    catch (exception: ParseException) {
        println(exception.message)
        0L
    }
}

/* Hawke's Bay has weird apostrophe */
fun String.replaceApos(): String {
    return this.replace("’","'")
}

/* Descriptions contain line breaks */
fun String.replaceCRLF(): String {
    return this.replace(
        "\n"," "
    ).replace(
        "\r"," "
    )
}

/* ignoreCase doesn't seem to work */
fun String.replaceMacrons(): String {
    return this
        .replace("Ā","A")
        .replace("Ē","E")
        .replace("Ī","I")
        .replace("Ō","O")
        .replace("Ū","U")
}

@Composable
fun String.toHtml(): String {
    val html = StringBuilder()
    html.append("<A href='")
    html.append(this)
    html.append("'>")
    html.append(this)
    html.append("</A>")
    return  html.toString()
}