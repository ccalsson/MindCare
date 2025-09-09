import 'package:flutter/material.dart';

class GradientScaffold extends StatelessWidget {
  final PreferredSizeWidget? appBar;
  final Widget body;
  final Widget? drawer;
  final EdgeInsetsGeometry padding;

  const GradientScaffold({
    super.key,
    this.appBar,
    required this.body,
    this.drawer,
    this.padding = const EdgeInsets.all(16),
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: appBar,
      drawer: drawer,
      extendBodyBehindAppBar: appBar != null,
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [Color(0xFF25D366), Color(0xFF2E86E6)],
          ),
        ),
        child: SafeArea(
          child: Padding(
            padding: padding,
            child: body,
          ),
        ),
      ),
      backgroundColor: Colors.transparent,
    );
  }
}

