# Medicine Reminder Architecture & API Documentation

## Architecture Overview
The project follows **Clean Architecture** principles, divided into three main layers:

### 1. Domain Layer (`ir.danialchoopan.medimate.domain`)
- **Entities:** `Medicine`, `Reminder`, `MedicationLog`, `Inventory`. These are POJOs with no Android dependencies.
- **Repositories:** Interfaces defining how to interact with data sources.
- **Use Cases:** Business logic like `AddMedicineUseCase` and `MarkAsTakenUseCase`.

### 2. Data Layer (`ir.danialchoopan.medimate.data`)
- **Room Database:** Local storage using SQLite.
- **DAOs:** `MedicineDao`, `ReminderDao`, `MedicationLogDao`.
- **Implementations:** Repository implementations that bridge Domain and Data sources.
- **Background Tasks:** `ReminderReceiver` (AlarmManager) and `LowInventoryWorker` (WorkManager).

### 3. Presentation Layer (`ir.danialchoopan.medimate.presentation`)
- **UI:** Built entirely with **Jetpack Compose**.
- **ViewModels:** Managing state using `StateFlow` and interacting with Use Cases.
- **Theme:** Material 3 with support for Dark Mode and Dynamic Colors.

## Database Schema

### Medicines Table
- `id` (Int, PK)
- `name` (String)
- `description` (String)
- `dosage` (String)
- `form` (String)
- `instruction` (String)
- `reason` (String)
- `imageUri` (String?)
- `color` (Int)

### Reminders Table
- `id` (Int, PK)
- `medicineId` (Int, FK)
- `intervalType` (String: HOURS, DAYS, WEEKS, EVEN_DAYS, ODD_DAYS, CYCLE)
- `intervalValue` (Int)
- `nextReminderTime` (Long)
- `isActive` (Boolean)
- `cycleOnDays` (Int)
- `cycleOffDays` (Int)

### Inventory Table
- `medicineId` (Int, PK)
- `currentStock` (Int)
- `lowStockThreshold` (Int)

### Medication Logs Table
- `id` (Int, PK)
- `medicineId` (Int)
- `reminderTime` (Long)
- `takenTime` (Long?)
- `status` (String: TAKEN, MISSED, SNOOZED)

## Background Services
- **AlarmReceiver:** Handles system alarms to trigger notifications.
- **Notification Actions:** Supports "Taken" and "Snooze" directly from the notification tray.
- **WorkManager:** Performs periodic checks for low inventory.
