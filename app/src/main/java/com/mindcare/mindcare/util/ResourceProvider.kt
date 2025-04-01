class ResourceProvider @Inject constructor(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val imageLoader = ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)
                .build()
        }
        .build()

    private val _resourceState = MutableStateFlow<ResourceState>(ResourceState.Initial)
    val resourceState = _resourceState.asStateFlow()

    init {
        scope.launch {
            preloadCriticalResources()
        }
    }

    private suspend fun preloadCriticalResources() {
        try {
            _resourceState.value = ResourceState.Loading
            loadFonts()
            loadInitialImages()
            _resourceState.value = ResourceState.Ready
        } catch (e: Exception) {
            _resourceState.value = ResourceState.Error(e)
        }
    }
} 