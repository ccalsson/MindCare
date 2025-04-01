@HiltAndroidTest
abstract class BaseInstrumentedTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Inject
    lateinit var preferences: SharedPreferences

    @Before
    fun init() {
        hiltRule.inject()
        clearAppState()
    }

    private fun clearAppState() {
        preferences.edit().clear().commit()
        InstrumentationRegistry.getInstrumentation().targetContext
            .deleteDatabase("mindcare_db")
    }

    protected fun launchActivity(
        initialState: AppState = AppState(),
        block: ActivityScenario<MainActivity>.() -> Unit
    ) {
        activityRule.scenario.use { scenario ->
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.onActivity { activity ->
                // Setup initial state
                activity.viewModel.setState(initialState)
            }
            scenario.block()
        }
    }
} 