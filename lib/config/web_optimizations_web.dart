// ignore_for_file: deprecated_member_use
// ignore: avoid_web_libraries_in_flutter
import 'dart:html' as html;

void setupWebOptimizations() {
  if (html.window.navigator.serviceWorker != null) {
    html.window.navigator.serviceWorker!.register('/service-worker.js');
  }
}
