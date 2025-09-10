import 'package:go_router/go_router.dart';
import '../features/student/student_home_page.dart';
import '../features/student/student_resources_page.dart';
import '../features/student/student_routines_page.dart';
import '../features/student/student_goals_page.dart';
import 'package:flutter/widgets.dart';

List<GoRoute> getStudentRoutes() => [
      GoRoute(path: '/student/home', builder: (c, s) => const StudentHomePage()),
      GoRoute(path: '/student/resources', builder: (c, s) => const StudentResourcesPage()),
      GoRoute(path: '/student/routines', builder: (c, s) => const StudentRoutinesPage()),
      GoRoute(path: '/student/goals', builder: (c, s) => const StudentGoalsPage()),
    ];

// Optional helper for assistant-like entry points
bool isStudentIntent(String text) {
  final t = text.toLowerCase();
  return t.contains('estudio') || t.contains('foco') || t.contains('examen');
}

