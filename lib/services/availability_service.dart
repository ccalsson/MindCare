import '../models/availability.dart';

class AvailabilityService {
  Future<List<AvailabilitySlot>> getWeekly(String proId) async {
    // TODO: Integrate with Firestore
    return [
      AvailabilitySlot(dayOfWeek: 1, startTime: '10:00', endTime: '12:00'),
      AvailabilitySlot(dayOfWeek: 3, startTime: '14:00', endTime: '18:00'),
    ];
  }

  Future<void> setWeekly(String proId, List<AvailabilitySlot> slots) async {
    // TODO: Persist to Firestore
  }

  Future<void> addException(String proId, DateTime day) async {
    // TODO: Persist exception
  }
}

