# DEBUG build v10
### You should manually uninstall any version lower than v09 before installing v09 or higher see v09 change history for details. 
### Settings -> Apps -> Bluefruit Playground -> Uninstall
- Double tap to reset the camera view for the accelerometer 3D model.
- Show loading indicator while the WebView and 3D model are loading.
- Added reminders to disable bluetooth and location settings if they weren't enabled to begin with
 
# DEBUG build v09
- package name changed to com.adafruit... Android system will see this as a different app than previous versions. Make sure to uninstall any previous ones before installing this one. Or else the system will allow you to install both and it'll be confusing.
- Neo pixel animation speed slider has a larger range
- About page implemented
- Help pages implemented
- Utilizing strings.xml more
- switching back to assets web loader for accelerometer activity
- signal strength icons in the pairing activity
- welcome activity
- All strings implemented from strings.xml
- pairing help page

# DEBUG build v08
- Changing the way neopixel animations work "under the hood". Now when it's set to the highest speed it will write the next animation frame as soon as the previous frame is finished writing. Lowering the speed setting adds extra delay from there. This could make the animations run a bit faster on some devices if they are able to write the BLE characteristic quicker. It will also solve the issue where the animations could sometimes keep running after you tried to turn them off with trashcan icon.


# DEBUG build v07
- starting this change history log
- fix issue when bluetooth adapter gets turned off before scanning period elapsed
- fix issue resulting from closing bluetoothGatt if it was never successfully connected
- exit the background service if the user leaves the app
- remove ability to exit background service from the notification
- fix exit button on EnableSettingsActivity
- add exit button at top left of PairingActivity
