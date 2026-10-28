# Agent Guidelines & Rules

## 1. Automatic GitHub Push on Successful Updates (সফল আপডেটের পর গিটহাবে আপলোড)

- **Mandatory Action After Success**: Whenever any update, feature implementation, bug fix, refactor, or configuration change is successfully completed and verified:
  1. Stage the relevant modified/created files (`git add <files>` or `git add .`).
  2. Commit the changes with a clear, concise, and descriptive commit message explaining the update (`git commit -m "..."`).
  3. Create an annotated git tag for the new version (`git tag -a "vX.Y" -m "Release vX.Y: ..."`).
  4. Immediately push the commit and tags to GitHub (`git push origin <branch> --tags`).
  5. Verify that the push succeeded.
- **Verification First**: Never push broken, unverified, or failing code. Ensure verification/builds succeed before pushing (`./gradlew testDebugUnitTest`).
- **Security & Hygiene**: Ensure sensitive files, credentials, secrets, and build artifacts respect `.gitignore` and are never committed.

---

## 2. Strict Version Bumping & Release Policy (ভার্সন নাম্বারিং ও রিলিজ নীতিমালা)

> **CRITICAL RULE**: কোনো ব্যবহারকারী-মুখী ফিচার, পরিবর্তন বা বাগ ফিক্স কখনই ভার্সন নাম্বার না বাড়িয়ে গিটহাবে পুশ করা যাবে না। মোবাইল অ্যাপটি কেবল তখনই আপডেট প্রম্পট দেখাবে যখন রিমোট ভার্সন নাম্বার লোকাল ইনস্টলড ভার্সন থেকে গাণিতিকভাবে বেশি হবে।

### Version Switching Tiers (ভার্সন সুইচিংয়ের স্তর ও নিয়ম):

1. **Major / Big Updates (বড় কোনো আপডেট বা বড় কোনো পরিবর্তন)**:
   - **কখন প্রযোজ্য**: যখন কোনো বড় ফিচার সেট, বড় ইউআই ওভারহল, বড় আর্কিটেকচারাল পরিবর্তন বা নতুন বড় কোনো সেকশন/মডিউল যোগ করা হবে।
   - **নিয়ম (বড় নাম্বারে সুইচ)**: ভার্সন সুইচিং হবে সরাসরি পরবর্তী **Major Integer** এ।
     - উদাহরণ: `v6.x` থেকে সরাসরি ➔ **`v7.0`**, `v7.x` থেকে সরাসরি ➔ **`v8.0`**।
     - `versionCode` বড় ধাপে উন্নীত হবে (যেমন: 65 থেকে 70, 70 থেকে 80)।
   - **Command**: `./scripts/bump_version.sh major "<commit message>"`

2. **Minor / Standard Feature Updates (মাঝারি আপডেট বা সাধারণ ফিচার)**:
   - **কখন প্রযোজ্য**: বিদ্যমান ফিচারে নতুন কোনো বিকল্প বা অপশন যোগ, বটম শিট বা ডায়ালগের উন্নয়ন, সাধারণ পারফরম্যান্স অপ্টিমাইজেশন।
   - **নিয়ম**: মাইনর ভার্সন ইনক্রিমেন্ট হবে।
     - উদাহরণ: `v6.5` ➔ **`v6.6`**, `v6.6` ➔ **`v6.7`**।
     - `versionCode` ১ ধাপ বাড়বে (`versionCode + 1`)।
   - **Command**: `./scripts/bump_version.sh minor "<commit message>"`

3. **Patch / Bug Fixes (ছোট ফিক্স বা জরুরি প্যাচ)**:
   - **কখন প্রযোজ্য**: ক্র্যাশ ফিক্স, ফন্ট/টেক্সট কারেকশন, একক কোনো বাগ ফিক্স।
   - **নিয়ম**: প্যাচ ভার্সন ইনক্রিমেন্ট হবে (যেমন: `v6.5.1` বা `v6.6`)।
     - `versionCode` ১ ধাপ বাড়বে (`versionCode + 1`)।
   - **Command**: `./scripts/bump_version.sh patch "<commit message>"`

### 3-Point Version Synchronization Checklist (৩ স্তরে ভার্সন মিল নিশ্চিতকরণ):
প্রতিটি রিলিজের আগে নিশ্চিত করুন:
- [ ] `app/build.gradle.kts`: `versionCode` এবং `versionName` আপডেট হয়েছে।
- [ ] `app/src/main/java/com/example/expensetracker/common/AppConstants.kt`: `APP_VERSION = "vX.Y"` আপডেট হয়েছে।
- [ ] Git Tag: `vX.Y` ট্যাগ তৈরি হয়েছে এবং `git push origin main --tags` দিয়ে পুশ করা হয়েছে।
*(উক্ত কাজগুলো `./scripts/bump_version.sh` স্ক্রিপ্ট এক কমান্ডেই স্বয়ংক্রিয়ভাবে নির্ভুলভাবে সম্পন্ন করে।)*
