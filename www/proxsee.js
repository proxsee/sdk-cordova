(function(window) {
	var adapterName = "Proxsee";
	var debug = false;
	
	// Convenience functions
	var wrapErrCB = function(failCB, logStr) {
		return function(errStr) {
			if (debug) console.log(logStr);
			if (debug) console.log(errStr);
			if ((failCB !== undefined) && (typeof failCB == "function"))
				failCB.apply(this, arguments);
		};
	};
	
	// Event functions
	var onBeaconNotificationListenerId = -1;
	var onBeaconNotification = function(ev) {
		if (isNaN(ev)) {
			if (debug) console.log(ev);
			window.dispatchEvent(new CustomEvent("beaconNotification", {detail: ev}));
		} else {
			onBeaconNotificationListenerId = ev; // The first time around, it is actually just the ID, not event data.
		}
	}
	
	// Publicly visible/reachable API
	var pub = {
		/**
		 * Initializes Proxsee SDK. Callbacks are optional, but API key is mandatory.
		 */
		init: function(successCB, failCB, apiKeyString) {
			var fcb = wrapErrCB(failCB, "Could not initialize Proxsee SDK (was a valid API key supplied?):");
			cordova.exec(successCB, fcb, adapterName, "init", [apiKeyString]);
		},
		
		/**
		 * Set custom data for later retrieval/tracking.
		 * obj must be a single-depth key-value mapped object.
		 */
		setData: function(successCB, failCB, obj) {
			var fcb = wrapErrCB(failCB, "Failed to set metadata:");
			cordova.exec(successCB, fcb, adapterName, "setData", [obj]);
		},
		
		/**
		 * Registers a listener to receive beacon notifications.
		 * First notification will actually contain the listener ID -- hold on to this for unregistering.
		 * It is up to you to do so when necessary, otherwise a crash can happen.
		 */
		registerListener: function(cb, failCB) {
			var fcb = wrapErrCB(failCB, "Failed to register listener");
			cordova.exec(cb, fcb, adapterName, "listen", []);
		},
		
		/**
		 * Start listening for beacons.
		 */
		start: function(cb, failCB) {
			var fcb = wrapErrCB(failCB, "Failed to start beacon monitoring");
			cordova.exec(cb, fcb, adapterName, "monitor", []);
		},
		
		/**
		 * Stop listening for beacons.
		 */
		stop: function(cb, failCB) {
			var fcb = wrapErrCB(failCB, "Failed to stop beacon monitoring");
			cordova.exec(cb, fcb, adapterName, "stopMonitor", []);
		},
		
		/**
		 * Unregisters a listener.
		 */
		unregisterListener: function(cb, failCB, id) {
			var fcb = wrapErrCB(failCB, "Failed to unregister listener");
			cordova.exec(cb, fcb, adapterName, "stopListen", [id]);
		},
		
		z: undefined // dummy end key
	};
	
	// Init
	// TODO: Make this configurable
	cordova.exec(onBeaconNotification, null, adapterName, "listen", []);
	
	module.exports = pub;
})(window);
