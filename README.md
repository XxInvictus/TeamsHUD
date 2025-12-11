[![License: AGPL v3](https://img.shields.io/badge/License-AGPL_v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0) [![Static Badge](https://img.shields.io/badge/github-repo-blue?logo=github&link=https%3A%2F%2Fgithub.com%2FXxInvictus%2FTeamsHUDPlus)](https://github.com/XxInvictus/TeamsHUDPlus) [![Dev Build & Test](https://github.com/XxInvictus/TeamsHUDPlus/actions/workflows/dev-build.yml/badge.svg?branch=1.20.1-dev)](https://github.com/XxInvictus/TeamsHUDPlus/actions/workflows/dev-build.yml) [![Release Build & Publish](https://github.com/XxInvictus/TeamsHUDPlus/actions/workflows/release.yml/badge.svg)](https://github.com/XxInvictus/TeamsHUDPlus/actions/workflows/release.yml)  
[![Static Badge](https://img.shields.io/badge/Get%20on%20Curseforge-link-chocolate?logo=curseforge&link=https%3A%2F%2Fwww.curseforge.com%2Fminecraft%2Fmc-mods%2Fteams-hud-plus)](https://www.curseforge.com/minecraft/mc-mods/teams-hud-plus)
 [![Static Badge](https://img.shields.io/badge/Get%20on%20Modrinth-link-forestgreen?logo=modrinth&link=https%3A%2F%2Fmodrinth.com%2Fmod%2Fteams-hud-plus)](https://modrinth.com/mod/teams-hud-plus)

# TeamsHUDPlus

**A Minecraft mod for seamless team management and real-time teammate HUD overlays.**

TeamsHUDPlus is a continuation and expansion of the original TeamsHUD and Teams mods, providing:

- Intuitive team creation and management
- Visual compass and status HUDs
- Server-enforced limits and migration helpers

---

## User Guide

## Features

### Team Management

- **Create and manage teams**: Form and manage teams with other players
- **Player invitations**: Send/accept team invites via toast notifications
- **Join requests**: Request to join existing teams
- **Team permissions**: Assign leaders with management rights
- **Player kicking**: Remove players (with permission)

### HUD Elements

- **Compass HUD**: Visual compass showing teammate locations/distances
- **Status HUD**: Real-time teammate health and hunger
- **Configurable detection distance**: Set how far teammates are detected
- **Toggle visibility**: Enable/disable HUD elements on the fly
- **Server-enforced compass limits**: Admins can set max detection range

### Migration & Compatibility

- **Automatic config migration**: Copies legacy Teams configs on first launch

### Keybindings

- **Accept** (`]`): Accept invites/requests
- **Reject** (`[`): Reject invites/requests
- **Toggle HUD** (`B`): Show/hide compass and status HUD
- **Toggle HUD Lock** (`L`): Lock/unlock HUD positioning (drag mode)

### HUD Customization

- **Lock/Unlock HUD**: Press `L` or use Teams menu
  - Drag overlays when unlocked (white borders)
  - Click/drag to reposition; press `ESC`/`L` to lock
- **Scale HUD elements**: 0.1x–3.0x (configurable)
  - Adjust `statusOverlayScale`/`compassOverlayScale` in config
- **Position persistence**: HUD positions/scales saved to config
- **Drag mode tips**: Green indicator when dragging; overlays move independently

### Configuration Options

Configuration is split into **Server** (admin) and **Client** (user) settings. Most options are available in the in-game mod config menu or as TOML files.

#### Server (admin-controlled, affects all players)

- `show_invisible_teammates` (default: true): Allow teammates to see each other when invisible
- `friendly_fire_enabled` (default: false): Allow teammates to damage each other
- `name_tag_visibility` (default: ALWAYS): Control when team name tags are visible
- `colour` (default: BOLD): Team color formatting
- `death_message_visibility` (default: ALWAYS): Control when death messages are shown
- `collision_rule` (default: PUSH_OWN_TEAM): Set collision rules for team members
- `sync_advancements` (default: true): Sync advancements between team members
- `max_compass_detection_distance` (default: 512, min: 16, max: 2048): Maximum allowed compass detection distance (clients cannot exceed this)

#### Client (per-user, visual and HUD options)

- `enable_compass_hud` (default: true): Show/hide the compass HUD
- `enable_status_hud` (default: true): Show/hide the status HUD
- `toast_duration` (default: 5): Duration of team toast notifications (seconds)
- `show_hunger` (default: true): Show other team members' hunger bars
- `compass_detection_distance` (default: 128, min: 16, max: 1024): Maximum detection distance for compass HUD (cannot exceed server max)

**HUD Positioning:**

- `status_overlay_x` / `status_overlay_y` (default: -1): X/Y position of status HUD (-1 = default/auto)
- `compass_overlay_x` / `compass_overlay_y` (default: -1): X/Y position of compass HUD (-1 = default/auto)
- `status_overlay_scale` (default: 1.0, min: 0.1, max: 3.0): Scale of status HUD
- `compass_overlay_scale` (default: 1.0, min: 0.1, max: 3.0): Scale of compass HUD

**Distance Counter:**

- `show_teammate_distance` (default: false): Show distance to teammates next to their names
- `teammate_distance_update_frequency` (default: 20, min: 1, max: 200): How often to update teammate distances (in ticks)
- `distance_only_within_compass_range` (default: false): Only show distance if teammate is within compass detection range

## Installation

### Steps

1. Download the correct version for Forge or Fabric
2. Place the JAR in your `mods` folder
3. Launch Minecraft with the chosen loader
4. Configure keybindings/settings in game options


## Usage

### Creating a Team

1. Open the Teams screen
2. Create a team with a custom name
3. Invite players by username

### Joining a Team

1. Receive a team invite (toast notification)
2. Press `]` to accept, `[` to reject

### Managing Team Members

- Leaders can invite/kick members
- Players can leave teams anytime

### Using the HUD

- Compass HUD: Shows teammate directions
- Status HUD: Real-time health/hunger and teammate distance (distance shown next to names, e.g., `PlayerName - 123m`)
- Press `B` to toggle HUD, configure in mod settings

---

## Development Guide

### Building from Source

This project uses a MultiLoader template (Forge & Fabric).

#### IntelliJ IDEA

1. Clone this repository
2. Import as a Gradle project
3. Set Project SDK to Java 17
4. In Gradle panel: `Common > Tasks > vanilla gradle > decompile`
5. In Gradle panel: `Forge > Tasks > forgegradle runs > genIntellijRuns`
6. Run configs for Forge and Fabric will appear

#### Build Commands

```bash
# Build all versions
./gradlew build

# Build Forge only
./gradlew :forge:build

# Build Fabric only
./gradlew :fabric:build

# Run checks and tests
./gradlew check
```

Built JARs:

- Forge: `forge/build/libs/`
- Fabric: `fabric/build/libs/`

### Architecture

- Minecraft 1.20.1
- Java 17 or higher
- **Forge**: 47.4.0+
- **Fabric**: Loader 0.18.1+ & Fabric API 0.92.6+

#### Folder Structure

- **Common**: Shared code (vanilla Minecraft)
- **Forge**: Forge-specific code
- **Fabric**: Fabric-specific code

Network packets handle client-server sync for team data/player updates.

### License

This project is licensed under the GNU Affero General Public License v3.0 – see [LICENSE.md](LICENSE.md).

### Authors

- XxInvictus (TeamsHUDPlus)
- Tfarcenim ([TeamsHUD](https://www.curseforge.com/minecraft/mc-mods/teams-hud))
- CommodoreThrawn ([Teams](https://www.curseforge.com/minecraft/mc-mods/teams))

### Changelog

See [CHANGELOG.md](CHANGELOG.md) for version history.
