#!/usr/bin/env python3
"""
ColorTrap - Complete Project Structure Generator
Creates all folders and empty files for the entire project
"""

import os
from pathlib import Path

# ANSI colors
class Colors:
    GREEN = '\033[92m'
    BLUE = '\033[94m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    CYAN = '\033[96m'
    BOLD = '\033[1m'
    END = '\033[0m'

def print_header(text):
    print(f"\n{Colors.CYAN}{Colors.BOLD}{'='*60}{Colors.END}")
    print(f"{Colors.CYAN}{Colors.BOLD}{text:^60}{Colors.END}")
    print(f"{Colors.CYAN}{Colors.BOLD}{'='*60}{Colors.END}\n")

def print_success(text):
    print(f"{Colors.GREEN}✓ {text}{Colors.END}")

def print_info(text):
    print(f"{Colors.BLUE}→ {text}{Colors.END}")

def print_warning(text):
    print(f"{Colors.YELLOW}⚠ {text}{Colors.END}")

def find_project_root():
    """Find ColorTrap project root"""
    current = Path.cwd()
    
    # Check if we're already in project root
    if (current / "app" / "src" / "main").exists():
        return current
    
    # Check if ColorTrap folder exists
    if current.name == "ColorTrap":
        return current
    
    # Look for ColorTrap in common locations
    possible_paths = [
        current / "ColorTrap",
        Path.home() / "Downloads" / "ColorTrap",
        Path.home() / "Desktop" / "ColorTrap",
        Path.home() / "Projects" / "ColorTrap"
    ]
    
    for path in possible_paths:
        if path.exists() and (path / "app").exists():
            return path
    
    # Ask user for path
    print_warning("Could not auto-detect project root")
    print_info("Current directory: " + str(current))
    user_path = input(f"{Colors.YELLOW}Enter ColorTrap project path: {Colors.END}")
    
    return Path(user_path)

def create_file_structure():
    """Complete file structure for ColorTrap"""
    
    return {
        # ==================== DATA LAYER ====================
        "data/models": [
            "GameMode.kt",
            "ItemType.kt",
            "LevelModifier.kt",
            "DifficultyLevel.kt",
            "DynamicColorGroup.kt",
            "TileVariant.kt",
            "DynamicLevel.kt",
            "DynamicTile.kt",
            "DynamicGameState.kt",
        ],
        
        "data/config": [
            "GameConfig.kt",
            "ShopConfig.kt",
            "TextConfig.kt",
            "BalanceConfig.kt",
        ],
        
        "data/repository": [
            "GameRepository.kt",
            "PreferencesRepository.kt",
        ],
        
        "data/local": [
            "PreferencesManager.kt",
        ],
        
        # ==================== DOMAIN LAYER ====================
        "domain": [
            "DynamicSkinManager.kt",
            "DifficultyBasedSelector.kt",
            "DynamicLevelGenerator.kt",
            "ConfigManager.kt",
        ],
        
        # ==================== UTILS ====================
        "utils": [
            "DynamicAssetScanner.kt",
            "AssetLoader.kt",
            "SoundManager.kt",
            "VibrationManager.kt",
            "AdManager.kt",
            "Constants.kt",
            "Extensions.kt",
        ],
        
        # ==================== UI - COMPONENTS ====================
        "ui/components": [
            "DynamicTileGrid.kt",
            "DynamicColorDisplayBar.kt",
            "TopBar.kt",
            "ItemBar.kt",
        ],
        
        # ==================== UI - SCREENS ====================
        "ui/screens/splash": [
            "SplashScreen.kt",
            "SplashViewModel.kt",
        ],
        
        "ui/screens/menu": [
            "MainMenuScreen.kt",
            "MainMenuViewModel.kt",
        ],
        
        "ui/screens/game": [
            "DynamicGameScreen.kt",
            "DynamicGameViewModel.kt",
        ],
        
        "ui/screens/gameover": [
            "GameOverScreen.kt",
            "GameOverViewModel.kt",
        ],
        
        "ui/screens/shop": [
            "ShopScreen.kt",
            "ShopViewModel.kt",
        ],
        
        "ui/screens/settings": [
            "SettingsScreen.kt",
            "SettingsViewModel.kt",
        ],
        
        # ==================== UI - NAVIGATION ====================
        "ui/navigation": [
            "Screen.kt",
            "AppNavGraph.kt",
        ],
        
        # Note: ui/theme already exists from project creation
    }

def create_kotlin_package_header(package_path):
    """Generate package declaration"""
    package_name = "com.colortrap.game." + package_path.replace("/", ".")
    return f"package {package_name}\n\n"

def create_folders_and_files(project_root):
    """Create all folders and empty Kotlin files"""
    
    kotlin_base = project_root / "app" / "src" / "main" / "java" / "com" / "colortrap" / "game"
    
    if not kotlin_base.exists():
        print_warning(f"Kotlin base path does not exist: {kotlin_base}")
        return False
    
    file_structure = create_file_structure()
    total_files = 0
    total_folders = 0
    
    for folder_path, files in file_structure.items():
        # Create folder
        full_folder_path = kotlin_base / folder_path
        full_folder_path.mkdir(parents=True, exist_ok=True)
        
        if not (full_folder_path / ".exists").exists():
            total_folders += 1
            print_success(f"Created folder: {folder_path}/")
        
        # Create files
        for filename in files:
            file_path = full_folder_path / filename
            
            if not file_path.exists():
                # Create file with package declaration
                package_header = create_kotlin_package_header(folder_path)
                
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(package_header)
                    f.write("// TODO: Copy code from artifacts\n")
                
                total_files += 1
                print_info(f"  ✓ {folder_path}/{filename}")
    
    return total_files, total_folders

def create_asset_folders(project_root):
    """Create asset folder structure"""
    
    assets_base = project_root / "app" / "src" / "main" / "assets"
    
    asset_folders = [
        "config",
        "skins/color",
        "audio",
        "effects",
        "ui",
    ]
    
    created = 0
    for folder in asset_folders:
        full_path = assets_base / folder
        if not full_path.exists():
            full_path.mkdir(parents=True, exist_ok=True)
            print_success(f"Created: assets/{folder}/")
            created += 1
    
    # Create .gitkeep files to preserve empty folders
    for folder in asset_folders:
        gitkeep_path = assets_base / folder / ".gitkeep"
        if not gitkeep_path.exists():
            gitkeep_path.touch()
    
    return created

def create_documentation(project_root):
    """Create project documentation files"""
    
    docs = {
        "README_STRUCTURE.md": """# ColorTrap - Project Structure

## 📁 Folder Organization

### Data Layer
- `data/models/` - Data models (9 files)
- `data/config/` - Configuration objects (4 files)
- `data/repository/` - Data repositories (2 files)
- `data/local/` - Local storage (1 file)

### Domain Layer
- `domain/` - Business logic (4 files)

### Utils
- `utils/` - Utility classes (7 files)

### UI Layer
- `ui/components/` - Reusable components (4 files)
- `ui/screens/` - Screen implementations (12 files)
- `ui/navigation/` - Navigation setup (2 files)
- `ui/theme/` - Theme configuration (already exists)

### Assets
- `assets/config/` - JSON configs
- `assets/skins/color/` - Color tile assets
- `assets/audio/` - Sound effects
- `assets/effects/` - Visual effects

## 🎯 Next Steps

1. Copy code from conversation artifacts into each file
2. Add your color assets to `assets/skins/color/`
3. Sync Gradle
4. Build project

Total files to fill: ~45 Kotlin files
""",
        
        "COPY_CHECKLIST.md": """# Copy Code Checklist

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
""",
    }
    
    created = 0
    for filename, content in docs.items():
        doc_path = project_root / filename
        if not doc_path.exists():
            with open(doc_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print_success(f"Created: {filename}")
            created += 1
    
    return created

def create_json_configs(project_root):
    """Create JSON config templates"""
    
    config_path = project_root / "app" / "src" / "main" / "assets" / "config"
    config_path.mkdir(parents=True, exist_ok=True)
    
    configs = {
        "game_config.json": """{
  "version": "1.0",
  "name": "ColorTrap",
  "skins": {
    "autoScan": true,
    "basePath": "skins",
    "supportedFormats": ["webp", "png", "jpg"],
    "defaultSkin": "color"
  }
}""",
        
        "balance_config.json": """{
  "version": "1.0",
  "modes": {
    "NORMAL": {
      "levels": []
    }
  },
  "scoring": {
    "baseScore": 10,
    "comboMultiplier": 5,
    "perfectBonus": 50
  }
}""",
    }
    
    created = 0
    for filename, content in configs.items():
        config_file = config_path / filename
        if not config_file.exists():
            with open(config_file, 'w', encoding='utf-8') as f:
                f.write(content)
            print_success(f"Created: assets/config/{filename}")
            created += 1
    
    return created

def main():
    print_header("ColorTrap - Project Structure Generator")
    
    # Find project root
    print_info("Locating project root...")
    project_root = find_project_root()
    
    if not project_root.exists():
        print(f"{Colors.RED}✗ Project root not found: {project_root}{Colors.END}")
        return
    
    print_success(f"Found project: {project_root}")
    
    # Verify it's an Android project
    if not (project_root / "app" / "src" / "main").exists():
        print(f"{Colors.RED}✗ Not a valid Android project structure{Colors.END}")
        return
    
    print("\n" + "="*60)
    
    # Create Kotlin files
    print_header("Creating Kotlin Files")
    files_created, folders_created = create_folders_and_files(project_root)
    print_success(f"Created {folders_created} folders and {files_created} files")
    
    # Create asset folders
    print_header("Creating Asset Folders")
    assets_created = create_asset_folders(project_root)
    print_success(f"Created {assets_created} asset folders")
    
    # Create JSON configs
    print_header("Creating JSON Configs")
    configs_created = create_json_configs(project_root)
    print_success(f"Created {configs_created} config files")
    
    # Create documentation
    print_header("Creating Documentation")
    docs_created = create_documentation(project_root)
    print_success(f"Created {docs_created} documentation files")
    
    # Summary
    print("\n" + "="*60)
    print_header("✅ STRUCTURE CREATION COMPLETE!")
    
    print(f"\n{Colors.CYAN}📊 Summary:{Colors.END}")
    print(f"  • Kotlin files: {Colors.GREEN}{files_created}{Colors.END}")
    print(f"  • Folders: {Colors.GREEN}{folders_created}{Colors.END}")
    print(f"  • Asset folders: {Colors.GREEN}{assets_created}{Colors.END}")
    print(f"  • Config files: {Colors.GREEN}{configs_created}{Colors.END}")
    print(f"  • Documentation: {Colors.GREEN}{docs_created}{Colors.END}")
    
    print(f"\n{Colors.YELLOW}📝 Next Steps:{Colors.END}")
    print(f"  1. Read {Colors.CYAN}COPY_CHECKLIST.md{Colors.END}")
    print(f"  2. Copy code from artifacts into each .kt file")
    print(f"  3. Replace {Colors.CYAN}app/build.gradle.kts{Colors.END}")
    print(f"  4. Replace {Colors.CYAN}app/src/main/res/values/strings.xml{Colors.END}")
    print(f"  5. Sync Gradle in IntelliJ")
    print(f"  6. Build project")
    
    print(f"\n{Colors.GREEN}🚀 Ready to copy code!{Colors.END}\n")

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print(f"\n\n{Colors.YELLOW}⚠ Cancelled by user{Colors.END}")
    except Exception as e:
        print(f"\n{Colors.RED}✗ Error: {e}{Colors.END}")
        import traceback
        traceback.print_exc()