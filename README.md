# RTPGUI

A modern Minecraft plugin that provides a graphical user interface for random teleportation commands, with support for hex colors and Brigadier commands.

## About

This is a fork of the original RTP GUI plugin found at [SpigotMC](https://www.spigotmc.org/resources/rtp-gui-betterrtp-required.126008/).

## Features

- **GUI Menu System**: Easy-to-use graphical interface for random teleportation
- **Hex Color Support**: Use modern hex colors with the `&#FFFFFF` format
- **Brigadier Commands**: Modern command system with `/rtpgui reload`
- **Configurable**: Fully customizable GUI, items, colors, and sounds
- **Multi-World Support**: Overworld, Nether, and End teleportation options

## Requirements

- Minecraft 1.21+
- Paper or Paper-based server (Purpur, Pufferfish, etc.)
- BetterRTP plugin (for teleportation functionality)

## Installation

1. Download the latest release
2. Place the JAR file in your server's `plugins` folder
3. Install [BetterRTP](https://www.spigotmc.org/resources/betterrtp.36081/)
4. Restart your server
5. Configure the plugin in `plugins/RTPGUI/config.yml`

## Commands

- `/rtp` - Opens the RTP GUI menu
- `/rtpgui reload` - Reloads the plugin configuration (requires `rtpgui.reload` permission)

## Permissions

- `rtpgui.use` - Allows using the /rtp command (default: true)
- `rtpgui.reload` - Allows reloading the plugin configuration (default: op)

## Configuration

The plugin is highly configurable through `config.yml`:

### Hex Color Support

You can use hex colors in any text field using the format `&#RRGGBB`:

```yaml
gui:
  title: "&#00AAFF&lRTP Menu"

items:
  overworld:
    display-name: "&#00FF00&lOverworld RTP"
    lore:
      - "&7Click to teleport to a"
      - "&#00FF00&lOverworld"
```

### Legacy Colors

Traditional Minecraft color codes are also supported:
- `&a` - Green
- `&c` - Red
- `&e` - Yellow
- `&l` - Bold
- And all other standard color codes

### Item Configuration

Each dimension (Overworld, Nether, End) can be customized:
- Material type
- Display name (with hex colors)
- Lore (description)
- GUI slot position
- Command to execute

### Sound Configuration

Configure the sound played when clicking items:
```yaml
sound:
  click: "ENTITY_EXPERIENCE_ORB_PICKUP"
  volume: 1.0
  pitch: 1.0
```

## Building from Source

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/`.

## Support

For issues or questions, please open an issue on this repository.

## License

This project is a fork and maintains compatibility with the original plugin's functionality while adding modern features.

## Credits

- Original plugin: [RTP GUI on SpigotMC](https://www.spigotmc.org/resources/rtp-gui-betterrtp-required.126008/)
- Fork maintainer: TSERATO
