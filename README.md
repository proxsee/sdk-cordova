# ProxSee SDK

The ProxSee SDK provides you with a simplified interface to quickly integrate iBeacon™ and geo-fence (virtual beacon) monitoring to your mobile application.

By managing and associating meaningful tags via a central platform, you can reduce the complexities of beacon interaction to simply responding to notifications of tag changes as an end user approaches and departs from beacon/geo-fence regions.

Furthermore, the ProxSee SDK will automatically communicate a check-in/check-out to a central platform to record a user's activities. In addition, you can send additional data about the user that will be associated with their check-ins. All of the resultant data could then be mined according to your needs - e.g. to determine wait times, travel patterns, etc.


## Table of Contents

* [How Does the ProxSee SDK Work?](#how-does-proxsee-sdk-works)
* [Key Concepts](#key-concepts)
    * [Beacon Device](#beacon-device)
    * [Virtual Beacon](#virtual-beacon)
        * [Recommendations](#recommendations)
        * [Limits](#limits)
    * [Mobile API Key](#mobile-api-key)
    * [Locations](#locations)
    * [Tags](#tags)
    * [Metadata](#metadata)
    * [Check-in/Check-out](#check-in-check-out)
* [Installation](#installation)
    * [Permissions, and Android 6.0 runtime permissions](#permissions)
* [Usage](#usage)
    * [Launching the SDK](#launching-the-sdk)
    * [Receiving Tag Changeset Notifications](#receive-tags-changeset-notifications)
    * [Turning On/Off Monitoring](#turning-monitoring-on-off)
    * [Updating Metadata](#updating-metadata)
* [Tested with](#Tested with)


## <a name="how-does-proxsee-sdk-works"></a>How Does the ProxSee SDK Work?

The ProxSee SDK wraps iBeacon™ and geo-fence APIs for easy use and integration. The SDK requires an active Bluetooth service in order to function with iBeacons™ and active Location services in order to function with virtual beacons (geo-fences). It also requires Internet connectivity.

On launching your mobile application, you will need to initialize the ProxSee SDK - for this, you will need a valid Mobile API key (see [Mobile API Key](#mobile-api-key)). Once initialized, the SDK will associate a unique identifier with the end user's device - used in all communications to the central platform. The SDK will start monitoring beacons and will send check-in/check-out information to the central platform whenever a beacon enter or exit event is received. The SDK will also query the main platform for tag information associated with a beacon and will automatically load and cache information about nearby beacons (and geo-fences).

During the course of operation of your application, you can send additional data (see "Updating Metadata" under [Usage](#usage)), the latest version of which will be associated in turn with a user's check-ins. This metadata could include user account information, ids, etc. that you could later use to identify a particular user and his/her device among all of the data collected or data you could otherwise report on.

One of the primary purposes of the SDK, however, is to allow a mobile application to listen for tag changes. As each beacon can have one or more tags associated with it, by acting upon the appearance or disappearance of a tag, you can easily handle approaching and leaving a beacon's region. In this respect, real beacons and virtual beacons (geo-fences) operate nearly identically (more on the differences in Key Concepts). The beacons' associated tags and positional information can be updated on the central platform without a need to update the SDK version nor the physical beacons that have been deployed.

At any stage of the application lifecycle you can turn on and off the SDK. Turning off the SDK will stop monitoring beacons, broadcasting check-ins/check-outs, notifying of tag changes.

## <a name="key-concepts"></a>Key Concepts

### <a name="beacon-device"></a>Beacon Device

Also referred to as a "real beacon", a "beacon" or an "iBeacon™", this is the physical beacon device that you will have deployed, that the user's device will detect. Unlike location-based services on a mobile device, beacon ranging is fairly precise (essentially serving as "indoor GPS") and low-power, leading to its use indoors and where fine-tuned location context is desired.

### <a name="virtual-beacon"></a>Virtual Beacon

A virtual beacon is a geo-fence - it acts like a broad-ranging real beacon but is based on the user crossing a circular boundary on a map as opposed to nearing a real beacon - as such, it can serve as a less accurate beacon in locations the customer may not have access/permission to add a real beacon device. You are limited to 5 virtual beacons being defined per general Location.

Virtual beacons work in concert with Beacon Devices - In order to work properly, the virtual beacons should be placed on the map in such a way as the user would hit a real beacon before hitting the virtual one. Example:

* Bad: Putting a virtual beacon in the parking lot of a mall and a beacon device inside the mall - would not be able to detect people approaching from the parking lot (the SDK would first begin to notify upon hitting the beacon device in the mall, not in the parking lot)
* Good: Putting a virtual beacon on Rent A Car Rd in Las Vegas and a beacon device inside the arrival area would be able to detect people coming off of the plane and then detect those that went off to the rental area

##### <a name="recommendations"></a>Recommendations
* Select a medium or greater range for your virtual beacon for best results
* Place the virtual beacon in an area a user is likely to remain or traverse for several seconds/minutes
* Do not depend on the triggering of virtual beacons - any number of factors including obstructions in the area, the user's phone capabilities, etc. can interfere with its operation
* Note that check-ins in a virtual beacon are far more likely to occur than check-outs

##### <a name="limits"></a>Limits
* The outer radius of a virtual beacon must be more than 200m in distance from the outer radius of any other beacon (real or virtual) - this is enforced by the main platform
* The accuracy of the virtual beacon may drop significantly in an indoor region
* The virtual beacon sensitivity is limited to the given device's geo-location abilities and the surrounding environment - results may vary. If greater accuracy is required, real beacon devices are recommended
* Virtual beacons will stay resident in a user's device until arriving at a new Location (or a reboot)

### <a name="mobile-api-key"></a>Mobile API Key

In order to use the SDK, you will need to generate a Mobile API key.

To generate one, simply:

1. Log in to the central platform
2. Select the "Applications" section
3. Choose "Create Application" and choose "Mobile" as the API Type.
4. Then you can copy the GUID generated.

Normally you will only generate one Mobile API key, but if you had multiple applications, you may wish to generate a unique one for each.

### <a name="locations"></a>Locations

A Location within the ProxSee system establishes a region that groups beacons (real and virtual). It also plays a part in the caching of beacon data and establishes a default tag for all the beacons within.

### <a name="tags"></a>Tags

Tags can be associated with beacons and are in the form of hashtags. By default, each beacon has a tag of "#" + its Location name. You can associate the same tag with multiple beacons (for instance, you may place beacons at all of your exits and tag them "#Exit") or not specify any tags at all.

Whereas, whenever a beacon is seen (or last seen) a check-in/check-out will occur, if a user moves between two beacons with the same tags, it will not result in a new notification in the SDK. Your listening application will only be notified when the tags change (either tags were removed or they were added).

### <a name="metadata"></a>Metadata
Metadata can be used to provide additional information about a user or their device, an example would be their user id within your system, their preferences, etc. The metadata sent will be associated to the unique id given to that user's specific device. When the SDK performs a check-in on seeing a new beacon, the last sent metadata will be associated with that check-in.

While the function used is named "updateMetadata", be aware that this metadata is essentially fully versioned - each update sent will be stored as its own record. As such, it is recommended that you only send metadata when it has changed. If the SDK detects that the metadata has not changed from the previous request, it will not send the metadata again (it will, however, return a successful result to its CompletionHandler).

Note that the key-value pairs will be stored as a JSON object in the central platform and you may wish to design your representation so that it is easily query-able later when you do reporting on it.

### <a name="check-in-check-out"></a>Check-in/Check-out

A checkin-in/check-out forms a tuple for a device event and track beacon region enter and exit events. The majority of information is stored during a check-in including the time, the device's unique id (UUID) and additional system information like the version of the SDK used. The check-out merely updates the check-out time.

Note that it is possible to have a check-in without a check-out time - network interruptions, etc. could cause a missed check-out.

One special type of check-out is the implied check-out for a virtual beacon. This happens when the user's device does not properly detect that it has left a region but has detected a new beacon. Since virtual beacons may not be placed in close proximity with any other beacon, this results in an implied check-out with an additional flag "context.impliedCheckout=true" appearing in its deviceevent record.

## <a name="installation"></a>Installation

These instructions assume you have Cordova already installed and running
(such as installing node.js, npm, cordova, and plugman) --
see [Cordova's Get Started](https://cordova.apache.org/#getstarted).
However, make sure you have at least Cordova 6.4.0 installed.
Update with `npm install -g cordova`, although macOS users may need to uninstall first.

If you do not have an app created yet, create it with e.g.:

	cordova create new-app-folder-name net.example.cordova "My Cordova ProxSee App"
	cd new-app-folder-name
	cordova platform add ios
	cordova platform add android

Note that you may need to specify `android@6.2.1` due to earlier versions of Cordova's Android platform
relying on temporary templates that are no longer provided by the Android SDK.
If you get an error to the effect that the Gradle wrapper cannot be found, this solves that issue.

Next, add the ProxSee plugin, e.g.:

	plugman install --platform android --project . --plugin https://github.com/ProxSee/SDK-Cordova
	plugman install --platform ios     --project . --plugin https://github.com/ProxSee/SDK-Cordova

### <a name="permissions"></a>Permissions, and Android 6.0 runtime permissions

ProxSee requires Bluetooth and Location access. While generally nothing special needs to be done for Bluetooth,
in iOS and recent versions of Android this requires prompting the user for access to their location.
(In older versions of Android, simply adding the permissions is sufficient; The plugin includes this.)

On Android [you must prompt for access](https://github.com/ProxSee/SDK-Android#permissions).
Alternatively (disrecommended!) you can target an older version of Android
(which will retain the previous "permissions granted on install" behaviour)
by adding the following to your `config.xml` in the `<platform name="android">` tag:

	<preference name="android-targetSdkVersion" value="22" />

On iOS 8+ you must simply provide the NSLocationAlwaysUsageDescription key (and description)
in your application's plist file. You can modify this file directly or use some other way to manage it;
As of this writing Cordova does not do this on its own.
Once this plist setting is provided, ProxSee will automatically prompt the user for location access when you enable it.
*It is your responsibility to only enable ProxSee at a time which makes sense for your application.*

## <a name="usage"></a>Usage

All API calls in the ProxSee Cordova plugin are:
* In the proxsee namespace (e.g. `proxsee.init(...)`)
* Have (generally optional but recommended) callbacks as the first two parameters (onSuccess and onError respectively)
* onError callbacks all include an error message as the first parameter.
* Documented here and in the Javascript file
* Thin wrappers around the platform code for optimal performance

### <a name="launching-the-sdk"></a>Launching the SDK

Obtain a Mobile API key (see [Mobile API Key](#mobile-api-key)). When the "deviceready" event fires,
initialize the ProxSee SDK with the Mobile API key:

	proxsee.init(onSuccess, onError, "YourApiKey");

Evidently you should replace "YourApiKey" with the API key you have obtained.
The onSuccess and onError callbacks are, in this case, optional and can be replaced with nulls if desired.

### <a name="receive-tag-changeset-notifications"></a>Receive Tag Changeset Notifications

To receive tag changeset notifications, simply register a listener to the "beaconNotification" event,
just as you would any other event in Javascript. The `event.detail` member will contain the tag information.
Alternatively, you can register a listener directly with the API:

	proxsee.registerListener(onSuccess, onError);

Note that if you use the API method, the onSuccess callback will be called multiple times.
The first time you will receive an ID; This is the listener ID so that you can unregister the listener:

	proxsee.unregisterListener(onSuccess, onError, id);

Each subsequent time you will receive beacon tag data as with the beaconNotification event.

<!-- TODO: Data sample! There are no reference classes in JS! -->

### <a name="turning-monitoring-on-off"></a>Turning Monitoring On/Off

At any point of the application lifecycle you can turn on or off the SDK to toggle monitoring beacons,
broadcasting check-ins/check-outs, notifying of tag changes, and updating metadata.

To turn off monitoring:

	proxsee.stop(onSuccess, onError);

To turn on monitoring:

	proxsee.start(onSuccess, onError);

### <a name="updating-metadata"></a>Updating metadata

At any point of the application lifecycle you can set and update metadata.
This metadata is whatever you wish to associate with check-ins/check-outs, e.g. a customer ID or coupon number.

	proxsee.setData(successCB, failCB, dataObj);

The data object must be valid as JSON, in other words you cannot include e.g. functions. For example:

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

## <a name="tested"></a>Tested with

* Node 6.10.1 (npm 3.10.10, for macOS nvm 0.33.2)
* Cordova 6.5.0
* Android SDK tools 26.0.1 / platform-tools 25.0.5 / build tools 25.0.3, Xcode 8.2.1
* Windows 7, macOS 10.11.6 El Capitan
