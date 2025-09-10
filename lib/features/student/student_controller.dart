import 'package:flutter/foundation.dart';
import 'models.dart';

class StudentController extends ChangeNotifier {
  List<StudyResource> resources = [];
  List<StudyRoutine> myRoutines = [];
  List<StudyGoal> goals = [];

  bool isFocusing = false;
  int focusMinutesLeft = 0; // mock countdown

  void loadMock() {
    resources = [
      StudyResource(
        id: 'res1',
        title: 'Mejorar la concentración',
        subtitle: 'Ejercicios breves para entrenar foco',
        body: 'Tres bloques de atención plena y cortes activos.',
        tags: ['Concentración'],
        minutes: 10,
      ),
      StudyResource(
        id: 'res2',
        title: 'Memoria efectiva',
        subtitle: 'Técnicas de repasos espaciados',
        body: 'Aplicá ciclos cortos de recuerdo activo con descansos.',
        tags: ['Memoria'],
        minutes: 12,
      ),
      StudyResource(
        id: 'res3',
        title: 'Ansiedad ante exámenes',
        subtitle: 'Respiración + plan simple',
        body: 'Respirar 1 minuto, listar 3 pasos y avanzar.',
        tags: ['Ansiedad exámenes'],
        minutes: 6,
      ),
      StudyResource(
        id: 'res4',
        title: 'Organización semanal',
        subtitle: 'Plan rápido por bloques',
        body: 'Definí 3 objetivos y repartilos en bloques.',
        tags: ['Organización'],
        minutes: 8,
      ),
      StudyResource(
        id: 'res5',
        title: 'Rutina Pomodoro',
        subtitle: '25/5 por 4 ciclos',
        body: 'Estructurá 4 bloques con descansos cortos controlados.',
        tags: ['Concentración', 'Organización'],
        minutes: 25,
      ),
      StudyResource(
        id: 'res6',
        title: 'Checklist previo examen',
        subtitle: 'Verifica material y entorno',
        body: 'Repasá materiales, tiempo, entorno y plan de descanso.',
        tags: ['Ansiedad exámenes', 'Organización'],
        minutes: 5,
      ),
    ];

    myRoutines = [
      StudyRoutine(
        id: 'rt1',
        name: 'Arranque rápido',
        steps: const [
          RoutineStep(type: 'breathe', minutes: 1),
          RoutineStep(type: 'focus', minutes: 20),
          RoutineStep(type: 'break', minutes: 5),
        ],
      ),
      StudyRoutine(
        id: 'rt2',
        name: 'Noche previa',
        steps: const [
          RoutineStep(type: 'check', minutes: 5),
          RoutineStep(type: 'breathe', minutes: 1),
          RoutineStep(type: 'focus', minutes: 15),
        ],
      ),
    ];

    goals = [
      StudyGoal(id: 'g1', title: '3 sesiones de 20 min', targetPerWeek: 3),
      StudyGoal(id: 'g2', title: '2 checklists', targetPerWeek: 2),
      StudyGoal(id: 'g3', title: '2 respiraciones', targetPerWeek: 2),
    ];
    notifyListeners();
  }

  void startFocus({int minutes = 20}) {
    isFocusing = true;
    focusMinutesLeft = minutes;
    notifyListeners();
  }

  void stopFocus() {
    isFocusing = false;
    focusMinutesLeft = 0;
    notifyListeners();
  }

  void toggleGoal(String id) {
    final g = goals.firstWhere((e) => e.id == id, orElse: () => throw ArgumentError('Goal not found'));
    if (g.done < g.targetPerWeek) {
      g.done += 1;
      notifyListeners();
    }
  }

  void addRoutine(StudyRoutine r) {
    myRoutines = [...myRoutines, r];
    notifyListeners();
  }
}

