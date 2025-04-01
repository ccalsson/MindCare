class DateUtilsTest {
    @Test
    fun `formatDuration converts seconds to readable format`() {
        // Given
        val seconds = 3665 // 1 hour, 1 minute, 5 seconds

        // When
        val result = DateUtils.formatDuration(seconds)

        // Then
        assertThat(result).isEqualTo("1:01:05")
    }

    @Test
    fun `isToday returns true for current date`() {
        // Given
        val now = System.currentTimeMillis()

        // When
        val result = DateUtils.isToday(now)

        // Then
        assertThat(result).isTrue()
    }
} 