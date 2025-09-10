import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import '../../services/remote_config_service.dart';
import '../../config/video_config.dart';
import '../../core/router/app_router.dart';
import 'video_call_service.dart';
import 'jitsi_launcher.dart';

class CallWaitingPage extends StatefulWidget {
  final String appointmentId;
  final String proId;
  final String? planTier; // 'basic' | 'full'

  const CallWaitingPage({super.key, required this.appointmentId, required this.proId, this.planTier});

  @override
  State<CallWaitingPage> createState() => _CallWaitingPageState();
}

class _CallWaitingPageState extends State<CallWaitingPage> {
  final _rc = RemoteConfigService();
  final _callSvc = VideoCallService();
  bool _loading = true;
  bool _enabled = true;

  @override
  void initState() {
    super.initState();
    () async {
      try {
        await _rc.init();
        _enabled = _rc.enableProCalls;
      } catch (_) {
        _enabled = true; // fallback
      }
      if (mounted) setState(() => _loading = false);
    }();
  }

  @override
  Widget build(BuildContext context) {
    if (_loading) return const Scaffold(body: Center(child: CircularProgressIndicator()));

    final isBasic = (widget.planTier ?? 'basic') == 'basic';
    return Scaffold(
      appBar: AppBar(title: const Text('Sala de espera')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            const Text(
              'Verificá tu conexión, cámara y micrófono. Al continuar, otorgás consentimiento para el uso de audio y video durante la sesión.',
            ),
            const SizedBox(height: 16),
            if (!_enabled)
              const Card(
                child: Padding(
                  padding: EdgeInsets.all(12.0),
                  child: Text('Las videollamadas no están disponibles temporalmente.'),
                ),
              ),
            if (isBasic)
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(12.0),
                  child: Column(
                    children: [
                      const Text('Tu plan no incluye videollamadas. Cambiá a Full para habilitarlas.'),
                      const SizedBox(height: 8),
                      ElevatedButton(
                        onPressed: () {
                          Navigator.of(context).push(
                            MaterialPageRoute(builder: (_) => const PaywallScreen()),
                          );
                        },
                        child: const Text('Mejorar plan'),
                      )
                    ],
                  ),
                ),
              ),
            const Spacer(),
            ElevatedButton(
              onPressed: (!_enabled || isBasic) ? null : () async {
                final domain = VideoConfig.defaultJitsiDomain;
                final room = await _callSvc.buildRoomName(widget.appointmentId, widget.proId);
                final url = JitsiLauncher.buildJitsiUrl(
                  domain: domain,
                  room: room,
                  subject: 'Sesión MindCare',
                  displayName: _callSvc.userDisplayName,
                  email: _callSvc.userEmail,
                );
                await _callSvc.markCallStarted(appointmentId: widget.appointmentId, room: room);
                await JitsiLauncher.openJitsiUrl(context, url);
                await _callSvc.markCallEnded(appointmentId: widget.appointmentId, reason: 'completed');
                if (mounted) Navigator.of(context).maybePop();
              },
              child: const Text('Unirme ahora'),
            ),
          ],
        ),
      ),
    );
  }
}
