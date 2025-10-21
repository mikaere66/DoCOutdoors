package com.michaelrmossman.docoutdoors.ui.alerts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring.DampingRatioLowBouncy
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.model.AffectedEntity
import com.michaelrmossman.docoutdoors.utils.MapUtils.getAffectedDrawableId
import com.michaelrmossman.docoutdoors.utils.MapUtils.isValidCoords
import com.michaelrmossman.docoutdoors.utils.openUrlInBrowser

@Composable
fun AffectedItemCard(
    affected: AffectedEntity,
    affectedIndex: Int,
    alertIndex: Int,
    navigateToAffectedMap: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardCornerShape = dimensionResource(R.dimen.card_corner_shape)
    val cardElevation = dimensionResource(R.dimen.card_elevation)
    val cardOuterPadding = dimensionResource(R.dimen.padding_mini)
    val columnVerticalPadding = dimensionResource(R.dimen.padding_small)
    val context = LocalContext.current
    val docLink: () -> Unit = {
        context.openUrlInBrowser(affected.docUrl)
    }
    val docLinkText = buildAnnotatedString {
        val uriHandler = LocalUriHandler.current
        val link = LinkAnnotation.Url(
            affected.docUrl,
            TextLinkStyles(
                SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    color = Color.Blue
                )
            )
        ) { url ->
            val uri = (url as LinkAnnotation.Url).url
            uriHandler.openUri(uri)
        }
        withLink(link) {
            append(
                stringResource(
                    R.string.alert_details_doc_link
                )
            )
        }
    }
    val isValidCoords = isValidCoords(
        affected.lat, affected.lon
    )
    val mapDrawableId = when (isValidCoords) {
        true -> R.drawable.outline_place_black_24
        else -> R.drawable.outline_not_listed_location_black_24
    }
    val mapLink: (() -> Unit)? = {
        when (isValidCoords) {
            true -> { navigateToAffectedMap(affectedIndex, alertIndex) }
            else -> null
        }
    }
    val mapText = stringResource(
        R.string.alerts_affected_map
    )
    val modifierAlpha = Modifier.alpha(
        when (isValidCoords) {
            true -> 1.0F
            else -> 0.6F
        }
    )
    val textHorizontalPadding = dimensionResource(R.dimen.padding_medium)
    val typeText = stringResource(
        R.string.alerts_affected_type,
        affected.type
    )
    var isVisible by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = cardOuterPadding),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        shape = RoundedCornerShape(size = cardCornerShape)
    ) {
        Column(
            modifier = modifier.padding(vertical = columnVerticalPadding),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.padding_small)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isVisible = !isVisible
                    }
            ) {
                Text(
                    text = affected.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(
                            horizontal = textHorizontalPadding
                        )
                        .weight(1F)
                )
                Icon(
                    imageVector = when (isVisible) {
                        true -> Icons.Outlined.ExpandLess
                        else -> Icons.Outlined.ExpandMore
                    },
                    contentDescription = stringResource(
                        R.string.toggle_item_visibility
                    ),
                    modifier = Modifier.padding(
                        horizontal = textHorizontalPadding
                    )
                )
            }
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = spring(dampingRatio = DampingRatioLowBouncy)
                ),
                exit = fadeOut(),
                modifier = modifier
            ) {
                Column(
                    modifier = modifier.fillMaxWidth()
                    .padding(
                        vertical = columnVerticalPadding,
                        horizontal = textHorizontalPadding
                    )
                ) {
                    AffectedItemRow(
                        contentDescription = null,
                        drawableId = getAffectedDrawableId(
                            itemType = affected.type
                        ),
                        text = buildAnnotatedString {
                            append(typeText)
                        }
                    )
                    AffectedItemRow(
                        contentDescription = stringResource(
                            R.string.alerts_affected_link_desc
                        ),
                        drawableId =
                            R.drawable.outline_open_in_browser_black_24,
                        onIconClick = docLink,
                        text = docLinkText
                    )
                    AffectedItemRow(
                        contentDescription = stringResource(
                            R.string.menu_map_one
                        ),
                        drawableId = mapDrawableId,
                        modifier = modifierAlpha,
                        onIconClick = mapLink,
                        text = buildAnnotatedString {
                            append(mapText)
                        }
                    )
                }
            }
        }
    }
}