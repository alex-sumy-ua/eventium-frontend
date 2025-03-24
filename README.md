# EVENTIUM - Frontend (Android App)

An Android mobile application for a community event platform that enables users to browse, register for, and manage events. Users can add events to Google Calendar, and staff members can create, update, and delete events. The app is built in Java and communicates with a Spring Boot backend API.


## ✨ Project Summary

### EVENTIUM Android app supports:

Viewing a list of upcoming community events

Registering for events (one registration per user per event)

Adding registered events to the user's Google Calendar

Staff functionality to create, update, and delete events

GitHub OAuth2 login to authenticate users and manage roles (admin, staff, member)

The app is fully functional and adheres to the project MVP requirements.

### 📊 Technologies Used

Core Stack

Java (Android Studio)

Retrofit for HTTP API requests

MVVM Architecture (Model-View-ViewModel)

GitHub OAuth2 Integration

Google Calendar Intent API

UI

Material Design

ConstraintLayout

RecyclerView for list rendering

Dialog Fragments for forms

### 📊 Tools & Platforms

Android Studio (Java)

Pixel 6 Pro API 35 Emulator (used during development; functionally covers most modern smartphones)

Git & GitHub

Postman (for backend testing)

PGAdmin (for DB testing)


## 📅 Features

✅ Browse all events

✅ View detailed information about each event

✅ Register and unregister for events

✅ Add events to Google Calendar (after registration)

✅ Only staff users can create, update, or delete events

✅ Filter registrations: All / Mine

✅ OAuth2 authentication using GitHub (handled via backend)


## 🚀 Running the App Locally

### 1. Prerequisites

Android Studio (latest version recommended)

Git

A running instance of the EVENTIUM backend (see Backend README)

### 2. Setup

Clone the repo:

git clone https://github.com/your-username/eventium-frontend.git
cd eventium-frontend

Open in Android Studio:

Choose "Open an existing project"

Select the root folder

Update backend API base URL:

// Example: in ApiClient.java or Retrofit provider
private static final String BASE_URL = "http://10.0.2.2:8080/api/";

Run the app on emulator or physical device


### 🌐 Authentication

GitHub OAuth2 login is initiated from the frontend and handled by the backend.

Upon successful login, a token is received and passed in the Authorization header for subsequent requests.


## ☑️ MVP Completion

This frontend application fully supports:

✅ Event browsing

✅ Event registration

✅ Google Calendar integration

✅ GitHub OAuth2 login and role-based permissions


## ✨ Optional Extensions

✅ Spring Security implement full authentication using the Spring Security framework for secure communication between the backend API and frontend Android app (done)

## ✨ Future Work

- Payment integration (e.g. Google Pay)

- Social media sharing of events

- Push notifications for reminders


## 💼 License

This project was developed as a one-month individual contract for Northcoders.
It is intended for educational and demonstration purposes only, but it is fully functional application and can be used for the purposes described in the Project Summary.
If publishing publicly, you may consider adding an open-source license (e.g., MIT) after discussing with the stakeholders.

## 📅 Author

### Oleksandr Plachkovskyi (Northcoders student, consultant)
### plachkovskyy@yahoo.com
