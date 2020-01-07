# Bluefruit Playground app for Android

Debug APK direct link: [https://github.com/FoamyGuy/Android_Bluefruit_Playground/raw/master/Bluefruit_Playground_v01_DEBUG.apk](https://github.com/FoamyGuy/Android_Bluefruit_Playground/raw/master/Bluefruit_Playground_v01_DEBUG.apk)

[iOS App Learn Guide PDF](https://cdn-learn.adafruit.com/downloads/pdf/bluefruit-playground-app.pdf)

[Circuit Playground Bluefruit UF2 Firmware File](https://adafru.it/HCh)

Install this firmware on your CPB and then power it up near your Android phone.

Bluetooth and Location services must be enabaled and you must allow the Location permission for the app to function.
The app should pretty well guide you into the right direction through setup process to get it working.
Launch the app and follow the on screen prompts to get started.

When you are done remember to click the notification to disconnect and exit the service.
Also remember to disable Bluetooth and Location services if you aren't otherwise using them to save battery.

---

Current Known Issues / Todo list:
* General
    - Service should finish itself after some time idle
    - pairing activity list of devices should show signal strength icons instead of raw value

* Accelerometer Module
    - 3D Model should allow user to manually drag to change view
    - Accelerometer activity should hide the ugly webview loading

* Neopixel Module
    - Animations can sometimes continue some frames after pressing the stop icon.
    - viewpager at the bottom should have indicators
    - Need to provide smaller cpb asset and make a layout for mdpi to fix pixel / selector checks alignment

* Buttons Module
    - The switch should either get set correctly automatically on load, or de-emphasize the first change

* Tone Module
    - the speaker symbol should animate - big when keys are pressed, regular when released

If you run across any trouble please make an issue.




