# COMPONENT SPEC — v7
Location: `ui/components/`. Reuse/upgrade existing where noted. Every component must have:
`@Preview` (light/dark/AMOLED, en/bn), 48dp targets, contentDescription/semantics, uses `LocalAppColors`,
`LocalAppStrings`, and design tokens from DESIGN_SYSTEM_V7.md (no magic numbers).

| Component | Replaces / extends | Spec |
|---|---|---|
| `AppScaffold` | NavigationScreenHost wrappers | Handles top bar (large-title collapsing), snackbar host, bottom bar visibility, insets, predictive back |
| `AppTopBar` | OneUiHeader, per-screen TopBars | Variants: Home (greeting+avatar+actions), LargeTitle (collapsing), Sheet title |
| `AppBottomBar` | ModernBottomNavBar | 5 slots, center + is 56dp circular accent button with 6dp shadow; selected = accentContainer pill + filled icon + label; badge support |
| `AppCard` | AppCard/OneUiCard | Variants: Flat (border), Raised (shadow), Tinted (container color). One padding API |
| `HeroBalanceCard` | OneUiBalanceCard | displayLarge amount, count-up once, income/expense chips, budget progress, privacy mask aware |
| `AmountText` | scattered Text | tnum, sign + color by type, auto-shrink, privacy mask, Bengali numerals, a11y label |
| `MetricChip` | KPI bits | Icon + label + amount; tinted container |
| `SectionHeader` | ad-hoc headers | Title + optional action ("See all"), 48dp action target, collapsible variant |
| `TransactionRow` | existing rows | 64dp, category circle, 2-line text, right amount; tap = detail; trailing swipe actions |
| `AppButton` | mixed buttons | Primary (accent fill), Secondary (tonal), Tertiary (text), Destructive; height 48/56dp, radius pill, loading state, icon slot |
| `AppChip` | FilterChips | Filter/Choice/Input variants, 48dp tall hit area (visual 36dp ok), selected = accentContainer + check |
| `SegmentedControl` | type switcher, market pills | 48dp high, animated indicator, 2-4 segments, equal width |
| `AmountKeypad` | InPlaceCalculator + MarketPromptAmountDialog keypad | 4x4 grid keys >= 56dp, operators + − × ÷, ⌫, "." , Bengali digit display option, haptic on key, `onSave` slot; expression preview line; evaluates with CalculatorEvaluator |
| `CategoryPicker` | category dropdown sheet | Inline horizontal chip grid + "More" expands inline; no nested modal; supports add-new inline |
| `AccountChipRow` | account dropdown | Horizontal chips: icon + name + balance (masked in privacy mode) |
| `AppBottomSheet` | all sheets | radiusSheet 28dp, drag handle, max 90% height, pinned footer slot for primary action, imePadding, scrim token |
| `FilterSheet` | TransactionsFilterBar dropdowns | Single sheet: type, accounts, categories, date range; "Apply (N)" + "Reset" |
| `ProgressBarThin` / `ProgressRing` | existing bars | Animated, threshold colors + text status |
| `EmptyState` | EmptyStateCard | Icon/illustration, title, body, primary action, optional `templates: List<Template>` chips |
| `StatusBanner` | Upcoming bills banner, warnings | Variants info/warning/error/success; icon + text + optional action |
| `SyncStatusCard` | Toast feedback | Last sync time, state, Backup now / Restore, error detail expandable |
| `SkeletonBox` / `ShimmerList` | none | Loading placeholders for lists/cards |
| `AppSnackbarHost` | Snackbar usage | Action support (Undo/Retry), bn/en, a11y live region |
| `DonutChart` | AnalyticsCharts | Center total, tap highlight, animated sweep, legend rows, a11y summary |
| `BarChart` | SpendingBarChart | Tap tooltip, today highlight, animated grow, a11y summary |
| `Sparkline` | Market sparklines | Light stroke, end dot |
| `PrivacyToggle` | Settings toggle | Eye icon in Home header; persists in DataStore (existing flag) |

## Screen -> files mapping (primary edits)
- Navigation: `ui/navigation/*` (AppNavigation, NavigationScreenHost; remove Navigation3DScaffold, SideMenuDrawer; add MoneyHubScreen)
- Home: `ui/dashboard/*` — split DashboardViewModel streams by section where it reduces coupling; DON'T change repository contracts.
- Add/Edit: `ui/transactions/AddEditTransactionSheet.kt`, `TransactionAmountInputField.kt` (to AmountKeypad)
- Transactions: `TransactionsScreen.kt`, `TransactionsFilterBar.kt` -> FilterSheet
- Market: `ui/market/*`, `MarketPromptAmountDialog.kt` -> AmountKeypad
- Money screens: `ui/accounts`, `budget`, `savings`, `bills`, `receivables`, `analytics`, `reports`, `settings`

## Definition of done (per component/screen)
1. Matches tokens, no hardcoded colors/sizes/strings. 2. Previews for 4 themes x 2 languages.
3. Large font (200%) and Bengali numerals checked. 4. Semantics added. 5. Tests green.
