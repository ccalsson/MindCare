import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class AppColors {
  static const blue = Color(0xFF2E86E6);
  static const green = Color(0xFF25D366);
  static const white = Color(0xFFFFFFFF);
  static const lightGrey = Color(0xFFF5F6F7);
}

ThemeData appTheme(BuildContext context) {
  final base = Theme.of(context);
  return base.copyWith(
    textTheme: GoogleFonts.poppinsTextTheme(base.textTheme),
    colorScheme: ColorScheme.fromSeed(seedColor: AppColors.blue),
    scaffoldBackgroundColor: AppColors.lightGrey,
  );
}

