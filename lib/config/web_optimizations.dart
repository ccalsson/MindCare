class WebOptimizations {
  static void configure() {
    if (kIsWeb) {
      // Optimizar renderizado
      Paint.enableDithering = true;
      
      // Configurar service worker para caché
      if (window.navigator.serviceWorker != null) {
        window.navigator.serviceWorker.register('/service-worker.js');
      }
      
      // Precargar recursos críticos
      _preloadCriticalAssets();
    }
  }

  static Future<void> _preloadCriticalAssets() async {
    final criticalImages = [
      'assets/images/logo.png',
      'assets/images/meditation_bg.jpg',
    ];

    for (var path in criticalImages) {
      final image = NetworkImage(path);
      await precacheImage(image, null);
    }
  }
} 