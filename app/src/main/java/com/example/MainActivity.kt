package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ui.AuraViewModel
import com.example.ui.components.AuraTopBar
import com.example.ui.components.FloatingBottomDock
import com.example.ui.screens.DotMatrixCalendarScreen
import com.example.ui.screens.LookbookScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.theme.AuraBlack
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AuraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val state by viewModel.uiState.collectAsState()

                if (state.showOnboarding) {
                    OnboardingScreen(
                        currentProfile = state.userProfile,
                        isEditing = state.userProfile.isOnboarded,
                        onComplete = { newProfile ->
                            viewModel.saveUserProfile(newProfile)
                        },
                        onDismiss = {
                            viewModel.dismissProfileEditor()
                        }
                    )
                } else {
                    BackHandler(enabled = state.currentTab != 0) {
                        viewModel.selectTab(0)
                    }

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AuraBlack),
                        containerColor = AuraBlack,
                        contentColor = AuraBlack,
                        contentWindowInsets = WindowInsets.safeDrawing,
                        topBar = {
                            AuraTopBar(
                                currentTab = state.currentTab,
                                onTabSelected = { viewModel.selectTab(it) },
                                userProfile = state.userProfile,
                                onOpenProfile = { viewModel.openProfileEditor() },
                                lookbookCount = state.lookbookHistory.size
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            // Main Screen Switcher: Studio (0), Dot-Matrix Style Calendar (1), Lookbook (2)
                            Crossfade(
                                targetState = state.currentTab,
                                label = "tabCrossfade"
                            ) { tab ->
                                when (tab) {
                                    0 -> StudioScreen(viewModel = viewModel)
                                    1 -> DotMatrixCalendarScreen(
                                        userProfile = state.userProfile,
                                        lookbookCount = state.lookbookHistory.size
                                    )
                                    2 -> LookbookScreen(viewModel = viewModel)
                                }
                            }

                            // High-Fashion Floating Bottom Dock
                            FloatingBottomDock(
                                currentTab = state.currentTab,
                                onSelectTab = { viewModel.selectTab(it) },
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                }
            }
        }
    }
}
