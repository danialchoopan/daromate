# یادآور پیشرفته دارو 💊⏰

یک اپلیکیشن پیشرفته، هوشمند و زیبا برای یادآوری مصرف دارو که با **Jetpack Compose** و معماری **Clean Architecture** ساخته شده است.

## ویژگی‌ها
- **زمان‌بندی پیشرفته:** قابلیت تنظیم به صورت ساعتی، روزانه، هفتگی، روزهای زوج/فرد و دوره‌های سفارشی (مثلاً ۲ هفته مصرف، ۱ هفته استراحت).
- **پروفایل جامع دارو:** ثبت نام دارو، دوز مصرفی، شکل دارو (قرص، شربت و غیره)، دستورالعمل مصرف (قبل/بعد از غذا) و علت مصرف.
- **مدیریت موجودی (Pill Tracker):** مدیریت موجودی داروها. اپلیکیشن به طور خودکار پس از تایید مصرف، از موجودی کم کرده و در صورت نیاز به خرید مجدد، به شما اطلاع می‌دهد.
- **نوتیفیکیشن‌های تعاملی:** امکان تایید مصرف ("Taken") یا به تاخیر انداختن ("Snooze") مستقیماً از طریق اعلان.
- **گزارش‌های پایبندی:** تقویم بصری و گزارش‌های درصدی سلامت برای پیگیری روند بهبودی.
- **رابط کاربری متریال ۳:** طراحی مدرن با پشتیبانی کامل از حالت تیره/روشن و تم داینامیک.
- **پشتیبانی از Wear OS:** همگام‌سازی اعلان‌ها با ساعت هوشمند برای دسترسی سریع.

## تکنولوژی‌های مورد استفاده
- **Jetpack Compose:** برای رابط کاربری مدرن و واکنشی.
- **Material 3:** برای رعایت آخرین استانداردهای طراحی.
- **Room:** برای ذخیره‌سازی محلی و ایمن داده‌ها.
- **Dagger-Hilt:** برای تزریق وابستگی (Dependency Injection).
- **WorkManager & AlarmManager:** برای وظایف پس‌زمینه مطمئن و زمان‌بندی دقیق.
- **Coroutines & Flow:** برای برنامه‌نویسی ناهمگام و جریان‌های داده واکنشی.

## شروع به کار

### نصب
1. پروژه را کلون کنید:
   ```bash
   git clone https://github.com/your-repo/medimate.git
   ```
2. پروژه را در **Android Studio Ladybug** یا نسخه‌های جدیدتر باز کنید.
3. گریدل را سینک کرده و اپلیکیشن را اجرا کنید.

### دسترسی‌ها (Permissions)
برای اندروید ۱۳ به بالا، اطمینان حاصل کنید که دسترسی‌های زیر داده شده است:
- `POST_NOTIFICATIONS`
- `SCHEDULE_EXACT_ALARM`

## اسکرین‌شات‌ها

| داشبورد | افزودن دارو | تاریخچه |
| :---: | :---: | :---: |
| ![Dashboard](./screenshots/dashboard_light.png) | ![Add Medicine](./screenshots/add_dark.png) | ![History](./screenshots/history.png) |

---
ساخته شده با ❤️ برای سلامتی.

---

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
