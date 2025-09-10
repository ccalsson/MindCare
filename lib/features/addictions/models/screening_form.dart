import 'package:flutter/foundation.dart';

class ScreeningForm {
  final String instrument;
  final int version;
  final List<Question> questions;
  final Map<String, dynamic> scoring;

  const ScreeningForm({
    required this.instrument,
    required this.version,
    required this.questions,
    required this.scoring,
  });

  Map<String, dynamic> toMap() => {
        'instrument': instrument,
        'version': version,
        'questions': questions.map((q) => q.toMap()).toList(growable: false),
        'scoring': scoring,
      };

  factory ScreeningForm.fromMap(Map<String, dynamic> map) {
    return ScreeningForm(
      instrument: map['instrument'] as String,
      version: (map['version'] as num).toInt(),
      questions: (map['questions'] as List<dynamic>)
          .map<Question>((e) => Question.fromMap(Map<String, dynamic>.from(e)))
          .toList(growable: false),
      scoring: Map<String, dynamic>.from(map['scoring'] as Map),
    );
  }
}

class Question {
  final String id;
  final String text;
  final String type; // single | scale | multi
  final List<String>? options; // for single/multi

  const Question({
    required this.id,
    required this.text,
    required this.type,
    this.options,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'text': text,
        'type': type,
        if (options != null) 'options': options,
      };

  factory Question.fromMap(Map<String, dynamic> map) {
    return Question(
      id: map['id'] as String,
      text: map['text'] as String,
      type: map['type'] as String,
      options: (map['options'] as List?)?.map((e) => e.toString()).toList(),
    );
  }
}

class Answer {
  final String qId;
  final dynamic value;

  const Answer({required this.qId, required this.value});

  Map<String, dynamic> toMap() => {
        'qId': qId,
        'value': value,
      };
}

