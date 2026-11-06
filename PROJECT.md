# Project: ExpenseTracker Market & UI/UX Handcrafted Modernization

## Architecture
- **Layer 1: Design System & Tokens (`theme/`)**:
  - "The Living Ledger & Tactile Bazaar" warm palette (charcoal slate `#0E1116`, linen white `#FFFFFF`, bone parchment `#F9F8F5`, cardamom emerald `#10B981`/`#047857`, terracotta rose `#F43F5E`/`#BE123C`).
  - Tabular typography scale with global `fontFeatureSettings = "tnum"`, minimum 12sp constraint for Bengali metadata, and explicit 400/500/600/700 weights.
  - Sheet & Hairline Rule styling (`0.5.dp` ink stroke) replacing nested card soup.
- **Layer 2: Core Market & In-Store Shopping (`ui/market/`, `ui/dashboard/`)**:
  - Tactile Bazaar Pocket Notepad (`MarketItemRow.kt`): 2-line clean hierarchy, animated pencil strikethrough, ink-stamped badges.
  - Department-Aisle Stall Organization (`MarketDepartmentSectionHeader.kt`): Collapsible aisle headers grouping by 🥬 কাঁচাবাজার, 🐟 মাছ ও মাংস, 🥛 দুধ ও বেকারি, 🧂 মুদি সদাই with live progress counters.
  - Persistent In-Store Shopping Dock (`MarketBottomTripSummaryBar.kt`): Pinned thumb-zone bar with running total, progress indicator, and 1-tap checkout.
- **Layer 3: Creation, Logging & Completion (`ui/dashboard/`)**:
  - Modern List Creation Wizard (`CreateMarketListSection.kt`): Interactive tag chips (`MarketParsedTagChip.kt`), WhatsApp bulk paste import, 1-tap staple recommendations without AI tropes.
  - Thumb-Zone Price Logging (`MarketPromptAmountDialog.kt`): Ergonomic bottom keypad, 1-tap rate variance badge, and calculator operator chips.
  - Perforated Digital Tally Slip (`MarketTripCompletionSheet.kt`): Monospace items breakdown, estimated savings, payment account selector, and ink stamp.
- **Layer 4: Price Intelligence & Inflation (`ui/dashboard/ProductPriceInsightsSheet.kt`)**:
  - Commodity price trends, historical rate sparklines, and fast department filter chips.
- **Layer 5: Business Logic & Persistence (`viewmodel/`, `data/repository/`, `data/local/`)**:
  - `DashboardViewModel` StateFlows: `activeMarketList`, `activeMarketLists`, `lastPurchasedRates`, `productPriceInsights`, `stapleProducts`.
  - SQLite Database Version 15 with WAL, foreign keys, and atomic transactions in `ExpenseRepositoryMarketCompletion.kt`.

---

## Feature Inventory
| # | Feature | Description | Milestone | Source |
|---|---------|-------------|:---------:|:------:|
| 1 | Living Ledger Warm Color Palette | Charcoal slate, bone parchment, cardamom emerald, terracotta rose meeting WCAG AA | M1 | Survey (explorer_survey_6_2) |
| 2 | Tabular Bilingual Typography Scale | Global `tnum`, explicit 400/500/600/700 weights, >=12sp Bengali metadata | M1 | Survey (explorer_survey_6_2) |
| 3 | Sheet & Hairline Rule Styling | Soft 0.5dp ink dividers and unified container surfaces replacing nested card soup | M1 | Survey (explorer_survey_6_2) |
| 4 | Tactile Bazaar Pocket Notepad Item Row | 2-line clean item layout, pencil strikethrough animation, tactile ink badge, swipe-to-dismiss | M2 | Survey (explorer_survey_6_1) |
| 5 | Department-Aisle Stall Organization | Collapsible stall headers (🥬 কাঁচাবাজার, 🐟 মাছ ও মাংস, 🥛 দুধ ও বেকারি, 🧂 মুদি সদাই) with live progress | M2 | Survey (explorer_survey_6_1) |
| 6 | Persistent In-Store Shopping Dock | Elevated thumb-zone bar showing running total, items progress ("৪/৫টি সম্পন্ন"), and 1-tap checkout | M2 | Survey (explorer_survey_6_1) |
| 7 | Handcrafted List Creation Wizard | Physical notepad list creation, WhatsApp bulk paste with interactive tags, 1-tap staple pills | M3 | Survey (explorer_survey_6_1) |
| 8 | Thumb-Zone Price Logging Keypad | Ergonomic bottom keypad, 1-tap previous rate chip, rate variance alert, calculator operator bar | M3 | Survey (explorer_survey_6_1) |
| 9 | Perforated Digital Tally Slip | Thermal receipt styling, monospace items ledger, savings calculation, celebratory ink stamp | M3 | Survey (explorer_survey_6_1) |
| 10 | Commodity Price Intelligence & Trends | Stock ticker cards, historical purchase rate sparklines, department inflation KPI ribbons | M4 | Survey (explorer_survey_6_1) |
| 11 | Structural & Ergonomic Invariant Preservation | Retain all test string matches (`SwipeToDismissBox`, `isEditMode`, `Icons.Filled.Check`), >=48dp touch targets, no AI sparkles | M2, M3, M4 | Survey (explorer_survey_6_3) |
| 12 | StateFlow & Transaction Preservation | 100% preservation of `DashboardViewModel` StateFlows and atomic SQLite transaction boundaries | M2, M3, M4 | Survey (explorer_survey_6_3) |
| 13 | Comprehensive Regression Suite | Unit tests verifying all redesigned components, layout invariants, and zero regressions | M5 | Survey (explorer_survey_6_3) |
| 14 | GEMINI.md Release Compliance | Minor version bump to `v6.18`, git tag, and origin main push | M5 | GEMINI.md |
| 15 | Natural Finger Gestures (1-Swipe Check-off) | 1-Swipe right (`StartToEnd`) check-off with emerald reveal, spring snap-back, and threshold haptics in `MarketItemRow.kt` | MG1 | Survey (explorer_gesture_1) |
| 16 | Horizontal Pager Tab Navigation | Fluid horizontal swipe navigation between Market tabs in `MarketScreen.kt` synchronized with segmented pills | MG1 | Survey (explorer_gesture_1) |
| 17 | Handcrafted Tactile Notebook Details | Ruled notebook baseline, tactile tally dots in `MarketBottomTripSummaryBar`, and serrated receipt tear edge in `MarketTripCompletionSheet` | MG1 | Survey (explorer_market_ux_2) |
| 18 | Gesture Verification & Production Release | Automated unit tests for gestures, 430+ regression pass, version bump to `v6.19`, git tag and origin main push | MG2 | GEMINI.md |

---

## Milestones
| # | Name | Scope | Dependencies | Status |
|---|------|-------|:------------:|:------:|
| M1 | Living Ledger Design System Tokens | Implement warm living color palette, extended tabular typography, and hairline rule tokens in `Color.kt`, `Theme.kt`, `Type.kt` | none | **DONE** (Gate Passed, 338 tests pass) |
| M2 | Handcrafted Market Checklist & In-Store Dock | Redesign `MarketItemRow.kt`, `MarketScreen.kt`, implement `MarketDepartmentSectionHeader.kt` and `MarketBottomTripSummaryBar.kt` | M1 | **DONE** (Gate Passed, 356 tests pass) |
| M3 | List Creation, Price Logging & Tally Slip | Overhaul `CreateMarketListSection.kt`, `MarketPromptAmountDialog.kt`, `MarketTripCompletionSheet.kt`, `MarketParsedTagChip.kt` | M1, M2 | **DONE** (Gate Passed, 361 tests pass) |
| M4 | Price Intelligence & Inflation Trends | Overhaul `ProductPriceInsightsSheet.kt` with living ledger styling and historical sparklines | M1 | **DONE** (Gate Passed, 416 tests pass) |
| M5 | Regression Verification & Release v6.18 | Run full test suite, execute `./scripts/bump_version.sh minor`, push to GitHub | M1..M4 | **DONE** (Gate Passed, v6.18 live on main) |
| MG1 | Market Natural Finger Gestures & Tactile Polish | 1-Swipe check-off, `HorizontalPager` tab gestures, threshold haptics, ruled baseline, tally dots, serrated tear edge | M1..M5 | **DONE** (Gate Passed, 461 tests pass) |
| MG2 | Production Verification & Release v6.19 | Run 461 unit tests, bump version to `v6.19`, tag and push to GitHub | MG1 | **DONE** (Gate Passed, v6.19 live on main) |

---

## Interface Contracts

### M1 ↔ M2, M3, M4 (Design System) [ACTIVE]
- `LocalAppColors.current`:
  - `cardBackground`: Surface sheet color (`#161B22` Dark / `#FFFFFF` Light)
  - `background`: Canvas background (`#0E1116` Dark / `#F9F8F5` Light)
  - `textPrimary`: Warm high-contrast text (`#F3F4F6` Dark / `#1C1917` Light)
  - `textSecondary`: Muted metadata text (`#9CA3AF` Dark / `#57534E` Light)
  - `divider`: Hairline ink separator (`#262E3B` Dark / `#E5DFD5` Light)
  - `accent`: Primary action emerald (`#10B981` Dark / `#047857` Light)
- `LivingTypography` / `MaterialTheme.typography`:
  - `fontFeatureSettings = "tnum"` globally enabled on tabular numbers.
  - Minimum font size for Bengali text $\ge 12\text{sp}$.

### M2 ↔ M3, M5 (Market UI Contracts)
- `MarketItemRow`:
  - Required parameters: `item: MarketItem`, `onTogglePurchased: (MarketItem) -> Unit`, `onPromptAmount: (MarketItem) -> Unit`, `onRequestDelete: () -> Unit`, `onSubstituteClick: (MarketItem) -> Unit`, `isEditMode: Boolean`.
  - Invariants: Uses `SwipeToDismissBox`, circular checkbox, `getDepartmentMeta`, `formatQuantity()`.
- `MarketDepartmentSectionHeader`:
  - Department title (Bangla & English), icon emoji, completed count, total count, collapsible state toggle.
- `MarketBottomTripSummaryBar`:
  - Pinned dock displaying spent total (`formatCurrency`), item progress counter, and single-tap `onFinishShopping` trigger.
- `MarketFinishButton`:
  - Invariant: Uses `Icons.Filled.Check`, no duplicate checkmark in text string.

### M3 ↔ M2, M5 (Creation & Completion Contracts)
- `CreateMarketListSection`:
  - Invariants: `CreateMarketListStep.TITLE`, `CreateMarketListStep.ITEMS`, `parseBulkText`, `addManualItem`, `quickStaples`.
- `MarketPromptAmountDialog`:
  - Invariants: `FocusRequester`, `CalculatorEvaluator.evaluate`, operator chips (`+`, `-`, `×`, `÷`), quick increment chips (`50, 100, 200, 500`), `lastRate`.
- `MarketTripCompletionSheet`:
  - Accepts `MarketCompletionResult`, lists completed items, allows account selection (`Cash`, `Bank`, `MFS`), calls `onConfirm`.

---

## Code Layout
- `app/src/main/java/com/example/expensetracker/theme/`:
  - `Color.kt` — Living Ledger palette
  - `Theme.kt` — AppTheme and LocalAppColors provider
  - `Type.kt` — LivingTypography with tabular numbers
- `app/src/main/java/com/example/expensetracker/ui/market/`:
  - `MarketScreen.kt` — Main Market screen & coordinator
  - `MarketDepartmentSectionHeader.kt` — Collapsible department stall header
  - `MarketBottomTripSummaryBar.kt` — Thumb-zone persistent shopping dock
  - `MarketParsedTagChip.kt` — Interactive WhatsApp/bulk tag chips
- `app/src/main/java/com/example/expensetracker/ui/dashboard/`:
  - `MarketItemRow.kt` — Tactile bazaar pocket notepad row
  - `CreateMarketListSection.kt` — Handcrafted list creator wizard
  - `MarketPromptAmountDialog.kt` — Thumb-friendly price entry bottom sheet
  - `MarketTripCompletionSheet.kt` — Perforated digital tally slip
  - `ProductPriceInsightsSheet.kt` — Commodity price intelligence
  - `MarketFinishButton.kt` — In-store finish button
- `app/src/test/java/com/example/expensetracker/`:
  - `MultiMarketListAndItemUxTest.kt` — Structural invariant tests
  - `MarketOverhaulE2ETest.kt` — 4-tier opaque-box E2E suite (73 tests)
  - `ChallengerMilestone1StressTest.kt` — Theme & contrast stress tests
  - `ChallengerM1EmpiricalStressTest.kt` — Component & shape stress tests
