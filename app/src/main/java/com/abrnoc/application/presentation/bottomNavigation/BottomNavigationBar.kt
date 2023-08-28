package com.abrnoc.application.presentation.bottomNavigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.core.os.ConfigurationCompat
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.abrnoc.application.presentation.components.CustomSurface
import com.abrnoc.application.presentation.screens.Landing
import com.abrnoc.application.presentation.ui.UiConstants.BottomNavHeight
import com.abrnoc.application.presentation.ui.UiConstants.BottomNavIndicatorShape
import com.abrnoc.application.presentation.ui.UiConstants.BottomNavigationItemPadding
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme
import com.abrnoc.application.presentation.ui.theme.ApplicationTheme
import java.util.Locale

@Composable
fun BottomNavigationBar(
    tabs: Array<HomeSection>,
    currentRoute: String,
    navigateRoute: (String) -> Unit,
    color: Color = ApplicationTheme.colors.navigationPrimary
) {
    val routes = remember {
        tabs.map { it.route }
    }
    val currentSection = tabs.first { it.route == currentRoute }
    CustomSurface(color = color) {
        val springSpec = SpringSpec<Float>(
            stiffness = 800f,
            dampingRatio = 0.8f
        )
        BottomNavLayout(
            selectedIndex = currentSection.ordinal,
            itemCount = routes.size,
            animSpec = springSpec,
            indicator = { BottomNavIndicator() },
            modifier = Modifier.navigationBarsPadding()
        ) {
            val configuration = LocalConfiguration.current
            val currentLocal: Locale =
                ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()
            tabs.forEach { section ->
                val selected = section == currentSection
                val tint by animateColorAsState(
                    if (selected)
                        ApplicationTheme.colors.navigationPrimary
                    else
                        ApplicationTheme.colors.iconInteractiveInactive, label = ""
                )

                val text = stringResource(section.title).uppercase(currentLocal)
                BottomNavigationItem(
                    icon =
                    if (selected) {
                        section.iconSelected
                    } else {
                        section.icon
                    },
                    text = {
                        Text(
                            text = text,
                            color = tint,
                            style = MaterialTheme.typography.button,
                            maxLines = 1
                        )
                    },
                    selected = selected,
                    onSelected = { navigateRoute(section.route) },
                    animSpec = springSpec,
                    tint = tint,
                    modifier = BottomNavigationItemPadding.clip(BottomNavIndicatorShape)
                )

            }
        }


    }
}

@Composable
fun BottomNavLayout(
    selectedIndex: Int,
    itemCount: Int,
    animSpec: AnimationSpec<Float>,
    indicator: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val selectionFractions = remember(itemCount) {
        List(itemCount) { i ->
            Animatable(if (i == selectedIndex) 1f else 0f)
        }
    }
    selectionFractions.forEachIndexed { index, animatable ->
        val target = if (index == selectedIndex) 1f else 0f
        LaunchedEffect(target, animSpec) {
            animatable.animateTo(target, animSpec)

        }
    }
    val indicatorIndex = remember {
        Animatable(0f)
    }
    val targetIndicatorIndex = selectedIndex.toFloat()
    LaunchedEffect(targetIndicatorIndex) {
        indicatorIndex.animateTo(targetIndicatorIndex, animSpec)
    }
    Layout(modifier = modifier.height(BottomNavHeight), content = {
        content()
        Box(Modifier.layoutId("indicator"), content = indicator)
    }) { measurables, constraints ->
        check(itemCount == (measurables.size - 1))
        val unselectedWidth = constraints.maxWidth / (itemCount + 1)
        val selectedWidth = (4/3) * unselectedWidth
        val indicatorMeasurable = measurables.first { it.layoutId == "indicator" }

        val itemPlaceables = measurables
            .filterNot { it == indicatorMeasurable }
            .mapIndexed { index, measurable ->
                // Animate item's width based upon the selection amount
                val width = lerp(unselectedWidth, selectedWidth, selectionFractions[index].value)
                measurable.measure(
                    constraints.copy(
                        minWidth = width,
                        maxWidth = width,
                    ),
                )
            }

        val indicatorPlaceable = indicatorMeasurable.measure(
            constraints.copy(
                minWidth = selectedWidth,
                maxWidth = selectedWidth,
            ),
        )
        layout(
            width = constraints.maxWidth,
            height = itemPlaceables.maxByOrNull { it.height }?.height ?: 0,
        ) {
            val indicatorLeft = indicatorIndex.value * unselectedWidth
            indicatorPlaceable.placeRelative(x = indicatorLeft.toInt(), y = 0)
            var x = 0
            itemPlaceables.forEach { placeable ->
                placeable.placeRelative(x = x, y = 0)
                x += placeable.width
            }
        }
    }
}

@Composable
private fun BottomNavIndicator(
    strokeWidth: Dp = 2.dp,
    color: Color = ApplicationTheme.colors.navigationPrimary,
    shape: Shape = BottomNavIndicatorShape,
) {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .then(BottomNavigationItemPadding)
            .border(strokeWidth, color, shape),
    )
}

fun NavGraphBuilder.addHomeGraph(
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    composable(HomeSection.LANDING.route) {
        Landing(onNavigateToRoute = onNavigateToRoute, modifier = modifier)
    }
}

@Preview
@Composable
private fun BottomNavPreview() {
    AbrnocApplicationTheme {
        BottomNavigationBar(
            tabs = HomeSection.values(),
            currentRoute = "home/landing",
            navigateRoute = {}
        )
    }
}