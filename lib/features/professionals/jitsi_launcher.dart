import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:webview_flutter/webview_flutter.dart';

class JitsiLauncher {
  static Uri buildJitsiUrl({
    required String domain,
    required String room,
    String? subject,
    String? displayName,
    String? email,
  }) {
    final params = <String, String>{
      if (subject != null) 'subject': subject,
      if (displayName != null) 'userInfo.displayName': displayName,
      if (email != null) 'userInfo.email': email,
    };
    final query = Uri(queryParameters: params).query;
    return Uri.parse('$domain/$room${query.isNotEmpty ? '?$query' : ''}');
  }

  static Future<void> openJitsiUrl(BuildContext context, Uri url) async {
    if (await canLaunchUrl(url)) {
      final ok = await launchUrl(url, mode: LaunchMode.externalApplication);
      if (!ok) {
        await _openWebView(context, url);
      }
    } else {
      await _openWebView(context, url);
    }
  }

  static Future<void> _openWebView(BuildContext context, Uri url) async {
    await Navigator.of(context).push(MaterialPageRoute(
      builder: (_) => Scaffold(
        appBar: AppBar(title: const Text('Videollamada')),
        body: WebViewWidget(controller: WebViewController()..loadRequest(url)),
      ),
    ));
  }
}

