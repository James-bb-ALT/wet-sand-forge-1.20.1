# Wet Sand (Unofficial Forge 1.20.1 Port)

> **Note:** This is an **unofficial Minecraft Forge 1.20.1 port** of the **Wet Sand** mod.
> All original concept, textures, assets, and gameplay design belong to the original author, **HearthianDev**.
>
> - **Original Mod & Author:** [HearthianDev/wet-sand](https://github.com/HearthianDev/wet-sand)
> - **License:** [GNU General Public License v3.0 (GPL-3.0)](LICENSE)

## Features
- **Humidity Progression:** Vanilla `sand` and `red_sand` near water gradually absorb moisture up to 3 blocks away (`sand` → `moist_sand` → `wet_sand` → `soaked_sand`, and `red_sand` equivalents).
- **Physics & Behavior:**
  - `moist_sand` and `moist_red_sand` still fall under gravity.
  - `wet_sand`, `soaked_sand`, `wet_red_sand`, and `soaked_red_sand` hold together and do not fall.
  - `soaked_sand` and `soaked_red_sand` have a slightly sunken collision box (`14/16` height).
- **Sugar Cane:** Sugar cane can be planted and survive on `wet` and `soaked` sand variants without requiring adjacent water.
- **Drying with Glass Bottle:** Right-clicking wettable sand variants with a `glass_bottle` lowers their humidity level by one step and gives a Water Bottle.
- **Concrete Recipes:** Moist, wet, and soaked sand variants can be used in place of regular sand to craft concrete.

## Building & Testing
Requires **JDK 17**:
```bash
./gradlew build
./gradlew runGameTestServer
```
