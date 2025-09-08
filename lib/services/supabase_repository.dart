import 'package:mindcare/services/supabase_service.dart';

class SupabaseRepository {
  /// List professionals (public.professionals)
  Future<List<Map<String, dynamic>>> listProfessionals() async {
    final res = await SupabaseService.client
        .from('professionals')
        .select('id, name, specialty, verified, profile')
        .order('name');
    return List<Map<String, dynamic>>.from(res as List);
  }

  /// List availability slots for a professional id
  Future<List<Map<String, dynamic>>> availabilityFor(String professionalId) async {
    final res = await SupabaseService.client
        .from('availability_slots')
        .select('id, start_at, end_at, status')
        .eq('professional_id', professionalId)
        .order('start_at');
    return List<Map<String, dynamic>>.from(res as List);
  }
}

