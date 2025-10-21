package com.michaelrmossman.docoutdoors.utils

import androidx.annotation.DimenRes
import androidx.annotation.PluralsRes
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.michaelrmossman.docoutdoors.OutdoorsApplication.Companion.instance
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.FilterType
import java.util.Locale

/**
 * Text utility functions used throughout the app
 */
object TextUtils {

    @Composable
    @ReadOnlyComposable
    fun fontDimensionResource(@DimenRes id: Int) =
        dimensionResource(id = id).value.sp

    @Composable
    fun getBoldLabelWithText(
        @StringRes labelStringId: Int,
        plainText: String,
        boldLabel: Boolean = true
    ) : AnnotatedString {
        return buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = when (boldLabel) {
                        true -> FontWeight.Bold
                        else -> FontWeight.Normal
                    }
                )
            ) {
                append(stringResource(labelStringId))
            }
            append (" ")
            append(plainText)
        }
    }

    @Composable
    fun getListHeaderText(
        listFilterRegion: String?,
        listSize: Int?,
        @PluralsRes pluralsIdFiltered: Int,
        @StringRes stringIdUnfiltered: Int,
        alertsFilterBy: Int? = 0,
        bookable      : Int? = 0,
        dogAccess     : Int? = 0
    ) : String {
        val headerText = when (listSize) {
            null -> String() // Basically hides text until ready
            else -> when (listFilterRegion?.isNotBlank() == true) {
                true -> pluralStringResource(
                    pluralsIdFiltered,
                    count = listSize,
                    listSize,
                    listFilterRegion
                )
                else -> stringResource(
                    stringIdUnfiltered,
                    listSize
                )
            }
        }
        return when (
            alertsFilterBy != 0 || bookable != 0 || dogAccess != 0
        ) {
            true -> FILTERED_ASTERISK.plus(headerText)
            else -> headerText
        }
    }

    @Composable
    fun getHighlightedSearchResult(
        searchQuery: String,
        searchResult: String
    ) : AnnotatedString {
        return when ( // Most likely superfluous
            searchResult.lowercase(
                Locale.getDefault()
            ).contains(searchQuery.lowercase(
                Locale.getDefault()
            ))
        ) {
            false -> buildAnnotatedString {
                append(searchResult)
            }
            else -> {
                val sections = searchResult.split(
                    searchQuery, ignoreCase = false
                )
                buildAnnotatedString {
                    sections.forEachIndexed { index, section ->
                        append(section)
                        if (index < sections.size.minus(1)) {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(searchQuery)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun getListSubHeaders(
        listFilterBookable: Int? = null,
        listFilterDogs: Int? = null
    ) : List<String> {
        val subHeaders = mutableListOf<String>()
        listFilterBookable?.let { filterBookable ->
            if (filterBookable > 0) {
                subHeaders.add(
                    stringResource(
                        R.string.common_filtered_bookable
                    )
                )
            }
        }
        listFilterDogs?.let { filterDogs ->
            if (filterDogs > 0) {
                subHeaders.add(
                    getStringFromArray(
                        R.array.filter_dogs_by,
                        filterDogs
                    )
                )
            }
        }
        return subHeaders
    }

    @Composable
    fun getMapsSubtitle(
        itemType: FilterType,
        region: String?
    ) : String? {
        return when (region) {
            null -> null
            String() -> stringResource(
                R.string.map_subtitle_all,
                itemType.name.lowercase()
            )
            else -> stringResource(
                R.string.map_subtitle_filt,
                itemType.name.lowercase(),
                region
            )
        }
    }

    fun getSearchResultWithExtras(
        name: String,
        extras: String
    ) : String {
        return String.format(
            instance.resources.getString(
                R.string.search_result_with_extras
            ),
            name, extras
        )
    }

    fun getSnippetForCampsiteOrHut(
        region: String?,
        status: String
    ) : String {
        return when (region == null || region.isBlank()) {
            true -> String()
            else -> String.format(
                instance.resources.getString(
                    when (
                        status.startsWith(
                            prefix = "CL",
                            ignoreCase = true
                        )
                    ) {
                        true -> R.string.snippet_closed
                        else -> R.string.snippet_open
                    }
                ),
                region
            )
        }
    }

    @Composable
    fun getStringFromArray(
        @ArrayRes arrayId: Int,
        arrayIndex: Int
    ) : String {
        return stringArrayResource(
            arrayId
        )[
            arrayIndex
        ]
    }
}