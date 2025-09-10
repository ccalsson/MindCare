import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/strings.dart';
import 'student_controller.dart';
import 'models.dart';

class StudentGoalsPage extends StatelessWidget {
  const StudentGoalsPage({super.key});

  @override
  Widget build(BuildContext context) {
    final c = context.watch<StudentController>();
    return Scaffold(
      appBar: AppBar(title: const Text(S.goals)),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: c.goals.length,
        itemBuilder: (ctx, i) => _GoalCard(goal: c.goals[i]),
      ),
    );
  }
}

class _GoalCard extends StatefulWidget {
  final StudyGoal goal;
  const _GoalCard({required this.goal});
  @override
  State<_GoalCard> createState() => _GoalCardState();
}

class _GoalCardState extends State<_GoalCard> {
  bool celebrate = false;

  @override
  Widget build(BuildContext context) {
    final g = widget.goal;
    final reached = g.done >= g.targetPerWeek;
    return GestureDetector(
      onTap: () {
        context.read<StudentController>().toggleGoal(g.id);
        if (!mounted) return;
        final nowReached = g.done >= g.targetPerWeek;
        if (nowReached) {
          setState(() => celebrate = true);
          Future.delayed(const Duration(milliseconds: 800), () => mounted ? setState(() => celebrate = false) : null);
        }
      },
      child: AnimatedScale(
        duration: const Duration(milliseconds: 160),
        scale: celebrate ? 1.05 : 1.0,
        child: Card(
          elevation: 0,
          margin: const EdgeInsets.only(bottom: 12),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12), side: const BorderSide(color: Color(0xFFE7EAED))),
          child: Padding(
            padding: const EdgeInsets.all(12),
            child: Row(children: [
              Icon(reached ? Icons.emoji_events : Icons.flag, color: reached ? Colors.amber : Colors.teal),
              const SizedBox(width: 12),
              Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                Text(g.title, style: Theme.of(context).textTheme.titleMedium),
                const SizedBox(height: 4),
                Text('${S.target}: ${g.targetPerWeek}/sem', style: const TextStyle(color: Colors.black54)),
                const SizedBox(height: 8),
                _Sparkline(),
              ])),
              const SizedBox(width: 12),
              Text('${g.done}/${g.targetPerWeek}'),
            ]),
          ),
        ),
      ),
    );
  }
}

class _Sparkline extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 28,
      width: double.infinity,
      child: CustomPaint(
        painter: _SparkPainter(),
      ),
    );
  }
}

class _SparkPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = Colors.teal
      ..style = PaintingStyle.stroke
      ..strokeWidth = 2;
    final path = Path();
    // Mock data
    final points = [0.2, 0.5, 0.3, 0.7, 0.6, 0.9, 0.8];
    for (var i = 0; i < points.length; i++) {
      final x = i / (points.length - 1) * size.width;
      final y = size.height - points[i] * size.height;
      if (i == 0) {
        path.moveTo(x, y);
      } else {
        path.lineTo(x, y);
      }
    }
    canvas.drawPath(path, paint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
