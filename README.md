# Command Binds

A lightweight client-side mod for Minecraft that lets you map keyboard keys to arbitrary commands or chat text through a clean in-game GUI. No config file editing, no mods menu required — just press `V`, set up your binds, and go. Client-side only — safe to use on any server

---

## Features

- **Bind any key** to a `/command` or plain chat message
- **In-game GUI** — open with `V` (rebindable in Controls settings)
- **Scrollable binds list** with color-coded badges (green = command, gray = message)
- **Edit or delete** existing binds directly from the list
- **Persistent config** — binds survive restarts, saved to `config/commandbinds.json`
- Works in **singleplayer and multiplayer**

---

## Installation

1. Install Fabric Loader
2. Install Fabric API
3. Drop `commandbinds-fabric-1.21-1.0.0.jar` into your `mods/` folder
4. Launch the game

---

## Usage

| Action | How |
|---|---|
| Open the GUI | Press `V` in-game |
| Add a bind | Type text or `/command` → click **Pick Key** → press the desired key → click **Add** |
| Edit a bind | Click any row in the list — it loads back into the input fields |
| Delete a bind | Click **✗ Del** on any row |
| Trigger a bind | Press the bound key while no screen is open |

**Commands** must start with `/` — the mod strips the slash and sends them via the command channel automatically. Everything else is sent as a regular chat message.

---

## Examples

| Key | Input | Result |
|---|---|---|
| `F5` | `/gamemode creative` | Switches to Creative mode |
| `F6` | `/tp ~ ~50 ~` | Teleports 50 blocks up |
| `F7` | `Hello everyone!` | Sends a chat message |
| `F8` | `/kill` | Instant kill |

---

## Config

Binds are stored in `.minecraft/config/commandbinds.json` and are loaded automatically on startup. You can edit the file manually if needed — the format is straightforward:

```json
{
  "binds": [
    {
      "keyCode": 294,
      "text": "/gamemode creative",
      "label": "/gamemode creative"
    },
    {
      "keyCode": 295,
      "text": "Hello everyone!",
      "label": "Hello everyone!"
    }
  ]
}
```

`keyCode` values correspond to GLFW key codes.

---

## Building from Source

```bash
git clone https://github.com/CardeJIbka/commandbinds
cd commandbinds
./gradlew build
```

Output: `build/libs/commandbinds-fabric-1.21-1.0.0.jar`

Requires JDK 21+.

---

## License

MIT — see [LICENSE](LICENSE).