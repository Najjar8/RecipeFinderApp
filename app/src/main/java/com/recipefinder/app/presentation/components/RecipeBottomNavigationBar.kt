package com.recipefinder.app.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.recipefinder.app.R
import com.recipefinder.app.presentation.navigation.Screen

private data class BottomNavItem(
    val screen:       Screen,
    val label:        Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val NAV_ITEMS = listOf(
    BottomNavItem(Screen.Home,      R.string.nav_home,      Icons.Filled.Home,          Icons.Outlined.Home),
    BottomNavItem(Screen.Search,    R.string.nav_search,    Icons.Filled.Search,        Icons.Outlined.Search),
    BottomNavItem(Screen.Favorites, R.string.nav_favorites, Icons.Filled.Favorite,      Icons.Outlined.FavoriteBorder),
)

@Composable
fun RecipeBottomNavigationBar(
    currentDestination: NavDestination?,
    onNavigate: (Screen) -> Unit,
    favoriteBadgeCount: Int = 0,
) {
    NavigationBar {
        NAV_ITEMS.forEach { item ->
            val selected = currentDestination
                ?.hierarchy
                ?.any { it.route == item.screen.route } == true

            val showBadge = item.screen == Screen.Favorites && favoriteBadgeCount > 0 && !selected

            NavigationBarItem(
                selected = selected,
                onClick  = { onNavigate(item.screen) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (showBadge) {
                                Badge { Text(favoriteBadgeCount.toString()) }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = stringResource(item.label),
                        )
                    }
                },
                label = { Text(stringResource(item.label)) },
            )
        }
    }
}
