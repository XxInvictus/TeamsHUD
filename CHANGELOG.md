# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.6.0] - 2025-12-09

### Added

- Automatic config file migration from legacy "teams" mod to "teamshudplus"
  - Automatically copies Fabric JSON config from old location to new
  - Automatically copies Forge TOML client and server configs
  - Preserves original config files for safety
  - Logs migration status for troubleshooting
- Optional LegacyDataMigration helper for debugging team data migration
- Teammate distance tracking feature
  - Real-time distance calculation from player to teammates
  - Configurable distance display on HUD
  - Updates dynamically based on player position

### Changed

- Improved Javadoc documentation across the codebase
- Enhanced error handling for config migration

### Fixed

- Resolved Javadoc warnings in build process
- Corrected error handling in config file operations

## [1.5.x] - 2025-11-18

### Added

- HUD drag functionality
  - Drag and reposition HUD elements on screen
  - Persistent HUD position saving
  - Configurable drag sensitivity
  - Lock/unlock toggle to prevent accidental movement
  - HUD lock toggle button in Teams menu
- Configurable HUD scaling
  - Adjustable scale factor for HUD elements
  - Client-side scale configuration
  - Scale for StatusOverlay and CompassOverlay
- Server-enforced max compass detection distance config option

### Changed

- Improved HUD overlay drag interaction and visibility
- Refined validation logic for HUD scale values
- Updated config structures to support new features

### Fixed

- HUD persistence issues with position and scale settings
- HUD overlay scaling and lock state issues
- StatusOverlay element misalignment when scaled
- Improved validation of client-supplied scale values

## [1.5.0] - 2025-11-14

### Added

- Network packet validation and REMOVE case handling
- Validation for enums and team creation
- UUID parsing error handling
- Advancement null validation
- Null safety to `isTeammate()` method
- Persistence trigger on player disconnect to prevent data loss

### Changed

- Reordered player lookup to validate existence before name lookup
- Improved exception handling in NBT loading with better error messages
- Enhanced error handling in packet handlers with context and stack traces
- Replaced wildcard imports with explicit imports for better code clarity
- Fixed logging string concatenation in ClientTeamImpl with parameterized logging
- Made mutable collection fields final in ModTeam for thread safety
- Renamed `type1` variable to `textureType` for better clarity
- Updated README with comprehensive mod documentation
- Enhanced .gitignore with IDE, OS-specific, and log file patterns

### Fixed

- Critical team persistence bugs in ModTeam
- Critical empty team and profile cache issues
- Removed unnecessary TODO comment in TeamsKeys
- Removed stray semicolon after imports in ModTeam

## Earlier History

### Project Origins

This mod is a continuation and enhancement of:

- TeamsHUD by Tfarcenim
- Teams by CommodoreThrawn

### Mod ID Migration

- Previous mod ID: `teams`
- Current mod ID: `teamshudplus`
- Package migration: `com.t2pellet.teams.*` → `com.xxinvictus.teamshudplus.*`

**Note:** Team data automatically migrates due to preserved NBT storage keys. Config files are automatically migrated on first launch.
