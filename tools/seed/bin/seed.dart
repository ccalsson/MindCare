// Simple seeder to upload example docs if --allow-seed is present.
// WARNING: This code is commented to avoid accidental execution.
// Requires: flutter pub add cloud_firestore firebase_core, and Firebase init.

// import 'dart:convert';
// import 'dart:io';
// import 'package:firebase_core/firebase_core.dart';
// import 'package:cloud_firestore/cloud_firestore.dart';

Future<void> main(List<String> args) async {
  // if (!args.contains('--allow-seed')) {
  //   print('Seed disabled. Run with --allow-seed to proceed.');
  //   return;
  // }
  // await Firebase.initializeApp();
  // final db = FirebaseFirestore.instance;
  //
  // // Seed screening form AUDIT v1
  // final audit = File('tools/seed/screening_forms/AUDIT/1.json');
  // final auditJson = json.decode(await audit.readAsString()) as Map<String, dynamic>;
  // await db.collection('screening_forms').doc('AUDIT').collection('versions').doc('1').set(auditJson);
  //
  // // Seed flags/global
  // final flags = File('tools/seed/flags/global.json');
  // final flagsJson = json.decode(await flags.readAsString()) as Map<String, dynamic>;
  // await db.collection('flags').doc('global').set(flagsJson);
  //
  // print('Seed completed.');
}

