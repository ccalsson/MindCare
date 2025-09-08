import 'package:flutter/material.dart';

class ModuleCard extends StatefulWidget {
  final String title;
  final IconData icon;
  final Color color;
  final bool locked;
  final VoidCallback? onTap;

  const ModuleCard({
    super.key,
    required this.title,
    required this.icon,
    required this.color,
    this.locked = false,
    this.onTap,
  });

  @override
  State<ModuleCard> createState() => _ModuleCardState();
}

class _ModuleCardState extends State<ModuleCard> {
  bool _hover = false;

  @override
  Widget build(BuildContext context) {
    final card = AnimatedContainer(
      duration: const Duration(milliseconds: 200),
      curve: Curves.easeOut,
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(_hover ? 0.15 : 0.08),
            blurRadius: _hover ? 16 : 8,
            offset: const Offset(0, 6),
          ),
        ],
        border: Border.all(color: const Color(0xFFF5F6F7)),
      ),
      padding: const EdgeInsets.all(16),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Stack(
            alignment: Alignment.center,
            children: [
              CircleAvatar(
                radius: 32,
                backgroundColor: widget.color.withOpacity(0.15),
                child: Icon(widget.icon, color: widget.color, size: 32),
              ),
              if (widget.locked)
                const Positioned(
                  right: -2,
                  top: -2,
                  child: Icon(Icons.lock, size: 18, color: Colors.redAccent),
                ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            widget.title,
            textAlign: TextAlign.center,
            style: const TextStyle(fontWeight: FontWeight.w600),
          ),
        ],
      ),
    );

    final child = InkWell(
      onTap: widget.locked ? null : widget.onTap,
      borderRadius: BorderRadius.circular(16),
      child: card,
      onHover: (v) => setState(() => _hover = v),
    );

    return child;
  }
}

