# MADdefinitivo

## Workspace 
Github:  
- Repository: https://github.com/RafaelAguilar64/MADdefinitivo
- Releases: https://github.com/RafaelAguilar64/MADdefinitivo/releases  

Workspace: https://upm365.sharepoint.com/sites/MADRAFAELSERGIO  
  

## Description
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
This feature is currently under development, and the form is not yet fully functional. Our team is working on completing this task for the next release of the app.

## Screenshots and navigation
<table>
  <tr>
    <td>
      <img src="https://raw.githubusercontent.com/RafaelAguilar64/MADdefinitivo/main/images/authentication.PNG" width="80%" alt="Pantalla de autenticación"/>
      <p align="center">Log In/Register activity(Authenticaction)</p>
    </td>
    <td>
      <img src="https://raw.githubusercontent.com/RafaelAguilar64/MADdefinitivo/main/images/main.PNG" width="80%" alt="Describe here image 2"/>
      <p align="center">Main activity of the app</p>
    </td>
  </tr>
  <tr>
    <td>
      <img src="https://raw.githubusercontent.com/RafaelAguilar64/MADdefinitivo/main/images/drawermenu.PNG" width="80%" alt="Describe here image 3"/>
      <p align="center">Drawer menu of the app</p>
    </td>
    <td>
      <img src="https://raw.githubusercontent.com/RafaelAguilar64/MADdefinitivo/main/images/openstreetmaps.PNG" width="80%" alt="Describe here image 4"/>
      <p align="center">Map and GPS usage activity</p>
    </td>
  </tr>
  <tr>
    <td>
      <img src="https://raw.githubusercontent.com/RafaelAguilar64/MADdefinitivo/main/images/secondactivity.PNG" width="80%" alt="Describe here image 5"/>
      <p align="center">List of coordinates</p>
    </td>
    <td>
    </td>
  </tr>
  <tr>
    <td>
      <img src="https://raw.githubusercontent.com/RafaelAguilar64/MADdefinitivo/main/images/thirdactivity.PNG" width="80%" alt="Pantalla de autenticación"/>
      <p align="center">Update and Delete coordinates activity</p>
    </td>
    <td>
      <img src="https://raw.githubusercontent.com/RafaelAguilar64/MADdefinitivo/main/images/reportinfo.PNG" width="80%" alt="Describe here image 2"/>
      <p align="center">Report information activity</p>
    </td>
  </tr>
  
</table>

## Demo Video

"https://www.youtube.com/watch?v=mvvYGKihuug"


## Features
List of **functional** features of the app.
- Get current weather information based on location, via API.
- Analyze weather data to determine if the current location matches the conditions required for the Gambilusa to live.
- Lists all the location data for the user to see.
- Possibility to update or delete location data from the list.
- Users can report information to the developers(on going development at the time of this post)

  List of **technical** features of the app.
- Firebase authentication
- Resful APIs used (OpenWeatherMaps https://openweathermap.org/api)
- Menu: Drawer
- Sensors: GPS coordinates
- Firebase Realtime database
- Persistence in Room database. Ref: https://github.com/RafaelAguilar64/MADdefinitivo/tree/main/app/src/main/java/com/ProyectoMAD/room

## How to Use

How to Use the Application
1. Install the Application  
  Download and install the APK file provided by the developer on your Android device.
2. Grant Permissions  
  When you open the app for the first time, it will request location permissions. Grant the necessary permissions to allow the app to access your location.
3. Main Features  
  View Map: The main screen displays a map where you can see your current location.
  Test Coordinates: Use the "Test Coordinates" button to interact with and test location data.
  View Saved Locations: Navigate to the list of saved coordinates by accessing the second activity.
  Detailed View: Tap on a saved coordinate to view detailed information, such as timestamp, latitude, longitude, and altitude.
4. Navigation  
  Use the buttons at the bottom of the screen to navigate between different activities:
    -Go to Report Info: Access additional information or reports.
    -Back to Main: Return to the main activity.
5. Save and View Locations  
  The app automatically saves your location data, which you can view in the list of saved coordinates.
6. Additional Features
  The app integrates weather forecasts based on your location.
  You can export or view location data for further use.

## Participants
List of MAD developers:
Rafael Aguilar Martinez-Peñalver(rafael.aguilar@alumunos.upm.es)
Sergio Alonso Vicente (sergio.alonsov@alumunos.upm.es)
