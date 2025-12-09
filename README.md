# TeamsHUDPlus

A Minecraft mod that adds team management functionality with visual HUD elements for both Forge and Fabric mod loaders. Continuation of the work by Tfarcenim (TeamsHUD) and CommodoreThrawn (Teams) with additional functionality I wish they had. I have raised Pull Requests against the TeamsHUD source for these features and may still scrap this continuation if they get accepted and merged into that mod.

## Features

### Team Management
- **Create and manage teams**: Form teams with other players
- **Player invitations**: Send and accept team invitations via toast notifications
- **Join requests**: Request to join existing teams
- **Team permissions**: Designate team leaders with management permissions
- **Player kicking**: Remove players from teams (requires permissions)

### HUD Elements
- **Compass HUD**: Visual compass showing teammate locations and distances
- **Status HUD**: Display teammate health and hunger levels in real-time
- **Configurable detection distance**: Customize how far teammates can be detected
- **Toggle visibility**: Enable/disable HUD elements on the fly

### Keybindings
- **Accept** (Right Bracket `]`): Accept team invitations or join requests
- **Reject** (Left Bracket `[`): Reject team invitations or join requests  
- **Toggle HUD** (B): Toggle compass and status HUD visibility
- **Toggle HUD Lock** (L): Lock/unlock HUD positioning (opens drag mode when unlocked)

### HUD Customization
- **Lock/Unlock HUD**: Press **L** or use the button in the Teams menu to toggle HUD lock
  - When **unlocked**: A transparent overlay appears allowing you to drag HUD elements
  - White borders appear around draggable elements
  - Click and drag the Status HUD or Compass HUD to reposition them
  - Press **ESC** or **L** to exit drag mode and lock the HUD
- **Scale HUD Elements**: Configure scaling from 0.1x (10%) to 3.0x (300%) in the mod settings
  - Adjust `statusOverlayScale` and `compassOverlayScale` in config files
  - Default scale: 1.0 (100%)
- **Position Persistence**: HUD positions and scales are saved to your config file
- **Drag Mode Tips**:
  - Drag indicator turns green when actively dragging an element
  - Both overlays can be positioned independently
  - Works with scaled overlays

### Configuration Options
- Show invisible teammates
- Name tag visibility settings
- Death message visibility settings
- Collision rules
- Compass HUD enable/disable
- Status HUD enable/disable
- Compass detection distance
- **Distance Counter**: Show real-time distance to teammates
  - Enable/disable teammate distance display
  - Configurable update frequency (1-200 ticks)
  - Option to show distance only within compass range

## Installation

### Prerequisites
- Minecraft 1.20.1
- Java 17 or higher
- **Forge**: Version 47.4.0 or higher
- **Fabric**: Fabric Loader 0.18.1+ and Fabric API 0.92.6+

### Steps
1. Download the appropriate version for your mod loader (Forge or Fabric)
2. Place the downloaded JAR file in your Minecraft `mods` folder
3. Launch Minecraft with the corresponding mod loader
4. Configure keybindings and settings in the game options menu

## Usage

### Creating a Team
1. Open the teams screen using the mod's interface
2. Create a new team with a custom name
3. Invite players by username

### Joining a Team
1. Receive a team invitation via toast notification
2. Press the **Accept** key (Right Bracket) to join
3. Or press the **Reject** key (Left Bracket) to decline

### Managing Team Members
- Team leaders can invite new members
- Team leaders can kick members
- Players can leave teams at any time

### Using the HUD
- The compass HUD shows directional indicators to teammates
- The status HUD displays real-time health and hunger information
- Press **Toggle HUD** (B) to show/hide HUD elements
- Configure detection distance and visibility in mod settings

### Distance Tracking
- **Real-time distance display**: Shows distance to teammates next to their names (e.g., "PlayerName - 123m")
- **Configurable updates**: Set update frequency from 1-200 ticks (default: 20 ticks/1 second)
  - Higher values = better performance, lower accuracy
  - Lower values = more accurate, slight performance impact
- **Compass range filtering**: Option to only show distance for teammates within compass detection range
- **Automatic handling**: Distances automatically hide for dead teammates or those in unloaded chunks
- **Configuration**: Enable in mod settings (`show_teammate_distance`, `teammate_distance_update_frequency`, `distance_only_within_compass_range`)
- **Format**: Horizontal distance (X-Z plane) displayed in meters

## Building from Source

This project uses a MultiLoader template supporting both Forge and Fabric.

### IntelliJ IDEA
1. Clone this repository
2. Import as a Gradle project in IntelliJ IDEA
3. Set Project SDK to Java 17
4. Navigate to Gradle panel > Common > Tasks > vanilla gradle > decompile
5. Navigate to Gradle panel > Forge > Tasks > forgegradle runs > genIntellijRuns
6. Run configurations for both Forge and Fabric will be available

### Build Commands
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

Built JARs will be located in:
- Forge: `forge/build/libs/`
- Fabric: `fabric/build/libs/`

## License

This project is licensed under the GNU Affero General Public License v3.0 - see the [LICENSE.md](LICENSE.md) file for details.

## Authors

- XxInvictus (TeamsHUDPlus)
- Tfarcenim ([TeamsHUD](https://www.curseforge.com/minecraft/mc-mods/teams-hud))
- CommodoreThrawn ([Teams](https://www.curseforge.com/minecraft/mc-mods/teams))

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for a detailed version history.

## Development

The mod uses a common sourceset architecture:
- **Common**: Shared code compiled against vanilla Minecraft
- **Forge**: Forge-specific implementations
- **Fabric**: Fabric-specific implementations

Network packets handle client-server synchronization for team data and player updates.
