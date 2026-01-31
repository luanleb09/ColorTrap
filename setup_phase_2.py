#!/usr/bin/env python3
"""
ColorTrap - Phase 2 Auto Setup
Creates complete project structure with all necessary files and configs
"""

import os
import sys
import json
from pathlib import Path

# ANSI colors
class Colors:
    GREEN = '\033[92m'
    BLUE = '\033[94m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    CYAN = '\033[96m'
    MAGENTA = '\033[95m'
    BOLD = '\033[1m'
    END = '\033[0m'

class ColorTrapSetup:
    def __init__(self):
        self.project_root = None
        self.stats = {
            'folders': 0,
            'kotlin_files': 0,
            'json_files': 0,
            'resource_files': 0,
            'doc_files': 0
        }
    
    def print_header(self, text):
        print(f"\n{Colors.CYAN}{Colors.BOLD}{'='*70}{Colors.END}")
        print(f"{Colors.CYAN}{Colors.BOLD}{text:^70}{Colors.END}")
        print(f"{Colors.CYAN}{Colors.BOLD}{'='*70}{Colors.END}\n")
    
    def print_success(self, text):
        print(f"{Colors.GREEN}✓ {text}{Colors.END}")
    
    def print_info(self, text):
        print(f"{Colors.BLUE}→ {text}{Colors.END}")
    
    def print_warning(self, text):
        print(f"{Colors.YELLOW}⚠ {text}{Colors.END}")
    
    def print_error(self, text):
        print(f"{Colors.RED}✗ {text}{Colors.END}")
    
    def find_project_root(self):
        """Find ColorTrap project root"""
        current = Path.cwd()
        
        # Check current directory
        if (current / "app" / "src" / "main").exists():
            return current
        
        # Check if we're inside project
        for parent in current.parents:
            if parent.name == "ColorTrap" and (parent / "app" / "src" / "main").exists():
                return parent
        
        # Common locations
        possible_paths = [
            current / "ColorTrap",
            Path.home() / "Downloads" / "ColorTrap",
            Path.home() / "Desktop" / "ColorTrap",
            Path("C:/Users/PC/Downloads/ColorTrap"),
        ]
        
        for path in possible_paths:
            if path.exists() and (path / "app" / "src" / "main").exists():
                return path
        
        # Ask user
        self.print_warning("Could not auto-detect project root")
        self.print_info(f"Current directory: {current}")
        user_path = input(f"\n{Colors.YELLOW}Enter ColorTrap project path: {Colors.END}")
        
        return Path(user_path.strip())
    
    def verify_project(self):
        """Verify it's a valid Android project"""
        required_paths = [
            self.project_root / "app",
            self.project_root / "app" / "src" / "main",
            self.project_root / "app" / "build.gradle.kts",
        ]
        
        for path in required_paths:
            if not path.exists():
                self.print_error(f"Missing required path: {path}")
                return False
        
        return True
    
    def create_kotlin_structure(self):
        """Create complete Kotlin folder structure with files"""
        self.print_header("STEP 1: Creating Kotlin Files")
        
        base = self.project_root / "app" / "src" / "main" / "java" / "com" / "colortrap" / "game"
        
        structure = {
            # Data Layer
            "data/models": [
                ("GameMode.kt", self.get_gamemode_template()),
                ("ItemType.kt", self.get_itemtype_template()),
                ("LevelModifier.kt", self.get_levelmodifier_template()),
                ("DifficultyLevel.kt", self.get_difficultylevel_template()),
                ("DynamicColorGroup.kt", self.get_package_only("data.models")),
                ("TileVariant.kt", self.get_package_only("data.models")),
                ("DynamicLevel.kt", self.get_package_only("data.models")),
                ("DynamicTile.kt", self.get_package_only("data.models")),
                ("DynamicGameState.kt", self.get_package_only("data.models")),
            ],
            
            "data/config": [
                ("GameConfig.kt", self.get_package_only("data.config")),
                ("ShopConfig.kt", self.get_package_only("data.config")),
                ("TextConfig.kt", self.get_package_only("data.config")),
                ("BalanceConfig.kt", self.get_package_only("data.config")),
            ],
            
            "data/repository": [
                ("GameRepository.kt", self.get_package_only("data.repository")),
                ("PreferencesRepository.kt", self.get_package_only("data.repository")),
            ],
            
            "data/local": [
                ("PreferencesManager.kt", self.get_package_only("data.local")),
            ],
            
            # Domain Layer
            "domain": [
                ("DynamicSkinManager.kt", self.get_package_only("domain")),
                ("DifficultyBasedSelector.kt", self.get_package_only("domain")),
                ("DynamicLevelGenerator.kt", self.get_package_only("domain")),
                ("ConfigManager.kt", self.get_package_only("domain")),
            ],
            
            # Utils
            "utils": [
                ("DynamicAssetScanner.kt", self.get_package_only("utils")),
                ("AssetLoader.kt", self.get_package_only("utils")),
                ("SoundManager.kt", self.get_package_only("utils")),
                ("VibrationManager.kt", self.get_package_only("utils")),
                ("AdManager.kt", self.get_package_only("utils")),
                ("Constants.kt", self.get_package_only("utils")),
                ("Extensions.kt", self.get_package_only("utils")),
            ],
            
            # UI Components
            "ui/components": [
                ("DynamicTileGrid.kt", self.get_package_only("ui.components")),
                ("DynamicColorDisplayBar.kt", self.get_package_only("ui.components")),
                ("TopBar.kt", self.get_package_only("ui.components")),
                ("ItemBar.kt", self.get_package_only("ui.components")),
            ],
            
            # UI Screens
            "ui/screens/splash": [
                ("SplashScreen.kt", self.get_package_only("ui.screens.splash")),
                ("SplashViewModel.kt", self.get_package_only("ui.screens.splash")),
            ],
            
            "ui/screens/menu": [
                ("MainMenuScreen.kt", self.get_package_only("ui.screens.menu")),
                ("MainMenuViewModel.kt", self.get_package_only("ui.screens.menu")),
            ],
            
            "ui/screens/game": [
                ("DynamicGameScreen.kt", self.get_package_only("ui.screens.game")),
                ("DynamicGameViewModel.kt", self.get_package_only("ui.screens.game")),
            ],
            
            "ui/screens/gameover": [
                ("GameOverScreen.kt", self.get_package_only("ui.screens.gameover")),
                ("GameOverViewModel.kt", self.get_package_only("ui.screens.gameover")),
            ],
            
            "ui/screens/shop": [
                ("ShopScreen.kt", self.get_package_only("ui.screens.shop")),
                ("ShopViewModel.kt", self.get_package_only("ui.screens.shop")),
            ],
            
            # Navigation
            "ui/navigation": [
                ("Screen.kt", self.get_package_only("ui.navigation")),
                ("AppNavGraph.kt", self.get_package_only("ui.navigation")),
            ],
        }
        
        for folder_path, files in structure.items():
            full_folder = base / folder_path
            full_folder.mkdir(parents=True, exist_ok=True)
            
            if not (full_folder / ".created").exists():
                self.stats['folders'] += 1
                self.print_success(f"Created: {folder_path}/")
            
            for filename, content in files:
                file_path = full_folder / filename
                
                if not file_path.exists():
                    with open(file_path, 'w', encoding='utf-8') as f:
                        f.write(content)
                    
                    self.stats['kotlin_files'] += 1
                    self.print_info(f"  ✓ {folder_path}/{filename}")
        
        self.print_success(f"\nCreated {self.stats['kotlin_files']} Kotlin files in {self.stats['folders']} folders")
    
    def create_asset_structure(self):
        """Create assets folder structure"""
        self.print_header("STEP 2: Creating Asset Folders")
        
        assets_base = self.project_root / "app" / "src" / "main" / "assets"
        
        folders = [
            "config",
            "skins/color",
            "audio",
            "effects",
            "ui",
        ]
        
        for folder in folders:
            full_path = assets_base / folder
            if not full_path.exists():
                full_path.mkdir(parents=True, exist_ok=True)
                # Create .gitkeep
                (full_path / ".gitkeep").touch()
                self.print_success(f"Created: assets/{folder}/")
    
    def create_json_configs(self):
        """Create JSON configuration files"""
        self.print_header("STEP 3: Creating JSON Configs")
        
        config_path = self.project_root / "app" / "src" / "main" / "assets" / "config"
        config_path.mkdir(parents=True, exist_ok=True)
        
        # game_config.json
        game_config = {
            "version": "1.0",
            "name": "ColorTrap",
            "skins": {
                "autoScan": True,
                "basePath": "skins",
                "supportedFormats": ["webp", "png", "jpg"],
                "defaultSkin": "color"
            },
            "difficulty": {
                "EASY": {
                    "strategy": "cross-group",
                    "description": "Different color groups, easy to distinguish",
                    "sameGroupRatio": 0.0,
                    "minGroupDistance": "maximum"
                },
                "MEDIUM": {
                    "strategy": "mixed",
                    "description": "Mix of same and different groups",
                    "sameGroupRatio": 0.5,
                    "minGroupDistance": "medium"
                },
                "HARD": {
                    "strategy": "same-group-preferred",
                    "description": "Mostly same group variants",
                    "sameGroupRatio": 0.8,
                    "minGroupDistance": "low"
                },
                "SUPER_HARD": {
                    "strategy": "single-group",
                    "description": "All from same group (N-1 rule)",
                    "sameGroupRatio": 1.0,
                    "minGroupDistance": "minimal",
                    "nMinusOneRule": True
                }
            }
        }
        
        # balance_config.json
        balance_config = {
            "version": "1.0",
            "modes": {
                "NORMAL": {
                    "levels": [
                        {"range": "1-5", "gridSize": 4, "forbiddenCount": 1, "timeLimit": 5.0},
                        {"range": "6-10", "gridSize": 4, "forbiddenCount": 1, "timeLimit": 4.5},
                        {"range": "11-20", "gridSize": 4, "forbiddenCount": 2, "timeLimit": 4.0},
                        {"range": "21-30", "gridSize": 5, "forbiddenCount": 2, "timeLimit": 3.8},
                        {"range": "31-40", "gridSize": 5, "forbiddenCount": 3, "timeLimit": 3.5},
                        {"range": "41-50", "gridSize": 6, "forbiddenCount": 4, "timeLimit": 3.2},
                        {"range": "51-60", "gridSize": 6, "forbiddenCount": 5, "timeLimit": 3.0},
                        {"range": "61-80", "gridSize": 8, "forbiddenCount": 7, "timeLimit": 2.5},
                        {"range": "81+", "gridSize": 10, "forbiddenCount": 9, "timeLimit": 2.0}
                    ]
                },
                "HARD": {
                    "levels": [
                        {"range": "1-10", "gridSize": 4, "forbiddenCount": 2, "timeLimit": 4.0},
                        {"range": "11-20", "gridSize": 5, "forbiddenCount": 3, "timeLimit": 3.5},
                        {"range": "21-30", "gridSize": 6, "forbiddenCount": 4, "timeLimit": 3.0},
                        {"range": "31-50", "gridSize": 6, "forbiddenCount": 5, "timeLimit": 2.8},
                        {"range": "51+", "gridSize": 8, "forbiddenCount": 7, "timeLimit": 2.0}
                    ]
                },
                "SUPER_HARD": {
                    "levels": [
                        {"range": "1-10", "gridSize": 5, "forbiddenCount": 4, "timeLimit": 3.0},
                        {"range": "11-20", "gridSize": 6, "forbiddenCount": 5, "timeLimit": 2.5},
                        {"range": "21-40", "gridSize": 8, "forbiddenCount": 7, "timeLimit": 2.0},
                        {"range": "41+", "gridSize": 10, "forbiddenCount": 9, "timeLimit": 1.5}
                    ]
                },
                "RELAX": {
                    "levels": [
                        {"range": "1-10", "gridSize": 4, "forbiddenCount": 1, "timeLimit": 8.0},
                        {"range": "11-20", "gridSize": 4, "forbiddenCount": 2, "timeLimit": 7.0},
                        {"range": "21-40", "gridSize": 5, "forbiddenCount": 2, "timeLimit": 6.5},
                        {"range": "41-60", "gridSize": 6, "forbiddenCount": 2, "timeLimit": 6.0},
                        {"range": "61-100", "gridSize": 6, "forbiddenCount": 3, "timeLimit": 5.5},
                        {"range": "101+", "gridSize": 6, "forbiddenCount": 2, "timeLimit": 5.0}
                    ],
                    "lives": 3
                }
            },
            "scoring": {
                "baseScore": 10,
                "comboMultiplier": 5,
                "perfectBonus": 50
            },
            "items": {
                "maxPerRun": {
                    "ADD_TIME": 2,
                    "HINT": 2,
                    "SHIELD": 1,
                    "REMOVE_TRAP": 1,
                    "SLOW_TIME": 1,
                    "SHUFFLE": 1
                },
                "costs": {
                    "ADD_TIME": 100,
                    "HINT": 150,
                    "SHIELD": 300,
                    "REMOVE_TRAP": 250,
                    "SLOW_TIME": 200,
                    "SHUFFLE": 150
                }
            }
        }
        
        configs = {
            "game_config.json": game_config,
            "balance_config.json": balance_config,
        }
        
        for filename, data in configs.items():
            file_path = config_path / filename
            with open(file_path, 'w', encoding='utf-8') as f:
                json.dump(data, f, indent=2)
            
            self.stats['json_files'] += 1
            self.print_success(f"Created: {filename}")
    
    def create_documentation(self):
        """Create project documentation"""
        self.print_header("STEP 4: Creating Documentation")
        
        docs = {
            "SETUP_STATUS.md": self.get_setup_status_doc(),
            "COPY_GUIDE.md": self.get_copy_guide_doc(),
            "FILE_MAP.md": self.get_file_map_doc(),
        }
        
        for filename, content in docs.items():
            file_path = self.project_root / filename
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            
            self.stats['doc_files'] += 1
            self.print_success(f"Created: {filename}")
    
    def get_package_only(self, package_path):
        """Generate file with only package declaration"""
        package_name = f"com.colortrap.game.{package_path}"
        return f"""package {package_name}

// TODO: Copy code from artifacts
// See COPY_GUIDE.md for artifact sources
"""
    
    def get_gamemode_template(self):
        return """package com.colortrap.game.data.models

enum class GameMode {
    NORMAL,
    HARD,
    SUPER_HARD,
    RELAX,
    ENDLESS,
    DAILY_CHALLENGE
}
"""
    
    def get_itemtype_template(self):
        return """package com.colortrap.game.data.models

enum class ItemType {
    ADD_TIME,
    HINT,
    SHIELD,
    REMOVE_TRAP,
    SLOW_TIME,
    SHUFFLE
}
"""
    
    def get_levelmodifier_template(self):
        return """package com.colortrap.game.data.models

enum class LevelModifier {
    COLORS_DISAPPEAR,
    SHUFFLE_TILES,
    SIMILAR_COLORS,
    TEXT_ONLY,
    COMBO_MODIFIER
}
"""
    
    def get_difficultylevel_template(self):
        return """package com.colortrap.game.data.models

enum class DifficultyLevel {
    EASY,
    MEDIUM,
    HARD,
    SUPER_HARD
}
"""
    
    def get_setup_status_doc(self):
        return """# Setup Status

## ✅ Completed by Script

- [x] Folder structure created
- [x] Kotlin files generated (with package declarations)
- [x] JSON configs created
- [x] Asset folders created
- [x] Documentation generated

## ⏳ Manual Steps Required

### 1. Copy Code into Kotlin Files
See `COPY_GUIDE.md` for detailed instructions

### 2. Update Build Files
- [ ] Replace `app/build.gradle.kts` (from artifact: build_gradle_centralized)
- [ ] Replace `app/src/main/res/values/strings.xml` (from artifact: strings_xml_centralized)

### 3. Add Assets
- [ ] Copy your color assets to `app/src/main/assets/skins/color/`

### 4. Sync & Build
- [ ] File → Sync Project with Gradle Files
- [ ] Build → Rebuild Project

## 📊 Statistics

""" + f"""- Kotlin files: {self.stats['kotlin_files']}
- JSON configs: {self.stats['json_files']}
- Documentation: {self.stats['doc_files']}
"""
    
    def get_copy_guide_doc(self):
        return """# Copy Guide - Artifact to File Mapping

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
"""
    
    def get_file_map_doc(self):
        return """# File Map - Complete Structure

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
"""
    
    def print_summary(self):
        """Print completion summary"""
        self.print_header("✅ SETUP COMPLETE!")
        
        print(f"\n{Colors.CYAN}📊 Summary:{Colors.END}")
        print(f"  • Folders: {Colors.GREEN}{self.stats['folders']}{Colors.END}")
        print(f"  • Kotlin files: {Colors.GREEN}{self.stats['kotlin_files']}{Colors.END}")
        print(f"  • JSON configs: {Colors.GREEN}{self.stats['json_files']}{Colors.END}")
        print(f"  • Documentation: {Colors.GREEN}{self.stats['doc_files']}{Colors.END}")
        
        print(f"\n{Colors.YELLOW}📝 What's Created:{Colors.END}")
        print(f"  ✓ All folder structure")
        print(f"  ✓ Empty Kotlin files (with package declarations)")
        print(f"  ✓ 4 enum files with complete code")
        print(f"  ✓ JSON configs (game_config.json, balance_config.json)")
        print(f"  ✓ Asset folders")
        print(f"  ✓ Documentation (3 files)")
        
        print(f"\n{Colors.CYAN}📂 Files Created:{Colors.END}")
        print(f"  • {Colors.MAGENTA}SETUP_STATUS.md{Colors.END} - Current status & checklist")
        print(f"  • {Colors.MAGENTA}COPY_GUIDE.md{Colors.END} - Which code goes where")
        print(f"  • {Colors.MAGENTA}FILE_MAP.md{Colors.END} - Complete file structure")
        
        print(f"\n{Colors.YELLOW}⏭️ Next Steps:{Colors.END}")
        print(f"  1. Read {Colors.CYAN}COPY_GUIDE.md{Colors.END}")
        print(f"  2. Start copying code from artifacts")
        print(f"  3. Priority: data/config/* files first")
        print(f"  4. Replace build.gradle.kts")
        print(f"  5. Replace strings.xml")
        print(f"  6. Sync Gradle & Build")
        
        print(f"\n{Colors.GREEN}🚀 Ready to copy code!{Colors.END}")
        print(f"{Colors.BLUE}Open COPY_GUIDE.md for detailed instructions{Colors.END}\n")
    
    def run(self):
        """Main execution flow"""
        self.print_header("ColorTrap - Phase 2 Auto Setup")
        
        try:
            # Find project
            self.print_info("Locating project...")
            self.project_root = self.find_project_root()
            
            if not self.project_root.exists():
                self.print_error(f"Project not found: {self.project_root}")
                return False
            
            self.print_success(f"Found: {self.project_root}")
            
            # Verify
            if not self.verify_project():
                self.print_error("Not a valid Android project")
                return False
            
            # Execute steps
            self.create_kotlin_structure()
            self.create_asset_structure()
            self.create_json_configs()
            self.create_documentation()
            
            # Summary
            self.print_summary()
            
            return True
            
        except KeyboardInterrupt:
            print(f"\n\n{Colors.YELLOW}⚠ Cancelled by user{Colors.END}")
            return False
        except Exception as e:
            self.print_error(f"Error: {e}")
            import traceback
            traceback.print_exc()
            return False

def main():
    setup = ColorTrapSetup()
    success = setup.run()
    sys.exit(0 if success else 1)

if __name__ == "__main__":
    main()