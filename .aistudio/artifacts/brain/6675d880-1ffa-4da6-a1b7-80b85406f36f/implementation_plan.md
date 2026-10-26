# Implementation Plan: Apple Liquid Glass Navigation Bar

## Overview
Enhance the bottom navigation bar (`LifeBridgeBottomBar`) to emulate Apple's signature liquid glass design language. This introduces multi-layered specular glass reflections, translucent frosted dock styling, and fluid viscous spring physics with dynamic squash-and-stretch transitions as the capsule glides between tabs.

---

## User Preferences Incorporated
- **Visual Glass Styling**: Translucent frosted glass with dynamic specular rim highlights, subtle inner curvature sheen, and ambient depth layering.
- **Transition Physics**: Fluid viscous jelly spring with slight horizontal squash and stretch during active tab transitions.

---

## Key Components & Architecture

### 1. Translucent Frosted Glass Dock Layer
- **Backdrop & Material**:
  - Replace opaque background with high-translucency frosted glass layers: `Color(0xCC1E1E22)` in dark mode and `Color(0xCCF7F7FA)` in light mode.
  - Layered ambient and spot drop shadows to give a true floating glass feel.
- **Specular Rim Illumination**:
  - Dual-stop specular edge gradient: Crisp top-lit highlight (`0.75f` opacity down to `0.15f`) simulating light catching the top beveled edge of precision-milled glass.
  - Subsurface glow and inner refraction line to simulate glass thickness and index of refraction.

### 2. Apple Liquid Capsule with Dynamic Specular Highlights
- **Layered Glass Material**:
  - Upper specular sheen: Diagonal linear gradient reflecting ambient light (`Color(0x55FFFFFF)` to `Color(0x08FFFFFF)`).
  - Subtle frosted glass core tint that contrasts against the dock background in both light and dark themes.
  - Crisp perimeter Fresnel border highlight with top-down fade.
- **Floating Depth**:
  - Multi-tier elevation shadow underneath the sliding capsule to make it appear suspended inside the glass dock chamber.

### 3. Viscous Jelly Spring Animation (Squash & Stretch)
- **Fluid Horizontal Offset**:
  - Active index position driven by `animateDpAsState` with jelly-like spring physics (`dampingRatio = 0.62f`, `stiffness = Spring.StiffnessMediumLow`).
- **Dynamic Squash & Stretch**:
  - Track transition delta or animate horizontal expansion (`widthScale` up to `1.18x`) and vertical compression (`heightScale` down to `0.90x`) while in motion, settling cleanly with an organic liquid settle.
- **Synchronized Icon Interaction**:
  - Icons animate with spring scale and gentle bounce when the liquid capsule catches them.

---

## File Changes
- **Target File**: `/app/src/main/java/com/example/ui/components/Components.kt`
  - Refactor `LifeBridgeBottomBar` to implement:
    1. Multi-layer frosted glass dock container with specular rim lighting.
    2. Jelly squash-and-stretch capsule animation states using Compose springs.
    3. Liquid capsule glass gradient shader, specular rim border, and ambient shadow.
    4. Refined icon bounce and tint transitions.

---

## Verification Plan
1. **Compilation Check**:
   - Run `compile_applet` to confirm all Jetpack Compose modifiers, animations, and graphics brushes compile cleanly without errors.
2. **Robolectric Test Suite**:
   - Run `gradle :app:testDebugUnitTest` to verify that existing navigation tests, screen transitions, and bottom bar test tags (`bottom_navigation_bar`, `nav_item_*`) pass with 100% success.
3. **Visual & Interaction Verification**:
   - Confirm touch response on all 5 navigation items.
   - Verify smooth jelly capsule movement and crisp specular rim highlights in both light and dark modes.
