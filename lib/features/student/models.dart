class StudyResource {
  final String id, title, subtitle, body;
  final List<String> tags;
  final int minutes;
  const StudyResource({
    required this.id,
    required this.title,
    required this.subtitle,
    required this.body,
    required this.tags,
    required this.minutes,
  });
}

class RoutineStep {
  final String type; // 'breathe' | 'focus' | 'break'
  final int minutes;
  const RoutineStep({required this.type, required this.minutes});
}

class StudyRoutine {
  final String id, name;
  final List<RoutineStep> steps;
  const StudyRoutine({required this.id, required this.name, required this.steps});
}

class StudyGoal {
  final String id, title;
  final int targetPerWeek;
  int done;
  StudyGoal({
    required this.id,
    required this.title,
    required this.targetPerWeek,
    this.done = 0,
  });
}

