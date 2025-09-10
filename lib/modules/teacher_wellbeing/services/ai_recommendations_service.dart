class AiRecommendationsService {
  Future<List<String>> suggestSelfCare(int mood, int energy, List<String> tags) async {
    // Placeholder heuristics; no external API calls.
    final suggestions = <String>[];
    if (energy <= 2) suggestions.add('Hacé una pausa de 5 minutos con respiración 4-7-8');
    if (mood <= 2) suggestions.add('Practicá gratitud: anota 3 cosas positivas');
    if (tags.contains('estrés')) suggestions.add('Ejercicio breve de mindfulness (3 min)');
    if (tags.contains('sueño')) suggestions.add('Higiene del sueño: evitá pantallas 30 min antes de dormir');
    if (suggestions.isEmpty) {
      suggestions.addAll([
        'Hidratate y estirá cuello/hombros (2 min)',
        'Organizá 3 tareas clave del día',
        'Caminata corta o respiración cuadrada (4x4)'
      ]);
    }
    // TODO(ccalsson): integrar API de recomendaciones en backend
    return suggestions.take(5).toList();
  }
}

