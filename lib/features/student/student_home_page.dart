import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import '../../constants/app_styles.dart';
import '../../core/strings.dart';
import '../../widgets/module_card.dart';
import '../../widgets/checklist_sheet.dart';
import 'student_controller.dart';
import 'student_resources_page.dart';
import 'student_routines_page.dart';
import 'student_goals_page.dart';

class StudentHomePage extends StatelessWidget {
  const StudentHomePage({super.key});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final controller = context.watch<StudentController>();
    return Scaffold(
      backgroundColor: AppColors.lightGrey,
      appBar: AppBar(title: const Text(S.studentTitle)),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          if (controller.isFocusing)
            _FocusBanner(minutes: controller.focusMinutesLeft, onStop: controller.stopFocus),
          const SizedBox(height: 8),
          _GreetingCard(),
          const SizedBox(height: 16),
          _SectionHeader(text: S.quickActions),
          const SizedBox(height: 8),
          Wrap(spacing: 8, runSpacing: 8, children: [
            OutlinedButton(onPressed: () => controller.startFocus(minutes: 20), child: const Text(S.focus20)),
            OutlinedButton(onPressed: () => _showAudioPlayerSheet(context, minutes: 5), child: const Text(S.break5)),
            OutlinedButton(
              onPressed: () => Navigator.of(context).push(
                MaterialPageRoute(builder: (_) => const StudentRoutinesPage()),
              ),
              child: const Text(S.startRoutine),
            ),
          ]),
          const SizedBox(height: 24),
          _SectionHeader(text: S.canHelpNow),
          const SizedBox(height: 8),
          SizedBox(
            height: 140,
            child: ListView(
              scrollDirection: Axis.horizontal,
              children: [
                _ActionCard(title: S.breathe1, icon: Icons.self_improvement, onTap: () => _showBreathingOverlay(context)),
                _ActionCard(title: S.musicFocus, icon: Icons.music_note, onTap: () => _showAudioPlayerSheet(context)),
                _ActionCard(title: S.pomodoro, icon: Icons.timer, onTap: () => _showPomodoroDialog(context)),
                _ActionCard(title: S.checklist, icon: Icons.checklist, onTap: () => showChecklistSheet(context)),
              ].map((w) => Padding(padding: const EdgeInsets.only(right: 12), child: w)).toList(),
            ),
          ),
          const SizedBox(height: 24),
          _SectionHeader(text: 'Accesos'),
          const SizedBox(height: 12),
          GridView(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 3,
              crossAxisSpacing: 12,
              mainAxisSpacing: 12,
              childAspectRatio: 1,
            ),
            children: [
              ModuleCard(
                title: S.resources,
                icon: Icons.menu_book,
                color: theme.colorScheme.primary,
                onTap: () => Navigator.of(context).push(
                  MaterialPageRoute(builder: (_) => const StudentResourcesPage()),
                ),
              ),
              ModuleCard(
                title: S.routines,
                icon: Icons.view_timeline,
                color: theme.colorScheme.secondary,
                onTap: () => Navigator.of(context).push(
                  MaterialPageRoute(builder: (_) => const StudentRoutinesPage()),
                ),
              ),
              ModuleCard(
                title: S.goals,
                icon: Icons.flag,
                color: theme.colorScheme.tertiary,
                onTap: () => Navigator.of(context).push(
                  MaterialPageRoute(builder: (_) => const StudentGoalsPage()),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _GreetingCard extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(16),
        gradient: LinearGradient(colors: [cs.primary.withOpacity(0.06), cs.secondary.withOpacity(0.06)]),
        color: Colors.white,
      ),
      padding: const EdgeInsets.all(16),
      child: const Text(S.studyWelcome, style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600)),
    );
  }
}

class _SectionHeader extends StatelessWidget {
  final String text;
  const _SectionHeader({required this.text});

  @override
  Widget build(BuildContext context) => Padding(
        padding: const EdgeInsets.symmetric(horizontal: 4),
        child: Text(text, style: Theme.of(context).textTheme.titleMedium),
      );
}

class _ActionCard extends StatefulWidget {
  final String title;
  final IconData icon;
  final VoidCallback onTap;
  const _ActionCard({required this.title, required this.icon, required this.onTap});
  @override
  State<_ActionCard> createState() => _ActionCardState();
}

class _ActionCardState extends State<_ActionCard> {
  bool _hover = false;
  bool _pressed = false;

  @override
  Widget build(BuildContext context) {
    final card = AnimatedContainer(
      duration: const Duration(milliseconds: 220),
      curve: Curves.easeOut,
      width: 160,
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(_hover ? 0.15 : 0.08),
            blurRadius: _hover ? 14 : 8,
            offset: const Offset(0, 6),
          ),
        ],
        border: Border.all(color: const Color(0xFFE7EAED)),
      ),
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(widget.icon, color: Theme.of(context).colorScheme.primary),
          const Spacer(),
          Text(widget.title, style: const TextStyle(fontWeight: FontWeight.w600)),
        ],
      ),
    );

    return MouseRegion(
      onEnter: (_) => setState(() => _hover = true),
      onExit: (_) => setState(() => _hover = false),
      child: GestureDetector(
        onTapDown: (_) => setState(() => _pressed = true),
        onTapCancel: () => setState(() => _pressed = false),
        onTapUp: (_) => setState(() => _pressed = false),
        onTap: widget.onTap,
        child: AnimatedScale(scale: _pressed ? 0.98 : 1.0, duration: const Duration(milliseconds: 120), child: card),
      ),
    );
  }
}

class _FocusBanner extends StatelessWidget {
  final int minutes;
  final VoidCallback onStop;
  const _FocusBanner({required this.minutes, required this.onStop});

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: const Color(0xFFE7EAED)),
      ),
      padding: const EdgeInsets.all(12),
      child: Row(
        children: [
          const Icon(Icons.timer, color: Colors.teal),
          const SizedBox(width: 8),
          Text('Sesión en curso: $minutes min'),
          const Spacer(),
          TextButton(onPressed: onStop, child: const Text('Detener')),
        ],
      ),
    );
  }
}

void _showBreathingOverlay(BuildContext context) {
  showDialog(
    context: context,
    builder: (ctx) => _BreathingDialog(),
  );
}

class _BreathingDialog extends StatefulWidget {
  @override
  State<_BreathingDialog> createState() => _BreathingDialogState();
}

class _BreathingDialogState extends State<_BreathingDialog> with SingleTickerProviderStateMixin {
  late final AnimationController _ctrl;
  @override
  void initState() {
    super.initState();
    _ctrl = AnimationController(vsync: this, duration: const Duration(seconds: 4))..repeat(reverse: true);
  }

  @override
  void dispose() {
    _ctrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      insetPadding: const EdgeInsets.all(24),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Text('Respirar 1 minuto', style: TextStyle(fontWeight: FontWeight.w600)),
            const SizedBox(height: 12),
            ScaleTransition(
              scale: Tween<double>(begin: 0.7, end: 1.0).animate(CurvedAnimation(parent: _ctrl, curve: Curves.easeInOut)),
              child: const CircleAvatar(radius: 48, backgroundColor: Color(0xFF2E86E6)),
            ),
            const SizedBox(height: 12),
            const Text('Inhala · Exhala', style: TextStyle(color: Colors.black54)),
            const SizedBox(height: 8),
            TextButton(onPressed: () => Navigator.of(context).pop(), child: const Text('Cerrar')),
          ],
        ),
      ),
    );
  }
}

void _showAudioPlayerSheet(BuildContext context, {int minutes = 5}) {
  showModalBottomSheet(
    context: context,
    isScrollControlled: false,
    backgroundColor: Colors.white,
    shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(16))),
    builder: (ctx) => Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              const Icon(Icons.music_note),
              const SizedBox(width: 8),
              const Text('Ruido blanco suave'),
              const Spacer(),
              IconButton(onPressed: () => Navigator.of(ctx).pop(), icon: const Icon(Icons.close)),
            ],
          ),
          const SizedBox(height: 8),
          const LinearProgressIndicator(value: null),
          const SizedBox(height: 8),
          Text('Temporizador: $minutes min (mock)', style: const TextStyle(fontSize: 12, color: Colors.black54)),
          const SizedBox(height: 12),
        ],
      ),
    ),
  );
}

void _showPomodoroDialog(BuildContext context) {
  int cycles = 4;
  showDialog(
    context: context,
    builder: (ctx) => StatefulBuilder(
      builder: (ctx, setState) => Dialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(mainAxisSize: MainAxisSize.min, crossAxisAlignment: CrossAxisAlignment.start, children: [
            const Text('Técnica Pomodoro', style: TextStyle(fontWeight: FontWeight.w600)),
            const SizedBox(height: 8),
            const Text('25/5 por ciclos (mock UI)'),
            const SizedBox(height: 12),
            Row(children: [
              const Text('Ciclos:'),
              const SizedBox(width: 8),
              DropdownButton<int>(
                value: cycles,
                onChanged: (v) => setState(() => cycles = v ?? 4),
                items: const [2, 3, 4, 5].map((e) => DropdownMenuItem(value: e, child: Text('$e'))).toList(),
              ),
            ]),
            const SizedBox(height: 12),
            Align(alignment: Alignment.centerRight, child: TextButton(onPressed: () => Navigator.of(ctx).pop(), child: const Text('Cerrar'))),
          ]),
        ),
      ),
    ),
  );
}
