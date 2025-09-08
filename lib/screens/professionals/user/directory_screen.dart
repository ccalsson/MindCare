import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';
import '../../../providers/professionals_provider.dart';
import '../../../widgets/module_card.dart';
import '../../../models/professional.dart';
import '../role_guard_screen.dart';
import '../../../constants/app_styles.dart';
import 'detail_screen.dart';

class ProfessionalDirectoryScreen extends StatefulWidget {
  const ProfessionalDirectoryScreen({super.key});

  @override
  State<ProfessionalDirectoryScreen> createState() => _ProfessionalDirectoryScreenState();
}

class _ProfessionalDirectoryScreenState extends State<ProfessionalDirectoryScreen> {
  final _q = TextEditingController();
  String? _specialty;
  String? _language;

  @override
  void initState() {
    super.initState();
    Future.microtask(() => context.read<ProfessionalsProvider>().refresh());
  }

  @override
  Widget build(BuildContext context) {
    final prov = context.watch<ProfessionalsProvider>();
    final items = prov.items;
    return Scaffold(
      appBar: AppBar(
        title: Text('Directorio de Profesionales', style: GoogleFonts.poppins(fontWeight: FontWeight.w600)),
        actions: [
          IconButton(
            icon: const Icon(Icons.home_outlined),
            onPressed: () => Navigator.pop(context),
          )
        ],
      ),
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            colors: [AppColors.green, AppColors.blue],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
          ),
        ),
        child: SafeArea(
          child: Padding(
            padding: const EdgeInsets.all(12),
            child: Column(
              children: [
                _filters(context),
                const SizedBox(height: 12),
                Expanded(
                  child: LayoutBuilder(
                    builder: (context, c) {
                      final isWide = c.maxWidth > 900;
                      final cross = isWide ? 3 : 2;
                      final filtered = _applySearch(items);
                      if (prov.loading) {
                        return const Center(child: CircularProgressIndicator());
                      }
                      if (filtered.isEmpty) {
                        return const Center(child: Text('No se encontraron profesionales', style: TextStyle(color: Colors.white)));
                      }
                      return GridView.builder(
                        gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                          crossAxisCount: cross,
                          crossAxisSpacing: 12,
                          mainAxisSpacing: 12,
                          childAspectRatio: 1.1,
                        ),
                        itemCount: filtered.length,
                        itemBuilder: (context, i) => _ProCard(
                          pro: filtered[i],
                          onView: () => Navigator.push(context, MaterialPageRoute(builder: (_) => ProfessionalDetailScreen(proId: filtered[i].proId))),
                          onBook: () => Navigator.push(context, MaterialPageRoute(builder: (_) => ProfessionalDetailScreen(proId: filtered[i].proId))),
                        ),
                      );
                    },
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  List<Professional> _applySearch(List<Professional> list) {
    final q = _q.text.trim().toLowerCase();
    return list.where((p) {
      final okQ = q.isEmpty || p.fullName.toLowerCase().contains(q) || p.bio.toLowerCase().contains(q);
      final okS = _specialty == null || p.specialties.contains(_specialty);
      final okL = _language == null || p.languages.contains(_language);
      return okQ && okS && okL;
    }).toList();
  }

  Widget _filters(BuildContext context) {
    return Row(
      children: [
        Expanded(
          child: TextField(
            controller: _q,
            decoration: InputDecoration(
              filled: true,
              fillColor: Colors.white,
              prefixIcon: const Icon(Icons.search),
              hintText: 'Buscar por nombre o bio',
              border: OutlineInputBorder(borderRadius: BorderRadius.circular(12), borderSide: BorderSide.none),
            ),
            onChanged: (_) => setState(() {}),
          ),
        ),
        const SizedBox(width: 8),
        _chipFilter('Especialidad', _specialty, (v) => setState(() => _specialty = v)),
        const SizedBox(width: 8),
        _chipFilter('Idioma', _language, (v) => setState(() => _language = v)),
      ],
    );
  }

  Widget _chipFilter(String label, String? current, void Function(String?) onSelected) {
    return PopupMenuButton<String>(
      onSelected: onSelected,
      itemBuilder: (context) => [
        const PopupMenuItem(value: 'es', child: Text('Español')),
        const PopupMenuItem(value: 'en', child: Text('Inglés')),
        const PopupMenuDivider(),
        const PopupMenuItem(value: 'psicologia', child: Text('Psicología')),
        const PopupMenuItem(value: 'mindfulness', child: Text('Mindfulness')),
      ],
      child: Chip(
        label: Text(current ?? label),
        backgroundColor: Colors.white,
      ),
    );
  }
}

class _ProCard extends StatelessWidget {
  final Professional pro;
  final VoidCallback onView;
  final VoidCallback onBook;
  const _ProCard({required this.pro, required this.onView, required this.onBook});

  @override
  Widget build(BuildContext context) {
    return ModuleCard(
      title: pro.fullName,
      icon: Icons.person,
      color: AppColors.blue,
      onTap: onView,
    );
  }
}

