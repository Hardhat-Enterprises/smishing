# Smishing Detection – Frontend

Welcome to the frontend of the Smishing Detection project — a mobile security application designed to help users detect and respond to SMS phishing attacks. This repository contains the Android application interface, user experience flow, screen layouts, and frontend logic that connects users with the backend detection services.

## 📂 Project Structure

```text
smishing-frontend/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/               # Java/Kotlin source files
│   │   │   │   └── ...             # Activities, adapters, helpers, and app logic
│   │   │   ├── res/                # App resources
│   │   │   │   ├── drawable/       # Images, icons, and drawable files
│   │   │   │   ├── layout/         # XML layout files for screens
│   │   │   │   ├── mipmap/         # Launcher icons
│   │   │   │   ├── values/         # Colours, strings, themes, and styles
│   │   │   │   └── anim/           # Animation files
│   │   │   └── AndroidManifest.xml # App permissions and activity declarations
│   │   └── test/                   # Unit tests
│
├── gradle/                         # Gradle wrapper files
├── build.gradle                    # Project-level Gradle configuration
├── settings.gradle                 # Gradle project settings
├── gradlew                         # Gradle wrapper script for Linux/Mac
├── gradlew.bat                     # Gradle wrapper script for Windows
├── README.md
└── .gitignore

Getting Started
Prerequisites
Android Studio
Java Development Kit, JDK 17 or compatible version
Git
Android Emulator or physical Android device
Stable internet connection for Gradle sync
1. Fork this Repository

Fork the frontend repository from the organisation GitHub account.

Make sure you fork the correct branch, preferably the dev branch, so your changes are based on the latest development version.
Make sure to fork the `dev` branch or both `main` and `dev`, not ONLY the `main`. (Helpful guide [here](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/working-with-forks/fork-a-repo))


2. Clone your Fork

- In the forked repository in your Github account, make sure you are on `dev` branch and go to `Code` then, copy the link `https://github.com/your-username/smishing-backend.git`
- Open Git Bash and change directory to the folder where you want to clone your forked repository. Example

```bash
cd your-folder
```

- Next, execute these commands:

```bash
git clone https://github.com/your-username/smishing-backend.git
cd smishing-backend
```
(Helpful guide [here](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/working-with-forks/fork-a-repo)) in `Cloning your forked repository` section.

2.1 Install Dependencies

```bash
npm install
```

3. Open the Project in Android Studio
For a full step-by-step visual reference on how to open the project in Android Studio, complete the Gradle sync, and install any required SDK or Gradle dependencies, please refer to the onboarding video below:

[Frontend Onboarding Guide Video](https://deakin365-my.sharepoint.com/personal/s223873671_deakin_edu_au/_layouts/15/stream.aspx?id=%2Fpersonal%2Fs223873671%5Fdeakin%5Fedu%5Fau%2FDocuments%2FFrontend%20Onboard%20Guide%2EMOV&referrer=StreamWebApp%2EWeb&referrerScenario=AddressBarCopied%2Eview%2Eb6fdfd54%2D3772%2D466b%2D8d71%2D72f6bdbdaf83
))

5. Configure the Project

Check the following files before running the application:

build.gradle
settings.gradle
AndroidManifest.xml
res/values/strings.xml
res/values/colors.xml
res/values/themes.xml

If backend API URLs or keys are required, add them in the correct configuration file as instructed by the team lead.

5. Run the App

You can run the app using either:

Android Emulator, or
A physical Android device with USB debugging enabled.

Click the Run button in Android Studio.

If the build is successful, the app will launch on the selected device.

📱 Key Frontend Areas

Splash Screen- Initial loading screen and app entry point
Onboarding Screens-	Introduces users to the purpose of the app
Login/Register Screens-	Handles user authentication flow
Dashboard/Home-	Main user interface after login
SMS Scanner-	Allows users to scan or check messages
Results Screen-	Displays whether a message is safe or suspicious
Settings/Profile-	User account and app preferences

## 🛠️ Common Android Studio Issues & Troubleshooting

While setting up the project, you may experience some common Android Studio or Gradle-related issues. Different Android Studio versions may have slightly different layouts, settings locations, or prompts, which can sometimes make the setup process confusing. Please follow the troubleshooting steps below before making major changes to the project.

### ✅ Common Fixes

- Sync Gradle again using:
  - `File > Sync Project with Gradle Files`

- Clean and rebuild the project:
  - `Build > Clean Project`
  - `Build > Rebuild Project`

- Ensure the correct JDK version is selected:
  - `File > Settings > Build, Execution, Deployment > Build Tools > Gradle`

- Install any missing SDK tools or dependencies if Android Studio prompts you.

- Restart Android Studio after installing dependencies or changing settings.

- Make sure all Gradle dependencies are fully downloaded before running the project.

- Check that the emulator or physical device is properly connected and detected.

### ⚠️ Issues Faced During Setup

Some setup issues experienced during development included:

- Gradle sync failures
- SDK version mismatches
- Android Studio configuration errors
- Dependencies not downloading correctly
- Build errors caused by corrupted Android Studio setup
- Emulator/device detection issues

In some cases, restarting Android Studio or re-syncing Gradle resolved the issue. During development, a full reinstall of Android Studio and SDK tools was also required to fix persistent configuration problems.

### 📌 Important Note

Please avoid making major configuration changes to the project structure, Gradle files, or SDK versions unless discussed with the team lead first.

If issues continue after troubleshooting, contact the team lead for assistance before proceeding further.

Useful options:

File > Sync Project with Gradle Files
Build > Clean Project
Build > Rebuild Project
File > Invalidate Caches / Restart

**📜 Git Workflow**
Create a new branch before making changes:

git checkout dev
git pull origin dev
git checkout -b feature/your-task-name

After completing your changes:

git add .
git commit -m "Add your commit message here"
git push origin feature/your-task-name

Then create a Pull Request to merge your branch into dev.

**🤝 Contribution Guidelines**

Before contributing:

Pull the latest changes from dev.
Work on a separate feature branch.
Keep commits clear and meaningful.
Test your changes before pushing.
Do not directly commit to main.
Ask the team lead if you are unsure about files, branches, or merge conflicts.

