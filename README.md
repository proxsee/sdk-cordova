# ProxSee SDK for Cordova

The following document provides background information on the ProxSee SDK as well as outlines setup and usage instructions.
 
The content in this document is divided into the following sections:
 
- [Section 1: Introducing the ProxSee SDK](#section-1-introducing-the-proxsee-sdk)
    - [Background](#background)
    - [How Does the ProxSee SDK Work?](#how-does-the-proxsee-sdk-work)
    - [Key Concepts](#key-concepts)
        - [Beacon](#beacon) 
        - [Virtual Beacon](#virtual-beacon)
            - [Deployment](#deployment)
            - [Accuracy](#accuracy)
        - [Mobile API Key](#mobile-api-key)
        - [Locations](#locations)
        - [Tags](#tags)
        - [Metadata](#metadata)
        - [Check-In/Check-Out](#check-in-check-out)
- [Section 2: Implementing the ProxSee SDK in a Cordova Project](#section-2-implementing-the-proxsee-sdk-in-a-cordova-project)
    - [Prerequisites](#prerequisites)
    - [Generate a Mobile API Key](#generate-a-mobile-api-key)
    - [Create an App](#create-an-app)
    - [Add the ProxSee SDK](#add-the-proxsee-sdk)
    - [Handle Permissions](#handle-permissions)
    - [Request Location Access on iOS](#request-location-access-on-ios)
    - [Request Location Access on Android](#request-location-access-on-android) 
    - [Launch the ProxSee SDK](#launch-the-proxsee-sdk)
- [Section 3: Using the ProxSee SDK](section-3-using-the-proxsee-sdk)
    - [Handle Tag Changeset Notifications](#handle-tag-changeset-notifications)
        - [Register a Listener](#register-a-listener)
        - [Unregister a Listener](#unregister-a-listener)
    - [Start/Stop the ProxSee SDK](#start-stop-the-proxsee-sdk)
        - [Start the ProxSee SDK](#start-the-proxsee-sdk)
        - [Stop the ProxSee SDK](#stop-the-proxsee-sdk)
    - [Update Metadata](#update-metadata)
 
## Section 1: Introducing the ProxSee SDK
 
### Background
 
The ProxSee SDK takes the complexities out of beacon interaction and provides you with a simplified interface to quickly integrate iBeacon™ and virtual beacon (geo-fence) monitoring into your mobile application.
 
Combined with the ProxSee Admin Portal, the ProxSee SDK allows you to create and manage tags, listen for, receive, and respond to tag changeset notifications, add and associate user metadata to check-ins, and mine resultant data according to your needs (e.g., to determine wait times, travel patterns).
 
### How Does the ProxSee SDK Work? 
 
Once initialized, the ProxSee SDK associates a unique identifier with the user's mobile device, which is used in all communications to the central platform. The ProxSee SDK starts monitoring beacons/virtual beacons and sends check-in/check-out information to the central platform whenever an enter or exit event is detected:
 
- **Enter event**: The user approaches the beacon or enters the virtual beacon (geo-fence) circular boundary. The ProxSee SDK sends check-in information to the central platform when an enter event is detected.
- **Exit event**: The user moves away from the beacon or exists the virtual beacon (geo-fence) circular boundary. The ProxSee SDK sends check-out information to the central platform when an exit event is detected.  
 
Along with monitoring the beacons/virtual beacons, the ProxSee SDK also queries the central platform for tag information associated with a beacon/virtual beacon and automatically loads and caches information about nearby beacons/virtual beacons.
 
The ProxSee SDK allows your application to:
 
- **Listen For and Receive Tag Changeset Notifications**: Your application can listen for and receive tag changeset notifications sent by the ProxSee SDK. You can update the tags and positional information associated to a beacon/virtual beacon through the ProxSee Admin Portal without having to update your ProxSee SDK or the physical, deployed beacons.
- **Turn Monitoring On/Off**: The ProxSee SDK monitors beacons/virtual beacons, broadcasts check-ins/check-outs, send tag changeset notifications, and update metadata. At any point in your application, you can turn on or off the ProxSee SDK, which turns on or off monitoring. 
- **Send Metadata**: You can send additional information about a user such as account information and user IDs to the ProxSee SDK. When the ProxSee SDK receives metadata it associates it with the user's check-ins, which helps you identify users and devices among the collected data. 
 
### Key Concepts
 
#### Beacon
 
Also referred to as a "physical beacon" or an "iBeacon™", this is the physical device that you deploy and that the user's mobile device detects. Unlike location-based services on a mobile device, beacon ranging is fairly precise (essentially serving as "indoor GPS") and low-power, leading to its use indoors and where fine-tuned location context is desired.
 
#### Virtual Beacon
 
A virtual beacon is a geo-fence that behaves  based on the user crossing a circular boundary on a map rather than nearing a physical beacon. s such, it can serve as a less accurate beacon in locations the customer may not have the access/permission to add a physical device. 
 
Virtual beacons work in concert with physical beacons. In order to work properly, virtual beacons should be placed on the map in such a way that a user would hit a physical beacon before hitting a virtual beacon. For example:
 
- **Bad Placement**: Putting a virtual beacon in the parking lot of a mall and a physical beacon inside the mall would not allow you to detect people approaching from the parking lot.  The ProxSee SDK would send notifications of users hitting the beacon in the mall first, not in the parking lot. 
- **Good Placement**: Putting a virtual beacon on Rent a Car Road in Las Vegas and a physical beacon inside the arrival area of the Las Vegas airport would allow you to detect people coming off of an airplane and then detect those that went to the rental car area. 
 
###### Deployment
 
- For best results, virtual beacons should be deployed with a medium or greater range.
- Virtual beacons should be placed in areas where a user is likely to remain or traverse for several seconds/minutes.
 
##### Limitations
 
- You are limited to 5 virtual beacons per location.
- The outer radius of a virtual beacon must be more than 200 meters in distance from the outer radius of any other beacon (physical or virtual).
 
##### Accuracy 
 
The accuracy of virtual beacons is based on the GPS/network provider; however, the following factors may affect the accuracy of a virtual beacon:
 
- Area obstructions
- The capabilities of the user’s mobile device
- The geo-location abilities of the user’s mobile device
- Indoor placement (Note: This may significantly affect the accuracy of the virtual beacon)
 
The ProxSee SDK is also expected to receive a location update whenever the device is moved approximately 100 meters. in general, the closer you are to a beacon the more accurate the reported distance. 
 
Because of the factors mentioned above, it’s not possible to provide specific numbers for accuracy. On average, the measurement error can be 20-30% of the actual distance. You can increase signal reliability by increasing the Broadcasting Power. For greater accuracy, the use of beacons (physical devices) is recommended.
 
 
#### Locations
 
Locations within the ProxSee platform group beacons/virtual beacons by region and establish a default tag for each beacon/virtual beacon within the region. Locations also play a part in the caching of beacon data.
 
To add/delete locations, see the Locations section of the ProxSee Admin Portal. 
 
#### Tags
 
Tags are simply short descriptions in the form of hashtags that are associated to beacons/virtual beacons for identification and classification purposes.
 
By default, each beacon/virtual beacon has a “#<Location>” (with <Location> being the Location of the beacon/virtual beacon) tag. 
 
You can associate the same tag with multiple beacons/virtual beacons.  or instance, you may place beacons at all of your exits and associated them all with an  "#Exit" tag.. However, if a user moves between two beacons associated with the same tag, the ProxSee SDK will not create a tag changeset notification. A tag changeset notification is only generated when the tags change (e.g., tags were removed from or added to the beacon/virtual beacon). 
 
To add and assign/remove tags, see the Tags section of the ProxSee Admin Portal. if a 
 
#### Metadata
 
Metadata can be used to provide additional information about a user and their device, such as user ID and user preferences. The metadata sent to the ProxSee SDK is then associated to each check-in by the user’s device.
 
See the Update Metadata section in this document for instructions on how to add metadata.  
 
#### Check-In/Check-Out
 
A checkin-in/check-out forms a tuple for a device event and helps track enter and exit events.  
When tracking enter (check-in) and exit (check-out) events, keep in mind:
You may have a check-in without a corresponding check-out. The most common cause of this is network interruptions.  
 
When using virtual beacons, check-ins are more likely to occur than check-outs.
 
Data is stored during both a check-in and a check-out.
 
- **Check-in**: The majority of information is stored during a check-in and includes: 
    - the time
    - the device's unique ID (UUID)
    - additional system information, including  the version of the SDK used 
- **Check-out**: Only the check-out time is updated and stored.

## Section 2: Implementing the ProxSee SDK in a Cordova Project
 
Implementing the ProxSee SDK for Cordova is a simple five-step process:
 
- [Generate a Mobile API Key](#generate-a-mobile-api-key)
- [Create an App](#create-an-app) 
- [Add the ProxSee SDK](#add-the-proxsee-sdk)
- [Handle Permissions](#handle-permissions)
- [Launch the ProxSee SDK](#launch-the-proxsee-sdk)
 
### Prerequisites
 
Before implementing the ProxSee SDK, ensure that you have **Cordova 6.4.0** or higher installed and running (i.e., installing node.js, npm, cordova, and plugman).  For instructions, see [Cordova's Get Started](https://cordova.apache.org/#getstarted) guide. 
 
If you don’t have the latest version of Cordova, update it using ``` npm install -g cordova```. **Note**: If using a Mac OS you may need to uninstall the previous version of Cordova first.
 
The ProxSee SDK also requires: 
 
- An active Bluetooth service in order to function with the beacons/virtual beacons
- Active Location services in order to function with virtual beacons
- An Internet connection 
 
### Generate a Mobile API Key
 
In order to use the ProxSee SDK, you will need to generate a Mobile API Key.
 
1. Navigate to the ProxSee portal at [https://app.proxsee.io/#/login](#https://app.proxsee.io/#/login).
2. From the login page, enter your username and password and then click **Login**.
3. From the navigation bar on the left side of the screen, click **Applications**.
4. Click **Create Application**.
5. From the **API Type** section, select **Mobile**.
6. Copy the generated GUID. This is your Mobile API Key. 
 
**Note**: If you have multiple applications, you may wish to generate a unique Mobile API Key for each one.
 
### Create an App
 
This step is only required if you have not yet created your Cordova app. The following sample depicts how to create a Cordova app:
 
**Warning**: Due to earlier versions of Cordova’s Android platform relying on temporary templates that are no longer provided by the Android SDK, you may get an error specifying that the Gradle wrapper cannot be found. To resolve this issue, you must specify  ```android@6.2.1```.
 
```

cordova create new-app-folder-name net.example.cordova "My Cordova ProxSee App"
cd new-app-folder-name
cordova platform add ios
cordova platform add android


```


 
### Add the ProxSee SDK
 
Once you have your app created, you can add the ProxSee SDK. 
 
```

	plugman install --platform android --project . --plugin https://github.com/ProxSee/SDK-Cordova
	plugman install --platform ios     --project . --plugin https://github.com/ProxSee/SDK-Cordova
 
```
### Handle Permissions
 
The ProxSee SDK requires both Bluetooth and Location access. For Location access in iOS and recent versions of Android users are required to be prompted for access to their location. In older versions of Android, simply adding the permissions is sufficient; the SDK includes this.

#### Request Location Access on iOS
 
On iOS 8+ simply provide the ```NSLocationAlwaysUsageDescription``` key (and description) in your app's **plist** file.
 
Once this plist setting is provided and the ProxSee SDK started, the ProxSee SDK will automatically prompt the user for location access. 


 
#### Request Location Access on Android 
 
[You must prompt users for access to their location](https://github.com/ProxSee/SDK-Android#permissions).


Alternatively, although highly discouraged, you can target an older version of Android. which will retain the previous "permissions granted on install" behaviour, by adding the following to your **config.xml** in the ```<platform name="android">``` tag:


```
<preference name="android-targetSdkVersion" value="22" />


```

### Launch the ProxSee SDK

Initialize the ProxSee SDK using the Mobile API Key you generated in a previous step.
 
**Notes**: 
- Replace “YourApiKey” with the Mobile API Key you generated in a previous step. 
- The onSuccess and onError callbacks are, in this case, optional and can be replaced with nulls if desired.


```
proxsee.init(onSuccess, onError, "YourApiKey");


```



## Section 3: Using the ProxSee SDK

All API calls in the ProxSee SDK for Cordova are documented below as well as in the Javascript file. 
 
The API calls in the ProxSee SDK for Cordova:

- Are In the proxsee namespace (e.g. ```proxsee.init(...)```)
- Are thin wrappers around the platform code for optional performance
- Have optional (but recommended) callbacks as the first two parameters (onSuccess and onError respectively) 
    - All onError callbacks include an error message as the first parameter.

The following actions can be performed with the ProxSee SDK:
 
- [Handle Tag Changeset Notifications](#handle-tag-changeset-notifications)
- [Turn On/Off the ProxSee SDK](#turn-on-off-the-proxsee-sdk)
- [Update Metadata](#update-metadata)
 
### Handle Tag Changeset Notifications
 
#### Register a Listener

You can register a listener directly with the API. Note that the ```onSuccess``` callback will be called multiple times. The first time it is called you will receive an ID, which is the listener ID you can use to [unregister the listener](#unregister-a-listener). Each subsequent time it is called you will receive beacon tag data as with the ```beaconNotification``` event.


```
proxsee.registerListener(onSuccess, onError);


```
Alternatively, you can simply register a listener to the ```beaconNotification``` event, just as you would any other event in Javascript. The ```event.detail``` member will contain the tag information. 
 
#### Unregister a Listener
 
To unregister a listener:

```
proxsee.unregisterListener(onSuccess, onError, id);


```

### Turn On/Off the ProxSee SDK

At any point of the application lifecycle you can turn on or off the SDK to toggle monitoring beacons, broadcasting check-ins/check-outs, notifying of tag changes, and updating metadata.
 
#### Turn On the ProxSee SDK
 
To turn on the ProxSee SDK and in turn enable beacon monitoring, check-in/check-out broadcasts, tag changeset notifications, and metadata updates:
 
```
proxsee.start(onSuccess, onError);
 
```
#### Turn Off the ProxSee SDK
 
To turn off the ProxSee SDK and in turn disable beacon monitoring, check-in/check-out broadcasts, tag changeset notifications, and metadata updates:


```
proxsee.stop(onSuccess, onError);

```

### Update Metadata

The following example depicts how to add metadata (e.g., user IDs, user preferences), which will get associated to every check-in by the user. 

```
proxsee.setData(successCB, failCB, dataObj);


```
The data object must be valid as JSON (e.g., cannot include functions). For example:

```
	var handler = {
		onSuccess: function() {
			app.log("Metadata set successfully!");
		},
		onError: function (errorMsg) {
			app.log("Error setting metadata: " + errorMsg);
		}
	};
	
	var dataObj = {
		"some": "data",
		"foo": "bar"
	};
	
	proxsee.setData(handler.successCB, handler.failCB, dataObj);

```
 
 
