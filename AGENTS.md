# Agent Guidelines & Rules

## 1. Automatic GitHub Push on Successful Updates (সফল আপডেটের পর গিটহাবে আপলোড)

- **Mandatory Action After Success**: Whenever any update, feature implementation, bug fix, refactor, or configuration change is successfully completed and verified:
  1. Stage the relevant modified/created files (`git add <files>` or `git add .`).
  2. Commit the changes with a clear, concise, and descriptive commit message explaining the update (`git commit -m "..."`).
  3. Immediately push the commit to GitHub (`git push origin <branch>`).
  4. Verify that the push succeeded.
- **Verification First**: Never push broken, unverified, or failing code. Ensure verification/builds succeed before pushing.
- **Security & Hygiene**: Ensure sensitive files, credentials, secrets, and build artifacts respect `.gitignore` and are never committed.
