import 'package:flutter/material.dart';

class MoodEnergyChart extends StatelessWidget {
  final List<int> moods; // 7 values 1..5
  final List<int> energies; // 7 values 1..5
  const MoodEnergyChart({super.key, required this.moods, required this.energies});

  @override
  Widget build(BuildContext context) {
    final barsMood = moods.map((e) => e.toDouble()).toList();
    final barsEnergy = energies.map((e) => e.toDouble()).toList();
    return SizedBox(
      height: 140,
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.end,
        children: List.generate(7, (i) {
          final m = i < barsMood.length ? barsMood[i] : 0;
          final en = i < barsEnergy.length ? barsEnergy[i] : 0;
          return Expanded(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 4),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  Container(height: (en / 5) * 120, color: Colors.blue.shade300),
                  const SizedBox(height: 2),
                  Container(height: (m / 5) * 120, color: const Color(0xFF25D366)),
                ],
              ),
            ),
          );
        }),
      ),
    );
  }
}

