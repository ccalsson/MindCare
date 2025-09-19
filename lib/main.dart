import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:provider/provider.dart';
import 'package:sami_1/core/routing/app_router.dart';
import 'package:sami_1/core/theme/app_theme.dart';
import 'package:sami_1/core/utils/password_hasher.dart';
import 'package:sami_1/data/repositories/alerts_repository_impl.dart';
import 'package:sami_1/data/repositories/auth_repository_impl.dart';
import 'package:sami_1/data/repositories/camera_repository_impl.dart';
import 'package:sami_1/data/repositories/company_repository_impl.dart';
import 'package:sami_1/data/repositories/fuel_repository_impl.dart';
import 'package:sami_1/data/repositories/operators_repository_impl.dart';
import 'package:sami_1/data/repositories/projects_repository_impl.dart';
import 'package:sami_1/data/repositories/reports_repository_impl.dart';
import 'package:sami_1/data/repositories/settings_repository_impl.dart';
import 'package:sami_1/data/repositories/tools_repository_impl.dart';
import 'package:sami_1/data/repositories/user_repository_impl.dart';
import 'package:sami_1/data/sources/local/hive_local_storage.dart';
import 'package:sami_1/data/sources/local/mock_seed_service.dart';
import 'package:sami_1/domain/repositories/auth_repository.dart';
import 'package:sami_1/domain/repositories/user_repository.dart';
import 'package:sami_1/domain/usecases/add_fuel_event_usecase.dart';
import 'package:sami_1/domain/usecases/delete_user_usecase.dart';
import 'package:sami_1/domain/usecases/generate_report_usecase.dart';
import 'package:sami_1/domain/usecases/get_alerts_usecase.dart';
import 'package:sami_1/domain/usecases/get_app_settings_usecase.dart';
import 'package:sami_1/domain/usecases/get_cameras_usecase.dart';
import 'package:sami_1/domain/usecases/get_company_usecase.dart';
import 'package:sami_1/domain/usecases/get_fuel_events_usecase.dart';
import 'package:sami_1/domain/usecases/get_operators_usecase.dart';
import 'package:sami_1/domain/usecases/get_projects_usecase.dart';
import 'package:sami_1/domain/usecases/get_reports_usecase.dart';
import 'package:sami_1/domain/usecases/get_tools_usecase.dart';
import 'package:sami_1/domain/usecases/get_users_usecase.dart';
import 'package:sami_1/domain/usecases/login_usecase.dart';
import 'package:sami_1/domain/usecases/logout_usecase.dart';
import 'package:sami_1/domain/usecases/resolve_alert_usecase.dart';
import 'package:sami_1/domain/usecases/save_app_settings_usecase.dart';
import 'package:sami_1/domain/usecases/save_company_usecase.dart';
import 'package:sami_1/domain/usecases/save_user_usecase.dart';
import 'package:sami_1/features/alerts/presentation/providers/alerts_provider.dart';
import 'package:sami_1/features/cameras/presentation/providers/cameras_provider.dart';
import 'package:sami_1/features/dashboard/presentation/providers/dashboard_provider.dart';
import 'package:sami_1/features/fuel/presentation/providers/fuel_provider.dart';
import 'package:sami_1/features/operators/presentation/providers/operators_provider.dart';
import 'package:sami_1/features/admin/users/presentation/providers/admin_users_provider.dart';
import 'package:sami_1/features/projects/presentation/providers/projects_provider.dart';
import 'package:sami_1/features/reports/presentation/providers/reports_provider.dart';
import 'package:sami_1/features/tools/presentation/providers/tools_provider.dart';
import 'package:sami_1/shared/providers/app_settings_provider.dart';
import 'package:sami_1/shared/providers/company_provider.dart';
import 'package:sami_1/shared/providers/session_provider.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final storage = HiveLocalStorage();
  await storage.init();
  final passwordHasher = PasswordHasher();
  final seedService = MockSeedService(storage, passwordHasher);
  await seedService.seed();

  final authRepository = AuthRepositoryImpl(storage, passwordHasher, seedService);
  final userRepository = UserRepositoryImpl(storage, passwordHasher);
  final alertsRepository = AlertsRepositoryImpl(storage);
  final cameraRepository = CameraRepositoryImpl(storage);
  final fuelRepository = FuelRepositoryImpl(storage);
  final toolsRepository = ToolsRepositoryImpl(storage);
  final operatorsRepository = OperatorsRepositoryImpl(storage);
  final projectsRepository = ProjectsRepositoryImpl(storage);
  final reportsRepository = ReportsRepositoryImpl(storage);
  final settingsRepository = SettingsRepositoryImpl(storage);
  final companyRepository = CompanyRepositoryImpl(storage);

  final loginUseCase = LoginUseCase(authRepository);
  final logoutUseCase = LogoutUseCase(authRepository);
  final sessionProvider = SessionProvider(
    loginUseCase: loginUseCase,
    logoutUseCase: logoutUseCase,
  );

  final appSettingsProvider = AppSettingsProvider(
    getSettings: GetAppSettingsUseCase(settingsRepository),
    saveSettings: SaveAppSettingsUseCase(settingsRepository),
    onTimeoutChanged: sessionProvider.refreshTimeout,
  );
  await appSettingsProvider.load();
  sessionProvider.refreshTimeout(minutes: appSettingsProvider.settings.sessionTimeoutMinutes);

  final companyProvider = CompanyProvider(
    getCompany: GetCompanyUseCase(companyRepository),
    saveCompany: SaveCompanyUseCase(companyRepository),
  );
  await companyProvider.load();

  final alertsProvider = AlertsProvider(
    getAlerts: GetAlertsUseCase(alertsRepository),
    resolveAlert: ResolveAlertUseCase(alertsRepository),
    saveAlert: (alert) => alertsRepository.saveAlert(alert),
  );
  await alertsProvider.load();

  final camerasProvider = CamerasProvider(getCameras: GetCamerasUseCase(cameraRepository));
  await camerasProvider.load();

  final fuelProvider = FuelProvider(
    getEvents: GetFuelEventsUseCase(fuelRepository),
    addEvent: AddFuelEventUseCase(fuelRepository),
  );
  await fuelProvider.load();

  final toolsProvider = ToolsProvider(getTools: GetToolsUseCase(toolsRepository));
  await toolsProvider.load();

  final operatorsProvider = OperatorsProvider(
    getOperators: GetOperatorsUseCase(operatorsRepository),
  );
  await operatorsProvider.load();

  final projectsProvider = ProjectsProvider(
    getProjects: GetProjectsUseCase(projectsRepository),
  );
  await projectsProvider.load();

  final reportsProvider = ReportsProvider(
    getReports: GetReportsUseCase(reportsRepository),
    generateReport: GenerateReportUseCase(reportsRepository),
  );
  await reportsProvider.load();

  final dashboardProvider = DashboardProvider(
    getAlerts: GetAlertsUseCase(alertsRepository),
    getCameras: GetCamerasUseCase(cameraRepository),
    getFuelEvents: GetFuelEventsUseCase(fuelRepository),
    getTools: GetToolsUseCase(toolsRepository),
    getProjects: GetProjectsUseCase(projectsRepository),
    getOperators: GetOperatorsUseCase(operatorsRepository),
  );
  await dashboardProvider.load();

  final adminUsersProvider = AdminUsersProvider(
    getUsers: GetUsersUseCase(userRepository),
    saveUser: SaveUserUseCase(userRepository),
    deleteUser: DeleteUserUseCase(userRepository),
  );
  await adminUsersProvider.load();

  final appRouter = AppRouter(sessionProvider: sessionProvider);

  runApp(
    MultiProvider(
      providers: [
        Provider<HiveLocalStorage>.value(value: storage),
        Provider<MockSeedService>.value(value: seedService),
        Provider<AuthRepository>.value(value: authRepository),
        Provider<UserRepository>.value(value: userRepository),
        ChangeNotifierProvider<SessionProvider>.value(value: sessionProvider),
        ChangeNotifierProvider<AppSettingsProvider>.value(value: appSettingsProvider),
        ChangeNotifierProvider<CompanyProvider>.value(value: companyProvider),
        ChangeNotifierProvider<AlertsProvider>.value(value: alertsProvider),
        ChangeNotifierProvider<CamerasProvider>.value(value: camerasProvider),
        ChangeNotifierProvider<FuelProvider>.value(value: fuelProvider),
        ChangeNotifierProvider<ToolsProvider>.value(value: toolsProvider),
        ChangeNotifierProvider<OperatorsProvider>.value(value: operatorsProvider),
        ChangeNotifierProvider<ProjectsProvider>.value(value: projectsProvider),
        ChangeNotifierProvider<ReportsProvider>.value(value: reportsProvider),
        ChangeNotifierProvider<DashboardProvider>.value(value: dashboardProvider),
        ChangeNotifierProvider<AdminUsersProvider>.value(value: adminUsersProvider),
      ],
      child: SamiApp(appRouter: appRouter),
    ),
  );
}

class SamiApp extends StatelessWidget {
  const SamiApp({required this.appRouter, super.key});

  final AppRouter appRouter;

  @override
  Widget build(BuildContext context) {
    final settings = context.watch<AppSettingsProvider>();
    return MaterialApp.router(
      title: 'SAMI 1',
      theme: buildLightTheme(),
      darkTheme: buildDarkTheme(),
      themeMode: settings.themeMode,
      routerConfig: appRouter.router,
      debugShowCheckedModeBanner: false,
      supportedLocales: const [Locale('es'), Locale('en')],
      locale: Locale(settings.locale),
      localizationsDelegates: const [
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
    );
  }
}
