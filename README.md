# Mobilispect

![Android CI](https://github.com/alandovskis/mobilispect/actions/workflows/android-ci.yml/badge.svg)
![Backend CI](https://github.com/alandovskis/mobilispect/actions/workflows/backend-ci.yml/badge.svg)

## Directory Structure
### Frontend

The frontend is powered by Kotlin Multi Platform. Available only as an Android app, but an iOS app is planned.


```
android: Android App + UI

common
├── build: Outputs
├── schemas: Room database schemas
├── src
│   ├── androidAndroidTest: Android instrumented tests
│   ├── androidMain: Android-specific code
│   ├── androidTest: Android local tests
│   ├── commonMain: Shared logic
│   ├── commonTest: Shared tests
│   ├── iosMain: iOS-specific code
│   └── iosTest: iOS local tests

ios: iOS App + UI
```

### Backend

The backend is powered by Spring Boot.
