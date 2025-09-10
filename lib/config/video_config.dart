class VideoConfig {
  static const String defaultJitsiDomain = String.fromEnvironment(
    'JITSI_DOMAIN',
    defaultValue: 'https://meet.jit.si',
  );
  static const String featureFlagKey = 'enableProCalls';
}

