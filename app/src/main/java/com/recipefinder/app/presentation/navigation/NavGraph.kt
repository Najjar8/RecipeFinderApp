package com.recipefinder.app.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.recipefinder.app.core.constants.AppConstants
import com.recipefinder.app.presentation.MainViewModel
import com.recipefinder.app.presentation.components.RecipeBottomNavigationBar
import com.recipefinder.app.presentation.detail.RecipeDetailScreen
import com.recipefinder.app.presentation.favorites.FavoritesScreen
import com.recipefinder.app.presentation.home.HomeScreen
import com.recipefinder.app.presentation.search.SearchScreen
import com.recipefinder.app.presentation.addrecipe.AddRecipeScreen
private const val TRANSITION_DURATION = AppConstants.ANIMATION_DURATION_MS

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val favoriteBadgeCount by mainViewModel.favoriteBadgeCount.collectAsStateWithLifecycle()

    // Determine whether the bottom bar should be visible
    val showBottomBar = Screen.bottomNavItems.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                RecipeBottomNavigationBar(
                    currentDestination = currentDestination,
                    favoriteBadgeCount = favoriteBadgeCount,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            // Pop back stack to the start destination to avoid
                            // building up a large back stack on tab switches.
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        SharedTransitionLayout {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding),
                enterTransition = {
                    fadeIn(tween(TRANSITION_DURATION))
//               + slideIntoContainer(
//                    AnimatedContentTransitionScope.SlideDirection.Start,
//                    tween(TRANSITION_DURATION)
//                )
                },
                exitTransition = {
                    fadeOut(tween(TRANSITION_DURATION))
//               + slideOutOfContainer(
//                    AnimatedContentTransitionScope.SlideDirection.Start,
//                    tween(TRANSITION_DURATION)
//                )
                },
                popEnterTransition = {
                    fadeIn(tween(TRANSITION_DURATION))
//               + slideIntoContainer(
//                    AnimatedContentTransitionScope.SlideDirection.End,
//                    tween(TRANSITION_DURATION)
//                )
                },
                popExitTransition = {
                    fadeOut(tween(TRANSITION_DURATION))
//               + slideOutOfContainer(
//                    AnimatedContentTransitionScope.SlideDirection.End,
//                    tween(TRANSITION_DURATION)
//                )
                },
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onRecipeClick    = { navController.navigate(Screen.RecipeDetail.createRoute(it)) },
                        onAddRecipeClick = { navController.navigate(Screen.AddRecipe.route) },
                        animatedVisibilityScope = this,
                        sharedTransitionScope = this@SharedTransitionLayout,
                    )
                }

                composable(Screen.Search.route) {
                    SearchScreen(
                        onRecipeClick = { recipeId ->
                            navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                        },
                        animatedVisibilityScope = this,
                        sharedTransitionScope = this@SharedTransitionLayout,
                    )
                }

                composable(Screen.Favorites.route) {
                    FavoritesScreen(
                        onRecipeClick = { recipeId ->
                            navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                        }
                    )
                }

                composable(Screen.AddRecipe.route) {
                    AddRecipeScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Screen.RecipeDetail.route,
                    arguments = listOf(
                        navArgument(Screen.RecipeDetail.ARG_RECIPE_ID) {
                            type = NavType.IntType
                        }
                    )
                ) {
                    RecipeDetailScreen(
                        onNavigateBack = { navController.popBackStack() },
                        animatedVisibilityScope = this,
                        sharedTransitionScope = this@SharedTransitionLayout,
                    )
                }
            }
        }
    }
}
