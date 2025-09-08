class AvailabilitySlot {
  final int dayOfWeek; // 1=Mon .. 7=Sun
  final String startTime; // HH:mm
  final String endTime; // HH:mm
  final List<DateTime> exceptions; // days off

  AvailabilitySlot({
    required this.dayOfWeek,
    required this.startTime,
    required this.endTime,
    this.exceptions = const [],
  });
}

