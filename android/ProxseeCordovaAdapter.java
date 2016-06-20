package io.proxsee.android;

import com.google.gson.*;
import org.apache.cordova.*;
import org.json.*;
import io.proxsee.sdk.*;
import io.proxsee.sdk.model.*;
import io.proxsee.sdk.broadcast.*;

import android.*;
import android.content.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.*;
import android.util.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * This is the iOS Proxsee SDK <-> Cordova adapter.
 * Created by cboisjoli on 2016-06-07.
 */
public class ProxseeCordovaAdapter extends CordovaPlugin {
	// Reference: https://github.com/apache/cordova-android/blob/master/framework/src/org/apache/cordova/CordovaPlugin.java
	private static final String TAG = ProxseeCordovaAdapter.class.getSimpleName();

	protected Context ctx;
	protected HashMap<Long, PSCBroadcastProxy> listeners;

	@Override
	protected void pluginInitialize() {
		super.pluginInitialize();
		listeners = new HashMap<Long, PSCBroadcastProxy>();
		ctx = cordova.getActivity().getApplicationContext();
		// FUTURE: We could read the API key from a metadata file and do the init automatically..
	}

	// TODO: Ideally we need to give an interface for the developer to prompt the user for location access:
	// android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION
	// a.k.a. android.permission.ACCESS_FINE_LOCATION, android.permission.ACCESS_COARSE_LOCATION
	// See https://cordova.apache.org/docs/en/latest/guide/platforms/android/plugin.html#android-permissions

	@Override
	public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
		// Note: This function does not execute in the main thread. See: https://cordova.apache.org/docs/en/latest/guide/platforms/android/plugin.html#threading
		try {
			if ("init".equals(action)) {
				String apiKey = args.getString(0);
				ProxSeeSDKManager.initialize(ctx, apiKey);
				// If we hit no exception at this point, we can assume everything's OK as .initialize() returns nothing.
				callbackContext.success();
				return true;
			} else if ("setData".equals(action)) {
				JSONObject obj = args.getJSONObject(0);
				HashMap<String, Object> data = convertJSONObjectToMap(obj);
				ProxSeeSDKManager.getInstance().updateMetadata(data, new ProxSeeSDKManager.CompletionHandler() {
					@Override
					public void onUpdateCompleted(boolean success, Exception error) {
						if (success)
							callbackContext.success();
						else {
							Log.e(TAG, "Error setting metadata: ", error);
							callbackContext.error(error.getMessage());
						}
					}
				});
				PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
				result.setKeepCallback(true);
				return true;
			} else if ("listen".equals(action)) {
				long id = System.currentTimeMillis();
				PSCBroadcastProxy listener = new PSCBroadcastProxy(ctx, callbackContext);
				listeners.put(id, listener);
				listener.register(id);
				return true;
			} else if ("stopListen".equals(action)) {
				long id = args.getLong(0);
				if (listeners.containsKey(id)) {
					listeners.remove(id).unregister();
					callbackContext.success();
				} else
					callbackContext.error("No such listener " + id);
				return true;
			} else if ("monitor".equals(action)) {
				ProxSeeSDKManager.getInstance().start();
				// If we hit no exception at this point, we can assume everything's OK as .start() returns nothing.
				callbackContext.success();
				return true;
			} else if ("stopMonitor".equals(action)) {
				ProxSeeSDKManager.getInstance().stop();
				// If we hit no exception at this point, we can assume everything's OK as .stop() returns nothing.
				callbackContext.success();
				return true;
			}
			return false; // Returning false results in a "MethodNotFound" error.
		} catch (Exception e) {
			Log.e(TAG, "Exception processing Cordova request: ", e);
			throw e;
		}
	}

	protected HashMap<String, Object> convertJSONObjectToMap(final JSONObject obj) throws JSONException {
		HashMap<String, Object> result = new HashMap<>(obj.length());
		Iterator<String> awkwardIterator = obj.keys();
		while (awkwardIterator.hasNext()) {
			String key = awkwardIterator.next();
			result.put(key, obj.get(key));
		}
		return result;
	}

	protected static class PSCBroadcastProxy extends ProxSeeBroadcastReceiver {
		protected final Context ctx;
		protected final CallbackContext callbackContext;
		protected final Gson gson;

		protected PSCBroadcastProxy(Context ctx, CallbackContext callbackContext) {
			this.ctx = ctx.getApplicationContext();
			this.callbackContext = callbackContext;
			// RFC2822 date format. ISO 8601 is also an option, but is harder to parse in Java should we need to.
			this.gson = new GsonBuilder().setDateFormat("EEE, dd MMM yyyy HH:mm:ss Z").create();
		}

		@Override
		public void didChangeTagsSet(BeaconNotificationObject beaconNotificationObject) {
			String data = gson.toJson(beaconNotificationObject);
				// Temporary work-around for current version
				.replace("\"mCurrentTagsChangedSet\"", "\"currentTagsChangeSet\"")
				.replace("\"mPreviousTagsChangedSet\"", "\"previousTagsChangeSet\"")
				.replace("\"mLastSeen\"", "\"lastSeen\"")
				.replace("\"mSet\"", "\"tags\"");
			Log.d(TAG, "Got beacon notification: " + data);
			PluginResult result = new PluginResult(PluginResult.Status.OK, data);
			result.setKeepCallback(true);

			if (!callbackContext.isFinished()) {
				callbackContext.sendPluginResult(result);
			}
		}

		public void register(long id) {
			if (!callbackContext.isFinished()) {
				ctx.registerReceiver(this, new IntentFilter(ProxSeeBroadcaster.TAGS_CHANGED_ACTION));

				PluginResult result = new PluginResult(PluginResult.Status.OK, id);
				result.setKeepCallback(true);

				callbackContext.sendPluginResult(result);
			} else {
				throw new IllegalStateException("Can't register without a callback context!");
			}
		}

		public void unregister() {
			ctx.unregisterReceiver(this);

			if (!callbackContext.isFinished()) {
				callbackContext.success();
			}
		}
	}

	// FUTURE: We could make it easier for JS implementations to be safe, e.g. automatically removing/re-adding listers onPause()/onReset()/etc.
}
