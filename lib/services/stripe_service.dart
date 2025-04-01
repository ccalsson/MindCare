import 'package:flutter_stripe/flutter_stripe.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import '../config/stripe_config.dart';

class StripeService {
  static const String _baseUrl = 'https://api.stripe.com/v1';
  
  // Inicializar Stripe
  static Future<void> initialize() async {
    Stripe.publishableKey = StripeConfig.publishableKey;
    await Stripe.instance.applySettings();
  }

  // Crear intent de pago
  static Future<Map<String, dynamic>> createPaymentIntent({
    required String amount,
    required String currency,
    required String customerId,
  }) async {
    try {
      final Map<String, dynamic> body = {
        'amount': amount,
        'currency': currency,
        'customer': customerId,
        'payment_method_types[]': 'card'
      };

      final response = await http.post(
        Uri.parse('$_baseUrl/payment_intents'),
        headers: {
          'Authorization': 'Bearer ${StripeConfig.secretKey}',
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: body,
      );

      return json.decode(response.body);
    } catch (e) {
      throw Exception('Error al crear el payment intent: $e');
    }
  }

  // Procesar pago
  static Future<bool> processPayment({
    required String amount,
    required String currency,
    required String customerId,
  }) async {
    try {
      // Crear el payment intent
      final paymentIntent = await createPaymentIntent(
        amount: amount,
        currency: currency,
        customerId: customerId,
      );

      // Iniciar el flujo de pago
      await Stripe.instance.initPaymentSheet(
        paymentSheetParameters: SetupPaymentSheetParameters(
          paymentIntentClientSecret: paymentIntent['client_secret'],
          merchantDisplayName: 'MindCare',
          style: ThemeMode.system,
        ),
      );

      // Mostrar hoja de pago
      await Stripe.instance.presentPaymentSheet();
      
      return true;
    } catch (e) {
      print('Error en el proceso de pago: $e');
      return false;
    }
  }

  // Crear cliente en Stripe
  static Future<String> createCustomer({
    required String email,
    required String name,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/customers'),
        headers: {
          'Authorization': 'Bearer ${StripeConfig.secretKey}',
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: {
          'email': email,
          'name': name,
        },
      );

      final customer = json.decode(response.body);
      return customer['id'];
    } catch (e) {
      throw Exception('Error al crear el cliente: $e');
    }
  }
} 