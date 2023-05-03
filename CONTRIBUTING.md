# Contributing to 2FAS for Android

Thank you for considering contributing to the 2FAS open source project. Your support is greatly appreciated and will help us make this project even better. There are many ways you can help, from reporting bugs and improving the documentation to contributing code changes.

## Reporting Bugs

Before you submit a bug report, please search the [existing issues](https://github.com/twofas/2fas-android/issues) to see if the problem has already been reported. If it has, please add any additional information you have to the existing issue.

If you can't find an existing issue for your problem, please open a new issue and include the following information:

- A clear and descriptive title for the issue
- A description of the problem, including any error messages or logs
- Steps to reproduce the problem
- Any relevant details about your setup, such as the version of Android you are using

## Contributing Code

We welcome code contributions to the 2FAS for Android project. If you are interested in contributing, please follow these steps:

1. Fork this repository to your own GitHub account
2. Clone the repository to your local machine
3. Create a new branch for your changes (e.g. `feature/new-login-screen`)
4. Follow the [Project Setup](#project-setup)
5. Make your changes and commit them to your branch
6. Push your branch to your fork on GitHub
7. Open a pull request from your branch to the `develop` branch of this repository

Please make sure your pull request includes the following:

- A clear and descriptive title
- A description of the changes you have made
- Any relevant issue numbers (e.g. "Fixes #123")
- A list of any dependencies your changes require
- Tests for any new or changed code

We will review your pull request and provide feedback as soon as possible. Thank you for your contribution!

### App architecture and code style
The app is over five years old and has evolved over time. There is some code that needs to be executed on every update to ensure that users migrating from older versions of the app can keep their tokens and data intact. Currently, the app uses Room and EncryptedSharedPreferences for persistence, but it used to use ObjectBox and SecurePreferences. We still see users updating 2FAS from very old versions, so we need to keep these libraries in the app.

The app is partially modularized by feature layers. The older parts of the app are written in classic MVP + RxJava, while the latest features use MVVM with Jetpack Compose and Coroutines. Because of this "hybrid" structure with two different async frameworks, some use cases have had to be duplicated or wrapped with additional layers. The main goal is to refactor the app step by step to match the latest Google standards (using the NIA app as an example):

- Migrating all screens to Compose and MVVM
- Using Jetpack Navigation and a single activity
- Implementing the Material 3 theme
- Replacing RxJava with Coroutines
- Adding unit and UI tests for crucial parts of the app


### Project setup
1. Create your own debug signing key in `config/debug_signing.jks`
2. Create `config/config.properties` file with your debug key alias and password:
```
debug.storePassword=
debug.keyAlias=
debug.keyPassword=
```
3. Comment out `id("com.google.gms.google-services")` plugin in `app/build.gradle.kts`. This is needed to disable Firebase services (we do not share `google-services.json` in the repository).

By sharing ideas and code with the 2FAS community, either through GitHub or Discord, you agree that these contributions become the property of the 2FAS community and may be implemented into the 2FAS open source code.
