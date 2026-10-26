package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// User Specified Color Palette (Exact tokens from swatch specifications)
val FrenchPorcelain = Color(0xFFF5F4F7) // #f5f4f7 - Primary canvas background
val Hudson = Color(0xFFEBDBD3)         // #ebdbd3 - Warm blush peach
val Penna = Color(0xFFB9C7E0)          // #b9c7e0 - Soft periwinkle blue
val CountryRubble = Color(0xFFD0BEA3)  // #d0bea3 - Warm sandy beige
val FarmersMarket = Color(0xFF8F917C)  // #8f917c - Earthy sage / olive
val Umbra = Color(0xFF1F1F1F)          // #1f1f1f - Deep charcoal black

// Complementary Soft Accents from the Editorial UI Design
val PastelWheat = Color(0xFFEBD368)    // Warm mustard / sunshine yellow
val PastelMint = Color(0xFFBEE2D6)     // Soft foam mint
val PastelCoral = Color(0xFFE3A894)    // Soft coral terracotta
val PureWhite = Color(0xFFFFFFFF)      // Pure card surface / pill container
val SoftIvory = Color(0xFFF8F7FA)      // Soft porcelain surface
val SubtleBorder = Color(0x1F000000)   // 12% black soft border stroke
val CharcoalMuted = Color(0xFF5A5854)  // Secondary text

// Dark Mode Companions (Maintains the warm organic editorial identity in dark mode)
val UmbraDarkBg = Color(0xFF141416)
val UmbraSurface = Color(0xFF1F1F23)
val UmbraElevated = Color(0xFF28282D)
val UmbraBorder = Color(0xFF38383F)

// Legacy compatibility aliases mapped to the new palette
val ElectricBlue = Penna
val ElectricCyan = FarmersMarket
val VibrantTeal = PastelMint
val RoyalViolet = Penna
val RestrainedMint = FarmersMarket
val WarningAmber = PastelWheat
val DangerRose = PastelCoral
val GlassHighlight = Color(0x1FFFFFFF)

val BackgroundDark = UmbraDarkBg
val SurfaceDark = UmbraSurface
val SurfaceElevatedDark = UmbraElevated
val SurfaceCardDark = UmbraSurface
val SurfaceCardBorder = UmbraBorder
val TextPrimaryDark = FrenchPorcelain
val TextSecondaryDark = CountryRubble
val TextTertiaryDark = FarmersMarket

val BackgroundLight = FrenchPorcelain
val SurfaceLight = PureWhite
val SurfaceElevatedLight = SoftIvory
val SurfaceCardLight = PureWhite
val SurfaceCardBorderLight = SubtleBorder
val TextPrimaryLight = Umbra
val TextSecondaryLight = CharcoalMuted
val TextTertiaryLight = FarmersMarket
