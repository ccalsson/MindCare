import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class AppTheme {
  static const Color primaryBlue = Color(0xFF0056D2);
  static const Color secondaryGreen = Color(0xFF00C853);
  static const Color lightBackground = Color(0xFFFFFFFF);
  static const Color darkBackground = Color(0xFF0B0B0D);

  static final TextStyle _titleStyle =
      GoogleFonts.poppins(fontWeight: FontWeight.bold);

  static final TextTheme _lightTextTheme =
      GoogleFonts.robotoTextTheme(ThemeData.light().textTheme).copyWith(
    titleLarge: _titleStyle,
    titleMedium: _titleStyle,
    titleSmall: _titleStyle,
    labelLarge: _titleStyle,
  );

  static final TextTheme _darkTextTheme =
      GoogleFonts.robotoTextTheme(ThemeData.dark().textTheme).copyWith(
    titleLarge: _titleStyle,
    titleMedium: _titleStyle,
    titleSmall: _titleStyle,
    labelLarge: _titleStyle,
  );

  static ThemeData lightTheme = ThemeData(
    colorScheme: const ColorScheme.light(
      primary: primaryBlue,
      secondary: secondaryGreen,
      background: lightBackground,
    ),
    scaffoldBackgroundColor: lightBackground,
    appBarTheme: AppBarTheme(
      backgroundColor: primaryBlue,
      foregroundColor: Colors.white,
      titleTextStyle: _titleStyle.copyWith(
        color: Colors.white,
        fontSize: 20,
      ),
    ),
    textTheme: _lightTextTheme,
    elevatedButtonTheme: ElevatedButtonThemeData(
      style: ElevatedButton.styleFrom(
        backgroundColor: primaryBlue,
        foregroundColor: Colors.white,
        textStyle: _titleStyle,
      ),
    ),
  );

  static ThemeData darkTheme = ThemeData(
    colorScheme: const ColorScheme.dark(
      primary: primaryBlue,
      secondary: secondaryGreen,
      background: darkBackground,
    ),
    scaffoldBackgroundColor: darkBackground,
    appBarTheme: AppBarTheme(
      backgroundColor: primaryBlue,
      foregroundColor: Colors.white,
      titleTextStyle: _titleStyle.copyWith(
        color: Colors.white,
        fontSize: 20,
      ),
    ),
    textTheme: _darkTextTheme,
    elevatedButtonTheme: ElevatedButtonThemeData(
      style: ElevatedButton.styleFrom(
        backgroundColor: primaryBlue,
        foregroundColor: Colors.white,
        textStyle: _titleStyle,
      ),
    ),
  );
}
