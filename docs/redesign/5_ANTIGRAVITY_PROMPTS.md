# ANTIGRAVITY PROMPTS — হিসাব কিতাব v7 redesign

## Setup (do once)
1. Create folder `docs/redesign/` in the project and copy these files in:
   `1_REDESIGN_BRIEF.md`, `2_DESIGN_SYSTEM_V7.md`, `3_UX_FLOWS.md`, `4_COMPONENT_SPEC.md`,
   `5_ANTIGRAVITY_PROMPTS.md`, `6_SCREENSHOT_FINDINGS_AND_PATCH.md`
   (also keep `APP_AUDIT.md` there).
2. Paste prompts below ONE AT A TIME. Wait for the agent's summary, review the app on emulator/device, then send the next prompt.
3. If a phase result looks wrong, reply with what is wrong (a screenshot helps) instead of moving on.

---

## PHASE 0 — Setup & baseline
```
Read docs/redesign/1_REDESIGN_BRIEF.md, 2_DESIGN_SYSTEM_V7.md, 3_UX_FLOWS.md,
4_COMPONENT_SPEC.md, APP_AUDIT.md, and 6_SCREENSHOT_FINDINGS_AND_PATCH.md fully.
(Note: 6_SCREENSHOT_FINDINGS_AND_PATCH.md overrides files 1-5 on conflicts).
They define a UI/UX redesign (v7) of this app.

Tasks (no UI changes yet):
1. Create and switch to git branch `redesign/v7`. Do NOT push to main during this redesign.
2. Append a short "Redesign v7" section to AGENTS.md (and GEMINI.md) that says: the documents in
   docs/redesign/ are the source of truth (file 6 overrides files 1-5 on conflicts); work on branch
   redesign/v7; one phase at a time; stop for approval after each phase; auto-push-to-main rule is paused
   until the owner approves the merge.
3. Run ./gradlew testDebugUnitTest and report the baseline result.
4. List the exact existing files you will touch or delete per phase (a short plan, max 40 lines).
Stop and wait for my "next phase".
```

## PHASE 1 — Foundations (tokens, fonts, base components)
```
Phase 1 of docs/redesign. Implement DESIGN_SYSTEM_V7.md foundations only (along with C1 and C2 from file 6):
1. Extend AppColors/LocalAppColors with the new tokens (surfaceRaised, *Container, scrim, onAccent) for
   Light, Dark and AMOLED. Keep all existing token values. Implement C1: flat surfaces, new radii (cards 16dp,
   chips 12dp, sheets 24dp top, pills full), no outlines on cards in Dark/AMOLED.
2. Add bundled fonts (Inter + Hind Siliguri, offline, in res/font) and update Type.kt with the new scale.
   Keep tnum on amounts and Bengali >=12sp / line-height >=16sp.
3. Update Shape.kt (radius tokens), add Spacing.kt (4-pt grid), Motion.kt (durations/easing + LocalMotion
   honoring system reduce-animations).
4. Build base components from COMPONENT_SPEC.md: AppCard, AppButton, AppChip, SegmentedControl,
   AmountText, SectionHeader, StatusBanner, EmptyState, SkeletonBox, AppBottomSheet, AppSnackbarHost.
   Implement C2: opaque bottom bar base, content bottom padding support.
5. Add a debug-only "Component Gallery" screen (or @Preview file) showing every component in
   light/dark/AMOLED and English/Bengali.
Do not redesign any real screen yet. Run tests, summarize, stop.
```

## PHASE 2 — Navigation
```
Phase 2. Implement navigation per 3_UX_FLOWS.md section 1 and C2 from file 6:
- New AppBottomBar: Home, Transactions, center +, Market, Money. Labels always visible, bn/en. Opaque surface,
  0.5dp top hairline, content bottom padding so nothing hides behind bar. Center + is solid emerald (remove blue gradient).
- Remove 3D scaffold and side drawer (Navigation3DScaffold.kt, SideMenuDrawer.kt) and the unused navigation3 dependency.
- Add MoneyHubScreen listing Accounts, Budgets & Goals, Bills, Money Owed, Insights, Reports with live summary values.
- Drill-down screens hide the bottom bar and show back arrow + large title. Back goes to parent (Money hub).
- Settings opens from the Home header avatar (and a gear on Money hub).
- + opens the existing Add sheet with ONE tap (no long-press requirement). Keep GlobalActionChooserSheet only if still referenced; otherwise remove it.
- Shared-axis transition between tabs; predictive back support.
Update tests that assert the removed drawer/scaffold structure, and list each updated test with the reason.
Run tests, summarize, stop.
```

## PHASE 3 — Add/Edit transaction + AmountKeypad
```
Phase 3. Implement 3_UX_FLOWS.md section 3, C4 from file 6, and the AmountKeypad/CategoryPicker/AccountChipRow components:
- Single full-height sheet: Expense | Income | Transfer segmented control (remember last used), big amount (area <=140dp),
  inline category chips (expandable, no nested modal, smart default category), account chips with balances, date shortcuts,
  merged Title with "+ Add note" expansion, small receipt row, pinned Save button showing the amount.
- AmountKeypad (custom, >=56dp keys, + - × ÷, backspace, decimal, expression preview) replaces the system
  keyboard for the amount. Reuse CalculatorEvaluator. Use the same AmountKeypad in the Market price entry
  dialog so both modules behave the same.
- Note field uses the system keyboard with imePadding; Save is always visible on screens < 720dp.
- Keep all existing business logic: balance validation warning, transfer paired entries, receipt compression.
- Remove Market type from this sheet (market expenses come from Market checkout).
Run tests, add unit tests for AmountKeypad input/evaluation, summarize, stop.
```

## PHASE 4 — Home
```
Phase 4. Redesign DashboardScreen per 3_UX_FLOWS.md section 2 and C3 from file 6:
- Header with greeting, avatar (opens Settings), privacy toggle, notifications. Pinned header with background color and hairline on scroll.
- HeroBalanceCard (target <=200dp, left-aligned, Total balance default, spending wallet secondary line), safe-to-spend row,
  4 quick actions (uniform accentContainer tint), conditional upcoming-bills banner, 3 recent transactions above fold.
- Below-the-fold widgets (market list, debts, savings, 7-day spend) as collapsed summary rows that remember state;
  hide sections that have no data; show a "Get started" checklist for new users.
- Do not change repository APIs. If DashboardViewModel has 20+ flows, group them into section state objects
  but keep behavior identical.
Run tests, summarize, stop.
```

## PHASE 5 — Transactions & Market
```
Phase 5.
Transactions (per 3_UX_FLOWS.md section 4 and C5): large-title collapsing header, expandable search, single Filters button -> FilterSheet with
active-filter chips, day-grouped sticky headers with day net total, new TransactionRow, tap -> detail sheet,
swipe left delete (with Undo) and swipe right edit (tap alternatives must exist), export moved to overflow -> Reports.
Remove profile icon from Transactions header.
Market (per 3_UX_FLOWS.md section 5 and C6): polish lists/prices/new list. Keep MarketItemRow's SwipeToDismissBox and every existing behavior
(WhatsApp parser, price variance alert, staples prompt, atomic checkout). Segmented pills min height 48dp,
aisle sections with progress, in-store dock restyle above bottom bar. Prices tab with compact segmented filter, deduplication normalization
for commodity names, neutral item avatars instead of department icons.
Run tests, summarize, stop.
```

## PHASE 6 — Money screens
```
Phase 6. Restyle Accounts, Budgets & Savings Goals, Bills, Money Owed per 3_UX_FLOWS.md section 7,
using only the shared components. Add empty-state templates (bills, savings, "suggest budget from last month").
Keep the digital card look but refine it with the new tokens (C1). Budget status must use text + color, not color alone.
Do not change data logic. Run tests, summarize, stop.
```

## PHASE 7 — Insights & Reports
```
Phase 7. Redesign Analytics (Insights) and Reports: new DonutChart (center total, tap-to-highlight, legend),
BarChart (tap tooltip, today highlight), Sparkline, period chips, KPI strip; Reports with statement preview card
and two clear Export buttons. Add accessibility summaries for charts. Animate charts once on entry and respect
reduce-motion. Run tests, summarize, stop.
```

## PHASE 8 — Settings, Sync status, onboarding
```
Phase 8. Settings grouped per 3_UX_FLOWS.md section 7 and C7. Replace all Toast usage for Drive backup/restore with
SyncStatusCard + actionable snackbars (retry, details). Add the 3-screen skippable onboarding for first launch
and the Home "Get started" checklist wiring. Ensure every new string exists in English and Bengali in AppStrings.
Run tests, summarize, stop.
```

## PHASE 9 — Accessibility, performance, QA, release
```
Phase 9 (final).
1. Accessibility sweep: all touch targets >=48dp (fix MarketSegmentedPills, AccountCardMenu, FilterChips),
   contentDescription on every meaningful icon (SideMenu removed; check OneUiComponents, charts), font scale 200%,
   contrast in all 4 themes.
2. Check checklist: no content hidden behind bottom bar, no ellipsized key labels, max 2 nesting levels.
3. Check every screen with Bengali numerals ON and long Bengali strings; fix clipping.
4. Performance: no nested modals, avoid unnecessary recomposition on Home/Transactions (stable keys, derivedStateOf),
   verify smooth scrolling.
5. Remove dead code from the old UI. Run ./gradlew testDebugUnitTest and lint.
6. Produce a CHANGELOG entry + before/after summary. Then run ./scripts/bump_version.sh major "UI/UX redesign v7"
   keeping the 3-point version sync. Do NOT merge or push to main; wait for my approval.
```

## OPTIONAL PHASE T — Transfer integrity (recommended)
```
Optional phase. Audit section 8.3: transfers are two unlinked transactions. Add DB migration v16 adding
transfer_pair_id to transactions, link both legs when creating a transfer, and make edit/delete of one leg
update/delete the other atomically (with confirmation text explaining both legs). Backfill existing transfers
best-effort by matching notes/category "Transfer", amount, timestamp and opposite accounts. Update backup JSON
(backward compatible with older backups) and add migration + backup/restore tests. Run tests, summarize, stop.
```
