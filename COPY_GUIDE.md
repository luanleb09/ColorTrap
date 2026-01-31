# Copy Guide - Artifact to File Mapping

## 📦 Artifact Sources

### auto_scan_asset_system
Contains: Core system files
- data/models/* (except enums)
- domain/*
- utils/DynamicAssetScanner.kt
- utils/AssetLoader.kt

### dynamic_ui_components
Contains: UI implementation
- ui/components/*
- ui/screens/game/*

### centralized_config
Contains: Configuration objects
- data/config/*

### main_activity_nav
Contains: Navigation & basic screens
- ui/navigation/*
- ui/screens/splash/*
- ui/screens/menu/*
- ui/screens/gameover/*

## 📋 Priority Order

### Phase 1: Core (Must have)
1. data/models/* (all 9 files)
2. data/config/* (all 4 files)
3. utils/DynamicAssetScanner.kt
4. utils/AssetLoader.kt

### Phase 2: Domain Logic
5. domain/DynamicSkinManager.kt
6. domain/DifficultyBasedSelector.kt
7. domain/DynamicLevelGenerator.kt
8. domain/ConfigManager.kt

### Phase 3: UI
9. ui/components/*
10. ui/screens/game/*
11. ui/navigation/*
12. ui/screens/* (other screens)

### Phase 4: Additional Utils
13. utils/SoundManager.kt
14. utils/AdManager.kt
15. Other utils

## 🎯 Quick Start

Start with these 4 files to get basic structure working:
1. data/config/GameConfig.kt
2. data/models/GameMode.kt
3. data/models/DynamicColorGroup.kt
4. utils/DynamicAssetScanner.kt
