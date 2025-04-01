class MemoryPerformanceTest {
    private val runtime = Runtime.getRuntime()

    @Test
    fun `memory usage during heavy operations`() {
        // Given
        val initialMemory = getUsedMemory()
        val largeDataSet = generateLargeDataSet()

        // When
        val operations = performHeavyOperations(largeDataSet)

        // Then
        val memoryUsed = getUsedMemory() - initialMemory
        assertThat(memoryUsed).isLessThan(50 * 1024 * 1024) // Menos de 50MB de uso adicional

        // Verify operation results
        assertThat(operations.isSuccess).isTrue()
    }

    @Test
    fun `memory cleanup after large operations`() {
        // Given
        val initialMemory = getUsedMemory()
        
        // When
        performLargeOperation()
        System.gc() // Forzar garbage collection
        
        // Then
        val finalMemory = getUsedMemory()
        val memoryDiff = finalMemory - initialMemory
        assertThat(memoryDiff).isLessThan(5 * 1024 * 1024) // Menos de 5MB de diferencia
    }

    private fun getUsedMemory(): Long {
        return runtime.totalMemory() - runtime.freeMemory()
    }

    private fun generateLargeDataSet(): List<Any> {
        // Simular un conjunto grande de datos
        return List(10000) { index ->
            ComplexDataStructure(
                id = index,
                data = "Large string data".repeat(100),
                timestamp = System.currentTimeMillis()
            )
        }
    }
} 