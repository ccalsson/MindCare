import '../data/events_repo.dart';
import '../models/event_entry.dart';

// Nota: NO llama a LLM desde la app. Solo consume eventos creados por backend.
class CoachService {
  final EventsRepo _repo;
  CoachService(this._repo);

  Stream<List<EventEntry>> streamCoachMessages(String uid) {
    return _repo
        .streamUserEvents(uid)
        .map((events) => events.where((e) => e.payload['coachMsg'] != null).toList());
  }
}

