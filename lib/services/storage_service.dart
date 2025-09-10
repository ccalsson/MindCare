import 'dart:io';
import 'package:firebase_storage/firebase_storage.dart';

class StorageService {
  final FirebaseStorage _storage;
  StorageService({FirebaseStorage? storage}) : _storage = storage ?? FirebaseStorage.instance;

  Future<(String url, String path)> uploadFile(String roomId, File file, {required String fileName, required String mime}) async {
    final ref = _storage.ref().child('rooms').child(roomId).child(fileName);
    final task = await ref.putFile(file, SettableMetadata(contentType: mime));
    final url = await task.ref.getDownloadURL();
    return (url, ref.fullPath);
  }
}

