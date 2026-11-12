# DESIGN SYSTEM V7 — "Refined Ledger"

Evolves the existing "Living Ledger" tokens. Same warm identity, more polish, more depth,
a real type system and consistent spacing. All colors via `LocalAppColors`.

## 1. Color tokens
Keep existing: background, surface, surfaceVariant, border, borderDashed, textPrimary/Secondary/Muted,
accent, income, expense, warning, info (values unchanged from the audit, section 6.2).

ADD these tokens to `AppColors` (tune in previews, keep WCAG AA 4.5:1 for text on them):

| Token | Light | Dark | AMOLED | Use |
|---|---|---|---|---|
| surfaceRaised | #FFFFFF | #1B212B | #0F131B | Cards that sit on `surface` (sheets, hero) |
| accentContainer | #E3F4EC | #12302A | #0E2A24 | Income chip bg, selected chip bg, positive banners |
| expenseContainer | #FCE8EC | #3A1820 | #301219 | Expense chip bg, overspend banners |
| warningContainer | #FBF0DC | #3A2D12 | #302410 | Due-soon, 75-99% budget |
| infoContainer | #E9E9FB | #1F2147 | #181A3A | Transfers, analytics highlights |
| scrim | #000000 @ 40% | #000000 @ 60% | #000000 @ 70% | Sheet/dialog scrim |
| onAccent | #FFFFFF | #04130D | #04130D | Text/icons on accent buttons |

**Category palette (for NEW categories / picker; existing stored colors untouched):**
#10B981 #14B8A6 #0EA5E9 #6366F1 #8B5CF6 #EC4899 #F43F5E #F97316 #F59E0B #84CC16 #78716C #64748B

Rules: never use color alone to convey meaning (pair with icon/sign/label). Income = +, expense = −.

## 2. Typography
Bundle fonts locally (offline-first; NO downloadable fonts):
- Latin / digits: **Inter** (400, 500, 600, 700) with `tnum`.
- Bengali: **Hind Siliguri** (400, 500, 600, 700) — or Noto Sans Bengali if glyph quality is better in preview.
- Compose `FontFamily` with fallback chain; verify Bengali numerals (১২৩) align in columns, otherwise use the font that aligns best.

| Token | Size / Line | Weight | Use |
|---|---|---|---|
| displayLarge | 40 / 46 | Bold | Hero balance (one per screen max) |
| displayMedium | 32 / 38 | Bold | Big KPI on hub screens |
| headlineMedium | 24 / 30 | SemiBold | Screen large-title, key metrics |
| titleLarge | 20 / 26 | SemiBold | Sheet titles, section hero |
| titleMedium | 16 / 22 | SemiBold | Card titles, list primary text |
| titleSmall | 14 / 20 | Medium | Row titles |
| bodyLarge | 16 / 24 | Regular | Inputs, notes |
| bodyMedium | 14 / 20 | Regular | Secondary text |
| bodySmall | 12 / 17 | Regular | Meta, timestamps (min for Bengali) |
| labelLarge | 14 / 20 | SemiBold | Buttons |
| labelMedium | 12 / 16 | SemiBold | Chips, badges |

Amount text style: `tnum`, weight 600+, never truncated with ellipsis — shrink-to-fit
(`autoSize` / scale down to 0.7) before truncating.

## 3. Spacing (4-pt grid)
`space2=2, space4=4, space8=8, space12=12, space16=16, space20=20, space24=24, space32=32, space48=48`
- Screen horizontal padding: **20dp** (phones), 24dp+ on width >= 600dp.
- Card padding: 16dp. Gap between cards: 12dp. Section gap: 24dp.
- List row height: min 64dp (two-line), 56dp (one-line). Touch target min 48x48dp.

## 4. Shape & elevation
| Token | Value | Use |
|---|---|---|
| radiusXS | 6dp | Badges |
| radiusS | 12dp | Chips, inputs, keypad keys |
| radiusM | 16dp | Cards |
| radiusL | 24dp | Hero cards, buttons (pill = full) |
| radiusSheet | 28dp (top corners) | Bottom sheets |

Elevation is **tonal first, shadow second**: cards use `surface` + 0.5dp hairline border;
hero card and floating elements use soft shadow (y=4, blur=16, color = black @ 8% light / 40% dark).
No heavy nested cards: max 2 levels of containment.

## 5. Iconography
Material Icons Extended (rounded variants where available), 24dp default, 20dp in chips.
Category icons in a 40dp circle with 12% tint of category color. One icon style only.

## 6. Motion
| Token | Duration | Easing | Use |
|---|---|---|---|
| fast | 120ms | FastOutSlowIn | Press, toggle, chip select |
| standard | 220ms | EaseInOutCubic | Content change, tab switch |
| emphasized | 340ms | Emphasized decelerate | Sheets, hero number count-up |

- Number changes animate (count-up/roll) once on screen entry, not on every recomposition.
- Shared-axis (X) transition between bottom tabs; sheets slide up; list items use `animateItem`.
- Respect system "remove animations" (duration 0) via a single `LocalMotion` wrapper.
- Haptics: keep `AppHaptics`; use on save, check-off, keypad confirm only.
- REMOVE the 3D scaffold rotation/scale drawer.

## 7. Data-viz
- Donut: center shows total + label; tap segment = highlight + legend row; animate sweep 340ms once.
- Bar chart (7/30 days): tap bar shows value tooltip; today highlighted with accent; others `surfaceVariant`.
- Sparklines: 1.5dp stroke, no axes, last point dot.
- Always provide a text/table alternative (contentDescription summary) for screen readers.

## 8. States (mandatory for every screen)
Loading = skeleton shimmer (not spinner) · Empty = illustration/icon + 1 sentence + 1 primary action + optional templates ·
Error = inline banner with Retry · Offline is normal (app is offline-first) — never show "no internet" for local features.

## 9. Accessibility
- Contrast >= 4.5:1 text, 3:1 UI components, in ALL 4 themes.
- Touch target >= 48dp (use `minimumInteractiveComponentSize()` or explicit size).
- contentDescription on every meaningful icon; `Role` + `stateDescription` on toggles/chips; amounts read as "1,250 taka expense".
- Support font scale up to 200% without clipping (use `heightIn(min=)`, not fixed heights).
- Focus order logical; TalkBack announces sheet titles.
