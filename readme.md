# Bluefruit Playground app for Android

Debug APK direct link: [https://github.com/FoamyGuy/Android_Bluefruit_Playground/raw/master/Bluefruit_Playground_v08_DEBUG.apk](https://github.com/FoamyGuy/Android_Bluefruit_Playground/raw/master/Bluefruit_Playground_v08_DEBUG.apk)

[iOS App Learn Guide PDF](https://cdn-learn.adafruit.com/downloads/pdf/bluefruit-playground-app.pdf)

[Circuit Playground Bluefruit UF2 Firmware File](https://adafru.it/HCh)

Install this firmware on your CPB and then power it up near your Android phone.

Bluetooth and Location services must be enabaled and you must allow the Location permission for the app to function.
The app should pretty well guide you into the right direction through setup process to get it working.
Launch the app and follow the on screen prompts to get started.


When you are done remember to disable Bluetooth and Location services if you aren't otherwise using them to save battery.

---

Current Known Issues / Todo list:
* General
    - pairing activity list of devices should show signal strength icons instead of raw value
    - About page needs to be implemented
    - Module Help pages need to be implemented
    - Switch package to adafruit namespace
    - all hard coded strings should be moved to strings.xml file
    - add reminder to EnableSettingsActivity to turn off the settings after user is done.
    
* Accelerometer Module
    - Accelerometer activity should hide the ugly webview loading

* Neopixel Module
    - Running animations should get stopped when user selects a single color.
    - Increase the range of the speed slider for the Animations.
    
    
If you run across any trouble please make an issue.




