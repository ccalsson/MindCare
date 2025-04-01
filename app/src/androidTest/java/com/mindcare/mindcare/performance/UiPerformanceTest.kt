@HiltAndroidTest
class UiPerformanceTest : BaseComposeTest() {
    @Test
    fun `scroll performance in large list`() {
        // Given
        val items = List(1000) { index -> "Item $index" }
        
        launchComposable {
            LazyColumn {
                items(items) { item ->
                    ListItem(item)
                }
            }
        }

        // When - Scroll rápido
        repeat(10) {
            composeRule.onNode(hasScrollAction())
                .performTouchInput {
                    swipeUp(
                        startY = 900f,
                        endY = 100f,
                        durationMillis = 100
                    )
                }
        }

        // Then - Verificar que no hay drops de frames
        assertThat(getDroppedFrames()).isLessThan(5)
    }

    @Test
    fun `animation performance`() {
        // Given
        var isExpanded by mutableStateOf(false)
        
        launchComposable {
            AnimatedContent(
                targetState = isExpanded,
                transitionSpec = { expandVertically() }
            ) { expanded ->
                if (expanded) {
                    LargeContent()
                } else {
                    SmallContent()
                }
            }
        }

        // When
        repeat(10) {
            isExpanded = !isExpanded
            // Esperar a que termine la animación
            composeRule.waitForIdle()
        }

        // Then
        assertThat(getAnimationFrameRate()).isGreaterThan(55f) // Mantener 55+ FPS
    }
} 