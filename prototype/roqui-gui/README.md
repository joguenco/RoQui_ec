# RoQui GUI

Frontend GUI for RoQui_ec

## Dependencies
- Vue 3.5
- Bulma 1
- Node 26
- Vite 8
- pnpm 12
## Project Setup
```sh
pnpm install
```
### Format code
```sh
pnpm format
```
### Compile and Hot-Reload for Development
```sh
pnpm dev
```
### Compile and Minify for Production
```sh
pnpm build
```
### Run Unit Tests with [Vitest](https://vitest.dev/)

```sh
pnpm test:unit
```
### Run End-to-End Tests with [Playwright](https://playwright.dev)

```sh
# Install browsers for the first run
npx playwright install

# When testing on CI, must build the project first
pnpm build

# Runs the end-to-end tests
pnpm test:e2e
# Runs the tests only on Chromium
pnpm test:e2e --project=chromium
# Runs the tests of a specific file
pnpm test:e2e tests/example.spec.ts
# Runs the tests in debug mode
pnpm test:e2e --debug
```

### Lint with [ESLint](https://eslint.org/)

```sh
pnpm lint
```
### Check outdated dependencies

```sh
pnpm outdated
```
### List installed dependencies

```sh
pnpm list
```
## Solve dependencies issues
```sh
rm -rf node_modules pnpm-lock.yaml
pnpm store prune
pnpm install
```

