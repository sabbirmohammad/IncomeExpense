# REDESIGN BRIEF — হিসাব কিতাব v7 ("Refined Ledger")

> Read this file first, then DESIGN_SYSTEM_V7.md, UX_FLOWS.md, COMPONENT_SPEC.md.
> Source of truth for the current state: APP_AUDIT.md.

## 1. Goal
Turn the app from "feature-rich but cluttered" into a calm, fast, premium-feeling
personal finance app that a first-time user understands in 10 seconds and a daily
user can operate in 2 taps. Keep everything that already works (data layer, features,
offline-first, bilingual, MFS, bazaar checklist). This is a UI/UX redesign, NOT a rewrite.

## 2. What we are fixing (from the audit)
| # | Problem (audit ref) | Target outcome |
|---|---|---|
| 1 | Core modules hidden in 3D drawer (7.1) | Max 2 taps to any primary destination; no drawer |
| 2 | Back-arrow + bottom bar conflict (7.1) | One clear hierarchy: tabs = top level, back = drill-down only |
| 3 | Hidden long-press on + (7.1) | No hidden gestures for primary actions |
| 4 | Add-transaction: tiny type pill, nested sheets, keyboard covers fields (7.2) | One sheet, inline pickers, custom keypad, save button always visible |
| 5 | Inconsistent numeric input vs Market (7.2) | One shared AmountKeypad everywhere |
| 6 | Dashboard overload, 8 stacked widgets (7.3) | Answer "how am I doing today?" above the fold; rest is progressive |
| 7 | Generic empty states (7.3) | Starter templates + one-tap setup |
| 8 | Toasts for Drive sync (7.3) | Persistent sync status row + actionable snackbars |
| 9 | Touch targets < 48dp, missing contentDescription (7.4) | WCAG 2.2 AA across the app |

## 3. Design principles
1. **Numbers are the hero.** Money amounts get the most size/contrast; chrome stays quiet.
2. **One primary action per screen.** Everything else is secondary or tertiary.
3. **Progressive disclosure.** Summary first, detail on tap.
4. **No hidden gestures** for essential actions (swipe/long-press are shortcuts only).
5. **Bengali-first quality.** Every layout must look right in Bengali with Bengali numerals (longer strings, taller glyphs).
6. **Privacy feels visible.** Privacy mode toggle reachable from the home header in 1 tap.
7. **Fast & calm.** Motion is short, purposeful, and respects "reduce animations".

## 4. Keep (do not change behaviour)
- SQLite schema/repository/StateFlow architecture, WorkManager, Drive backup logic.
- All features and business logic (market checkout, bill pay, debt installments, bill splitter, reconcile).
- AppStrings localization system (`LocalAppStrings`) — every new string goes through it in BOTH English and Bengali.
- Color access via `LocalAppColors.current` (never raw `MaterialTheme.colorScheme` for brand colors).
- Bengali numerals toggle, privacy mode, 4 theme modes (Dark, Light, AMOLED, System).
- Palette DNA: Cardamom Emerald / Terracotta Rose / Turmeric / Iris + Parchment/Slate surfaces.

## 5. Non-goals (do NOT do in this redesign)
- No new backend, no analytics SDK, no network calls, no AI/OCR (OfflineReceiptAndAiPurgeVerificationTest must stay green).
- No multi-currency, recurring income, biometric lock (future roadmap).
- No migration to Navigation 3 unless trivial; a clean enum/state-driven nav is fine. Removing the unused nav3 dependency is allowed.

## 6. Hard invariants (tests/CI)
- `./gradlew testDebugUnitTest` stays green (471 tests). If a structural test asserts something we intentionally remove (e.g. 3D drawer), update that test in the same commit and list it in the commit message. Never delete a test just to pass.
- MarketItemRow keeps `SwipeToDismissBox`.
- Touch targets >= 48dp (ChallengerPhase2Iteration2TouchTargetTest must stay green).
- Bengali text >= 12sp, line-height >= 16sp.
- Tabular numerals (`tnum`) on all amounts.

## 7. Success criteria
- Add an expense in <= 4 interactions from Home (tap +, type amount, pick category, Save).
- Any of Budgets / Accounts / Bills / Debts / Reports reachable in <= 2 taps.
- Home first screen (no scroll) shows: balance, month income/expense, safe-to-spend, quick actions, 3 recent transactions.
- Zero touch targets < 48dp; every icon has contentDescription or is explicitly decorative.
- No jank when opening Add sheet on a mid-range device (no nested modals).

## 8. Workflow rules for the agent
- Work on branch `redesign/v7`. Do NOT push to `main` until the owner approves (this overrides the auto-push rule in AGENTS.md for this redesign only).
- One phase = one PR-sized set of commits. After each phase: run tests, summarize what changed, list files touched, list any test updated and why, then STOP and wait for "next phase".
- Final step only: `./scripts/bump_version.sh major "UI/UX redesign v7"` (v6.x -> v7.0), keeping the 3-point version sync.
- If the emulator/screenshots are unavailable, create Compose `@Preview` functions (light, dark, AMOLED, English, Bengali) for each new screen/component.
