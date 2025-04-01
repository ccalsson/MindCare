class AppState extends ChangeNotifier {
  final _repository = locator<Repository>();
  final _cache = locator<CacheService>();

  AppState() {
    _initializeState();
  }

  Future<void> _initializeState() async {
    await Future.wait([
      _loadUserPreferences(),
      _loadCachedData(),
      _setupSubscriptions(),
    ]);
  }

  @override
  void dispose() {
    _cache.dispose();
    _repository.dispose();
    super.dispose();
  }
} 