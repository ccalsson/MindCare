import 'package:flutter/foundation.dart';
import 'package:flutter_stripe/flutter_stripe.dart' as stripe;
import 'package:flutter_dotenv/flutter_dotenv.dart';

/// Stripe client helper (placeholder-friendly)
/// 
/// Usage:
/// - Call StripeService.initFromEnv() once app starts.
/// - Then call StripeService.maybePay(...) before creating an appointment.
/// - If no publishable key is set, it will return true so you can proceed during development.
class StripeService {
  static bool _initialized = false;
  static bool _available = false;

  static bool get isAvailable => _available;

  static Future<void> initFromEnv() async {
    if (_initialized) return;
    final pk = dotenv.env['STRIPE_PUBLISHABLE_KEY'];
    if (pk != null && pk.trim().isNotEmpty && !pk.toLowerCase().contains('pk_test_your_key_here')) {
      stripe.Stripe.publishableKey = pk;
      stripe.Stripe.merchantIdentifier = 'MindCare';
      _available = true;
    } else {
      _available = false;
    }
    _initialized = true;
  }

  /// Returns true if payment flow completed or is not required/available.
  static Future<bool> maybePay({required int amountCents, String currency = 'usd', bool requirePayment = false}) async {
    await initFromEnv();
    if (!_available) {
      // Not configured: allow proceeding in development unless strictly required.
      return !requirePayment;
    }
    try {
      // Placeholder: In a real flow you must fetch a PaymentIntent client secret
      // from your backend (Cloud Functions) and present the payment sheet.
      // Here we simply return true to unblock flows until backend is wired.
      // TODO: integrate with callable function to create PaymentIntent.
      return true;
    } catch (e) {
      if (kDebugMode) {
        print('Stripe error: $e');
      }
      return false;
    }
  }
}

