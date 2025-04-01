@HiltAndroidTest
class NotificationTest : BaseInstrumentedTest() {
    @Inject
    lateinit var notificationManager: NotificationManager

    @Test
    fun scheduleReminder_showsNotification() {
        val initialState = AppState(isAuthenticated = true)
        
        launchActivity(initialState) {
            // Schedule reminder
            val reminder = Reminder(
                id = "test",
                title = "Meditation Time",
                message = "Time for your daily meditation",
                time = System.currentTimeMillis() + 1000 // 1 second from now
            )
            notificationManager.scheduleReminder(reminder)
            
            // Wait for notification
            SystemClock.sleep(2000)
            
            // Verify notification shown
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) 
                as android.app.NotificationManager
            val notifications = notificationManager
                .activeNotifications
                .filter { it.notification.extras.getString("reminder_id") == "test" }
            
            assertThat(notifications).hasSize(1)
            assertThat(notifications[0].notification.extras.getString("android.title"))
                .isEqualTo("Meditation Time")
        }
    }
} 