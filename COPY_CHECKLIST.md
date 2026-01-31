# Copy Code Checklist

## ✅ Files to Copy

### Data Models (9 files)
- [ ] data/models/GameMode.kt
- [ ] data/models/ItemType.kt
- [ ] data/models/LevelModifier.kt
- [ ] data/models/DifficultyLevel.kt
- [ ] data/models/DynamicColorGroup.kt
- [ ] data/models/TileVariant.kt
- [ ] data/models/DynamicLevel.kt
- [ ] data/models/DynamicTile.kt
- [ ] data/models/DynamicGameState.kt

### Config (4 files)
- [ ] data/config/GameConfig.kt
- [ ] data/config/ShopConfig.kt
- [ ] data/config/TextConfig.kt
- [ ] data/config/BalanceConfig.kt

### Repository (3 files)
- [ ] data/repository/GameRepository.kt
- [ ] data/repository/PreferencesRepository.kt
- [ ] data/local/PreferencesManager.kt

### Domain (4 files)
- [ ] domain/DynamicSkinManager.kt
- [ ] domain/DifficultyBasedSelector.kt
- [ ] domain/DynamicLevelGenerator.kt
- [ ] domain/ConfigManager.kt

### Utils (7 files)
- [ ] utils/DynamicAssetScanner.kt
- [ ] utils/AssetLoader.kt
- [ ] utils/SoundManager.kt
- [ ] utils/VibrationManager.kt
- [ ] utils/AdManager.kt
- [ ] utils/Constants.kt
- [ ] utils/Extensions.kt

### UI Components (4 files)
- [ ] ui/components/DynamicTileGrid.kt
- [ ] ui/components/DynamicColorDisplayBar.kt
- [ ] ui/components/TopBar.kt
- [ ] ui/components/ItemBar.kt

### UI Screens (12 files)
- [ ] ui/screens/splash/SplashScreen.kt
- [ ] ui/screens/splash/SplashViewModel.kt
- [ ] ui/screens/menu/MainMenuScreen.kt
- [ ] ui/screens/menu/MainMenuViewModel.kt
- [ ] ui/screens/game/DynamicGameScreen.kt
- [ ] ui/screens/game/DynamicGameViewModel.kt
- [ ] ui/screens/gameover/GameOverScreen.kt
- [ ] ui/screens/gameover/GameOverViewModel.kt
- [ ] ui/screens/shop/ShopScreen.kt
- [ ] ui/screens/shop/ShopViewModel.kt
- [ ] ui/screens/settings/SettingsScreen.kt
- [ ] ui/screens/settings/SettingsViewModel.kt

### Navigation (2 files)
- [ ] ui/navigation/Screen.kt
- [ ] ui/navigation/AppNavGraph.kt

### Root
- [ ] MainActivity.kt (replace existing)

## 📊 Progress
Total: 45 files
Completed: 0/45

## 🔍 Artifact Sources
- `auto_scan_asset_system` - Data models, domain, utils
- `dynamic_ui_components` - UI screens, components
- `centralized_config` - Config files
- `main_activity_nav` - Navigation
