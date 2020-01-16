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
