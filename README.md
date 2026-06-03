# Advanced Medicine Reminder 💊⏰

An advanced, smart, and beautiful medication reminder app built with **Jetpack Compose** and **Clean Architecture**.

## Features
- **Advanced Scheduling:** Hourly, daily, weekly, even/odd days, and custom cycles (e.g., 2 weeks on, 1 week off).
- **Comprehensive Pill Profile:** Track medicine name, dosage, form (tablet, syrup, etc.), instructions (before/after meal), and reason.
- **Pill Tracker (Inventory):** Manage your stock. The app automatically decrements stock when taken and notifies you when it's time to refill.
- **Actionable Notifications:** Mark medicines as "Taken" or "Snooze" directly from the notification.
- **Adherence Reports:** Visual calendar and percentage-based health reports to track your progress.
- **Material 3 UI:** Modern design with full Light/Dark mode support and Dynamic Theme.
- **Wear OS Support:** Sync notifications with your smartwatch for quick actions.

## Tech Stack
- **Jetpack Compose:** For a modern, reactive UI.
- **Material 3:** For the latest design standards.
- **Room:** For robust local data storage.
- **Dagger-Hilt:** For dependency injection.
- **WorkManager & AlarmManager:** For reliable background tasks and precise scheduling.
- **Coroutines & Flow:** For asynchronous programming and reactive data streams.

## Getting Started

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/medimate.git
   ```
2. Open the project in **Android Studio Ladybug** or newer.
3. Sync Gradle and run the app.

### Permissions
For Android 13+, ensure you grant the following permissions:
- `POST_NOTIFICATIONS`
- `SCHEDULE_EXACT_ALARM`

## Screenshots

| Dashboard | Add Medicine | History |
| :---: | :---: | :---: |
| ![Dashboard](./screenshots/dashboard_light.png) | ![Add Medicine](./screenshots/add_dark.png) | ![History](./screenshots/history.png) |

---
Built with ❤️ for health.
