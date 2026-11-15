const { defineConfig } = require('@playwright/test');

const baseURL = process.env.E2E_BASE_URL || 'http://localhost:8080';

module.exports = defineConfig({
  testDir: './tests',
  timeout: 30000,
  retries: process.env.CI ? 1 : 0,
  use: {
    baseURL,
    headless: true,
    viewport: { width: 1280, height: 800 }
  },
  webServer: {
    command: 'java -jar ../target/dyfiwildlifecentre-1.0.jar',
    url: `${baseURL}/actuator/health`,
    reuseExistingServer: !process.env.CI,
    timeout: 90000
  }
});
