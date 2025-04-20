# MADdefinitivo
This is an Android application developed in Android Studio with the goal of recording sightings of possible 
habitats for the repopulation of the Gambilusa, a small freshwater shrimp.

MAIN FEATURES

The application starts with a login system, required before accessing the main activity. This authentication system is implemented using Google Firebase.
Once logged in, you are taken to the Main Activity, where you will find:
    -A short description of the app's purpose.
    -A switch to activate GPS location.
    -Weather data (temperature, humidity, and general conditions) retrieved from the OpenWeatherMap API, 
     which is used to help evaluate the viability of the marked habitat locations.
The application includes a navigation drawer, from which you can access various activities:

OPENSTREETMAP VIEW

A map is displayed showing your current GPS location.
A button is provided to check if the current location is suitable for repopulation. This verification uses 
environmental data obtained from the API and assesses whether the conditions are optimal for habitat viability.

SAVED LOCATIONS

A list view where you can see your previously marked locations.
You can edit each saved location by clicking on it, allowing updates or changes.

REPORT HABITAT LOCATION

A form that allows the user to create a new report with:
    -Current GPS coordinates.
    -A custom description provided by the user, including any details about the location,
     access routes, or any other relevant information.
Once saved, this report is stored in the database and can be reviewed by the responsible 
organization to verify the site’s suitability for Gambilusa repopulation efforts.

