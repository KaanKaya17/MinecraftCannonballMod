# 💣 Realistic Cannonball Mod

A detailed, physically simulated Minecraft mod introducing custom 3D-modeled field artillery, high-velocity roundshot ballistics, ricochet physics, structural penetration, recoil animations, and interactive player driving.

![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen)
![Loader](https://img.shields.io/badge/Modloader-Fabric-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## 🌟 Features

### 🎯 1. Custom 3D Models & Dynamic Animations
- **Field Cannon**: Multi-part model featuring a wooden carriage trail, iron axle, spoked wheels with iron rims, side cheeks, trunnions, cascabel button, and a cast iron cannon barrel.
- **Synchronized 360° Aiming**: Mount the cannon and aim seamlessly using your mouse. Vertical elevation reaches from -55° (high-angle bombardment) to +15° (depression).
- **Physical Recoil**: Firing the cannon triggers an authentic axial recoil where the barrel kicks backward against the carriage and dampens forward.
- **Driveable Artillery (WASD Controls)**: When mounted, steer and drive the cannon forward and backward (`W` / `S` keys) with authentic wooden wheel rolling sound effects and smooth step-up (`0.6m`).
- **Breakable Entity**: Left-clicking in Creative instantly breaks it; in Survival mode, taking damage plays impact sounds, sheds wooden/iron debris, and drops the cannon along with any loaded ammo.

### 💨 2. Realistic Ballistics Engine
- **Aerodynamic Drag & Gravity**: Unlike default Minecraft projectiles, cannonballs follow true parabolic ballistic curves calculated with air resistance (`0.992` drag factor) and mass acceleration.
- **Ricochet Mechanics**: Glancing impacts against hard surfaces (Stone, Deepslate, Obsidian, Iron, Cobblestone) produce high-pitch metallic ricochets, sparks, and direction changes.
- **Penetration & Block Destruction**: Direct hits on softer materials (Wood, Glass, Dirt, Leaves) punch straight through blocks with kinetic deceleration, showering debris and continuing along their trajectory.
- **Variable Charge Power**: Load 1 to 3 bags of gunpowder to adjust muzzle velocity (up to 4.4 blocks/tick), distance, and blast yield.
- **Smooth In-Flight Trail & Airburst**: Trailing dense smoke plumes and fire sparks in flight, with visible airburst detonation if the round detonates high in the sky.

### 🔴 3. Dispenser & Redstone Integration
- Automate harbor defenses and fortress batteries by firing cannonballs directly from vanilla **Dispensers** using Redstone signals!

---

## 🕹️ Controls & Gameplay

1. **Deploying**:
   - Place the **Field Cannon** item on any solid surface.
2. **Aiming & Driving**:
   - Right-click with an empty hand to mount the cannon.
   - Look with your **Mouse** to aim yaw and pitch.
   - Press **`W`** to move forward, **`S`** to reverse.
   - Press **`Shift`** to dismount.
3. **Loading the Cannon**:
   - **Step 1**: Right-click the cannon with **Gunpowder** (load 1 to 3 bags).
   - **Step 2**: Right-click with an **Iron Cannonball**.
   - **Step 3**: Right-click with a **Ramrod** to pack the round firmly into the chamber.
4. **Firing**:
   - Right-click with **Flint and Steel** or a **Fire Charge** to light the touchhole fuse (or power the block beneath/behind the cannon with **Redstone**).

---

## 🔨 Crafting Recipes

### Field Cannon
```
[   ] [Iron Block] [   ]
[Iron Block] [Iron Block] [Iron Block]
[Iron Ingot] [Oak Log] [Iron Ingot]
```

### Iron Cannonball (x4)
```
[   ] [Iron Ingot] [   ]
[Iron Ingot] [Coal / Charcoal] [Iron Ingot]
[   ] [Iron Ingot] [   ]
```

### Ramrod
```
[   ] [   ] [Iron Ingot]
[   ] [Stick] [   ]
[Stick] [   ] [   ]
```

---

## 📦 Installation

1. Install **Minecraft 1.20.1**.
2. Install **[Fabric Loader](https://fabricmc.net/)** (version `>=0.19.5`).
3. Download **[Fabric API](https://modrinth.com/mod/fabric-api)** and place it into your `.minecraft/mods` folder.
4. Build or download `cannonball-1.0.0.jar` and place it into your `.minecraft/mods` folder.
5. Launch the game and enjoy!

---

## 🛠️ Building from Source

```bash
git clone https://github.com/KaanKaya17/MinecraftCannonballMod.git
cd MinecraftCannonballMod
./gradlew build
```
Compiled JAR will be available in `build/libs/cannonball-1.0.0.jar`.

---

## 📄 License

This project is licensed under the MIT License.
Developed by **Kaan Kaya**.
