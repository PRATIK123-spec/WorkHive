# WorkHive

**Android task-management app for teams: managers assign tasks with deadlines, employees track and complete them, all synced in real time through Firebase.**

<!-- Screenshots: add docs/login.png and docs/tasks.png and uncomment
<p>
  <img src="docs/login.png" width="240"> <img src="docs/tasks.png" width="240">
</p>
-->

## Features

- **Role-based accounts**: sign up as a Manager or an Employee (Firebase Authentication, roles stored in Firestore)
- **Task assignment**: managers create tasks with a title, description, assignee, and an optional deadline (date picker)
- **Personal task feed**: managers see the tasks they assigned; employees see the tasks assigned to them
- **Deadline tracking**: each task shows "Due in N days", "Due Today", or "Due < 1 hour", and overdue tasks are automatically marked **Expired**
- **Task actions**: mark tasks as done; managers can delete tasks
- **Real-time sync**: task lists update live from Firestore

## Tech Stack

| Area | Technologies |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| Architecture | MVVM (ViewModels + StateFlow) |
| Backend | Firebase Authentication, Cloud Firestore |
| Build | Gradle (Kotlin DSL), minSdk 24, targetSdk 35 |

## Setup & Installation

1. Clone the repo and open it in Android Studio.
2. Create a Firebase project, add an Android app with package name `com.example.workhive`, and enable **Email/Password** sign-in and **Cloud Firestore**.
3. Download your `google-services.json` into `app/` (it is git-ignored and must not be committed).
4. Build and run:

```bash
./gradlew assembleDebug      # Windows: .\gradlew.bat assembleDebug
```

## Usage

1. Sign up as a **Manager** and create one or more **Employee** accounts.
2. As the manager, enter a task title and description, pick an employee and (optionally) a deadline, then tap **Assign Task**.
3. Sign in as the employee to see the assigned tasks with their deadline status, and tap **Done** when finished.

## Data Model (Firestore)

| Collection | Fields |
|---|---|
| `users/{uid}` | `name`, `email`, `role` (`MANAGER` / `EMPLOYEE`) |
| `tasks/{id}` | `title`, `description`, `assignedTo`, `assignedBy`, `assignedByRole`, `status` (`Pending` / `Completed` / `Expired`), `deadline`, `timestamp` |

## Project Structure

```
app/src/main/java/
├── com/example/workhive/
│   ├── MainActivity.kt, WorkHiveApp.kt   # entry point, application class
│   ├── AuthViewModel.kt                  # sign-up / login / roles
│   ├── MainViewModel.kt                  # task loading, assignment, expiry, completion
│   ├── screens/                          # Splash, Auth, Task (+ team management UI)
│   └── ui/theme/                         # colors, typography
├── di/                                   # Hilt module for Firebase (not yet wired in)
├── model/                                # Role, TeamMember, Task
├── navigation/                           # NavHost: splash → auth → tasks
└── network/                              # Retrofit client (not yet used)
```

## Future Work

- Hook up the team-management screen in navigation
- Connect the Retrofit API for AI task descriptions, summaries, and assignee recommendations (`network/`)
- Use the existing `priority` field in the UI
- Add Firestore security rules so users can only read and write their own team's tasks
- Finish Hilt dependency injection (`@HiltAndroidApp`, `@HiltViewModel`) using the module in `di/`
- Clean up duplicated dependencies in `app/build.gradle.kts`

## License

Released under the [MIT License](LICENSE).
