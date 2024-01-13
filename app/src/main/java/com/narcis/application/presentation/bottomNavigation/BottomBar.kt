package com.narcis.application.presentation.bottomNavigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.narcis.application.presentation.ui.theme.narcisApplicationTheme
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi


@Composable
fun BottomBar(
    navController: NavHostController,
    currentDestination: NavDestination?,
    isDarkTheme: Boolean
) {
    NavigationBar(
        modifier = Modifier
            .height(52.dp)
            .shadow(elevation = 16.dp)
            .padding(top = 2.dp)
    ) {
        BottomBarDestination.values().asList().forEach {
            BottomItem(it, navController, currentDestination, isDarkTheme)
        }
    }
}

@Composable
fun RowScope.BottomItem(
    screen: BottomBarDestination,
    navController: NavHostController,
    currentDestination: NavDestination?,
    isDarkTheme: Boolean
) {
    val isCurrentBottomItemSelected = currentDestination?.hierarchy?.any {
        it.route == screen.route.route
    } ?: false

    val (iconSize, offsetY) = Pair(22.dp, 0.dp)

//        if (screen == BottomBarDestination.Home) Pair(42.dp, (-8).dp)
//    else Pair(22.dp, 0.dp)

    var icon: Int = screen.unFilledIcon
    screen.apply {
//        if (this == BottomBarDestination.Home) {
//            if (isDarkTheme) darkModeIcon?.let { icon = it }
//        } else {
        if (isCurrentBottomItemSelected) {
            filledIcon?.let { icon = it }
        }
//        }
    }
    NavigationBarItem(
        modifier = Modifier.offset(y = -BottomBarItemVerticalOffset),
        label = {
            screen.title?.let {
                Text(
                    modifier = Modifier.offset(y = BottomBarItemVerticalOffset.times(1.85f)),
                    text = stringResource(id = screen.title),
                    style = MaterialTheme.typography.labelSmall,
                    softWrap = false,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (isCurrentBottomItemSelected) 1f else 0.7f)
                )
            }
        },
        icon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                Modifier
                    .padding(bottom = 9.dp)
                    .size(iconSize)
                    .offset(y = offsetY),
                tint = Color.Unspecified,
            )
        },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = MaterialTheme.colorScheme.background,
            selectedIconColor = MaterialTheme.colorScheme.secondary,
            selectedTextColor = MaterialTheme.colorScheme.secondary
        ),
        selected = isCurrentBottomItemSelected,
        onClick = {
            screen.route.let {
                navController.navigate(it.route) {
                    launchSingleTop = true
                }
            }
        }
    )
}

private val BottomBarItemVerticalOffset = 10.dp

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterialApi::class)
@Preview
@Composable
fun DemoBottom() {
    narcisApplicationTheme{
        val modalSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = false,
        )
        val bottomSheetNavigator = remember(modalSheetState) {
            BottomSheetNavigator(modalSheetState)
        }  //rememberBottomSheetNavigator()
        val navController = rememberNavController(bottomSheetNavigator)
        val currentBackStackEntryAsState by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStackEntryAsState?.destination
        BottomBar(navController, currentDestination, isDarkTheme = true)
    }
}