import 'dart:convert';
import 'dart:io';

// Simple script to generate sample routines and resources JSON files.
// Run: dart run tools/seed_wellbeing.dart

void main() async {
  final dir = Directory('tools/seed/wellbeing');
  if (!await dir.exists()) await dir.create(recursive: true);

  final routines = [
    {
      'id': 'breathing_5',
      'title': 'Respiración 4-7-8',
      'durationMin': 5,
      'steps': [
        'Inhalá 4 segundos',
        'Mantené 7 segundos',
        'Exhalá 8 segundos',
        'Repetí por 5 minutos'
      ]
    },
    {
      'id': 'stretch_3',
      'title': 'Estiramientos de cuello y hombros',
      'durationMin': 3,
      'steps': ['Lento giro de cuello', 'Elevar y soltar hombros', 'Respiración lenta']
    },
    {
      'id': 'mindfulness_5',
      'title': 'Mindfulness 5 min',
      'durationMin': 5,
      'steps': ['Enfoque en respiración', 'Observá sin juzgar', 'Volvé al presente']
    },
    {
      'id': 'gratitude_5',
      'title': 'Gratitud en 3 pasos',
      'durationMin': 5,
      'steps': ['Anotá 3 cosas', 'Leé en voz baja', 'Agradecé internamente']
    },
  ];

  final resources = [
    {
      'id': 'art_org_5',
      'title': 'Organización del tiempo en el aula',
      'kind': 'articulo',
      'durationMin': 5,
      'topics': ['organizacion'],
      'contentUrl': 'https://example.org/organizacion',
      'description': 'Tips rápidos para organizar tu clase.'
    },
    {
      'id': 'audio_relax_5',
      'title': 'Audio: relajación breve',
      'kind': 'audio',
      'durationMin': 5,
      'topics': ['estres','mindfulness'],
      'contentUrl': 'gs://bucket/relax_5min.mp3',
      'description': 'Audio guiado de relajación.'
    },
    {
      'id': 'video_posture_3',
      'title': 'Video: postura saludable',
      'kind': 'video',
      'durationMin': 3,
      'topics': ['salud'],
      'contentUrl': 'https://example.org/postura',
      'description': 'Recomendaciones para mejorar postura en clase.'
    },
  ];

  final rPath = File('${dir.path}/routines.json');
  final resPath = File('${dir.path}/resources.json');
  await rPath.writeAsString(const JsonEncoder.withIndent('  ').convert(routines));
  await resPath.writeAsString(const JsonEncoder.withIndent('  ').convert(resources));
  print('Seed files written to ${dir.path}');
}

