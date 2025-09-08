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

  /// Book a slot via RPC (atomic on the server)
  Future<String> bookSlot({
    required String professionalId,
    required String slotId,
    double priceAmount = 100,
    String priceCurrency = 'usd',
  }) async {
    final c = SupabaseService.client;
    final res = await c.rpc('book_slot', params: {
      'professional_id': professionalId,
      'slot_id': slotId,
      'price_amount': priceAmount,
      'price_currency': priceCurrency,
    });
    // res is booking id
    return res as String;
  }

  /// List current user bookings with joined info
  Future<List<Map<String, dynamic>>> myBookings() async {
    final c = SupabaseService.client;
    final res = await c
        .from('bookings')
        .select(
            'id, status, price_amount, price_currency, created_at, professionals(name, specialty), availability_slots(start_at, end_at)')
        .order('created_at', ascending: false);
    return List<Map<String, dynamic>>.from(res as List);
  }
}
