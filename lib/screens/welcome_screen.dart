import 'dart:io' show Platform;
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:mindcare/screens/login_screen.dart';
import 'package:mindcare/services/auth_social_service.dart';

class WelcomeScreen extends StatefulWidget {
  const WelcomeScreen({super.key});

  @override
  State<WelcomeScreen> createState() => _WelcomeScreenState();
}

class _WelcomeScreenState extends State<WelcomeScreen> {
  final _social = AuthSocialService();
  bool get _firebaseReady => Firebase.apps.isNotEmpty;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Scaffold(
      body: Center(
        child: ConstrainedBox(
          constraints: const BoxConstraints(maxWidth: 420),
          child: Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const Icon(Icons.self_improvement, size: 72, color: Colors.teal),
                const SizedBox(height: 12),
                Text('MindCare', style: theme.textTheme.headlineMedium),
                const SizedBox(height: 8),
                Text(
                  'Bienvenido. Inicia sesión o crea tu cuenta para continuar.',
                  textAlign: TextAlign.center,
                  style: theme.textTheme.bodyMedium,
                ),
                const SizedBox(height: 24),
                Row(
                  children: [
                    Expanded(
                      child: FilledButton(
                        onPressed: () {
                          Navigator.of(context).push(
                            MaterialPageRoute(builder: (_) => const LoginScreen()),
                          );
                        },
                        child: const Text('Ingresar / Crear cuenta'),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                if (_firebaseReady) ...[
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Expanded(
                        child: OutlinedButton.icon(
                          onPressed: () async {
                            try {
                              await _social.signInWithGoogle();
                              if (!mounted) return;
                              Navigator.of(context).pop();
                            } catch (e) {
                              if (!mounted) return;
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(content: Text('Google: $e')),
                              );
                            }
                          },
                          icon: const Icon(Icons.login),
                          label: const Text('Continuar con Google'),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),
                  if (!kIsWeb && (Platform.isIOS || Platform.isMacOS))
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Expanded(
                          child: OutlinedButton.icon(
                            onPressed: () async {
                              try {
                                await _social.signInWithApple();
                                if (!mounted) return;
                                Navigator.of(context).pop();
                              } catch (e) {
                                if (!mounted) return;
                                ScaffoldMessenger.of(context).showSnackBar(
                                  SnackBar(content: Text('Apple: $e')),
                                );
                              }
                            },
                            icon: const Icon(Icons.apple),
                            label: const Text('Continuar con Apple'),
                          ),
                        ),
                      ],
                    ),
                ] else ...[
                  Text(
                    'Autenticación con Google/Apple disponible cuando configures Firebase.',
                    textAlign: TextAlign.center,
                    style: theme.textTheme.bodySmall,
                  ),
                ],
              ],
            ),
          ),
        ),
      ),
    );
  }
}

