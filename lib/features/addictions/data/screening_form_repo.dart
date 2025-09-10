import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/screening_form.dart';

class ScreeningFormRepo {
  final FirebaseFirestore _db;
  ScreeningFormRepo({FirebaseFirestore? db}) : _db = db ?? FirebaseFirestore.instance;

  // Reads /screening_forms/{instrument}/versions/{version}
  // Note: Spec mentions "/screening_forms/{instrument}/{version}"; we use a 'versions' subcollection for clarity.
  Future<ScreeningForm> getForm(String instrument, int version) async {
    final ref = _db.collection('screening_forms').doc(instrument).collection('versions').doc('$version');
    final snap = await ref.get();
    if (!snap.exists) {
      throw StateError('Formulario no encontrado: $instrument v$version');
    }
    final data = snap.data() as Map<String, dynamic>;
    return ScreeningForm.fromMap(data);
  }
}

