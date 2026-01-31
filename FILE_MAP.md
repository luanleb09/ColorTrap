# File Map - Complete Structure

## 📁 Project Structure

```
com.colortrap.game/
├── data/
│   ├── models/
│   │   ├── GameMode.kt ✅ (has code)
│   │   ├── ItemType.kt ✅ (has code)
│   │   ├── LevelModifier.kt ✅ (has code)
│   │   ├── DifficultyLevel.kt ✅ (has code)
│   │   ├── DynamicColorGroup.kt ⏳ (copy from artifact)
│   │   ├── TileVariant.kt ⏳
│   │   ├── DynamicLevel.kt ⏳
│   │   ├── DynamicTile.kt ⏳
│   │   └── DynamicGameState.kt ⏳
│   │
│   ├── config/
│   │   ├── GameConfig.kt ⏳
│   │   ├── ShopConfig.kt ⏳
│   │   ├── TextConfig.kt ⏳
│   │   └── BalanceConfig.kt ⏳
│   │
│   ├── repository/
│   │   ├── GameRepository.kt ⏳
│   │   └── PreferencesRepository.kt ⏳
│   │
│   └── local/
│       └── PreferencesManager.kt ⏳
│
├── domain/
│   ├── DynamicSkinManager.kt ⏳
│   ├── DifficultyBasedSelector.kt ⏳
│   ├── DynamicLevelGenerator.kt ⏳
│   └── ConfigManager.kt ⏳
│
├── utils/
│   ├── DynamicAssetScanner.kt ⏳
│   ├── AssetLoader.kt ⏳
│   ├── SoundManager.kt ⏳
│   ├── VibrationManager.kt ⏳
│   ├── AdManager.kt ⏳
│   ├── Constants.kt ⏳
│   └── Extensions.kt ⏳
│
└── ui/
    ├── components/
    │   ├── DynamicTileGrid.kt ⏳
    │   ├── DynamicColorDisplayBar.kt ⏳
    │   ├── TopBar.kt ⏳
    │   └── ItemBar.kt ⏳
    │
    ├── screens/
    │   ├── splash/ (2 files) ⏳
    │   ├── menu/ (2 files) ⏳
    │   ├── game/ (2 files) ⏳
    │   ├── gameover/ (2 files) ⏳
    │   └── shop/ (2 files) ⏳
    │
    └── navigation/
        ├── Screen.kt ⏳
        └── AppNavGraph.kt ⏳
```

Legend:
✅ = Complete (has code)
⏳ = Empty (needs code from artifacts)

Total: 4 complete, 41 to copy
