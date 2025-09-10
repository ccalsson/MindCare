import 'package:flutter_test/flutter_test.dart';
import 'package:mindcare/features/professionals/video_call_service.dart';

void main() {
  test('buildRoomName formats correctly', () async {
    final svc = VideoCallService();
    final room = await svc.buildRoomName('Appt_ABC', 'Pro_001');
    expect(room, 'mindcare_appt_abc_pro_001');
  });
}

