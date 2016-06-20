//
//  ProxseeCordovaAdapter.m
//  Proxdova
//
//  Created by Christian Boisjoli on 2016-06-10.
//
//  This is the iOS Proxsee SDK <-> Cordova adapter.

#import "ProxseeCordovaAdapter.h"
#import <LXProxseeSDK/LXProxseeSDK.h>

#pragma mark ProxseeBroadcastProxy
@interface ProxseeBroadcastProxy : NSObject <LXProxSeeNotificationsHandler>
@property (nonatomic, assign) ProxseeCordovaAdapter* adapter;
@property (nonatomic, retain) CDVInvokedUrlCommand* command;

@end

@implementation ProxseeBroadcastProxy

- (void)didChangeTagsSet:(LXProxSeeNotificationObject *)proximityNotificationObject {
  NSDictionary* data = @{
                         @"previousTagsChangeSet": @{
                             @"tags": [proximityNotificationObject.previousTagsChangeSet.tags allObjects],
                             @"lastSeen": [self.adapter formatDate:proximityNotificationObject.previousTagsChangeSet.lastSeen],
                             },
                         @"currentTagsChangeSet": @{
                             @"tags": [proximityNotificationObject.currentTagsChangeSet.tags allObjects],
                             @"lastSeen": [self.adapter formatDate:proximityNotificationObject.currentTagsChangeSet.lastSeen],
                             },
                         };
	CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:data];
	pluginResult.keepCallback = @1;
  [self.adapter.commandDelegate sendPluginResult:pluginResult callbackId:self.command.callbackId];
}

- (void)listen {
  [self addProxSeeNotifcationObserver];
}

- (void)stopListening {
  [self removeProxSeeNotificationObserver];
}

@end

#pragma mark ProxseeCordovaAdapter
@implementation ProxseeCordovaAdapter
{
  NSMutableDictionary* _listeners;
	NSDateFormatter *_dateFormatter;
}

- (void)pluginInitialize {
  [super pluginInitialize];
	
	NSLocale* enUSPOSIXLocale = [NSLocale localeWithLocaleIdentifier:@"en_US_POSIX"];
	NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
	[dateFormatter setLocale:enUSPOSIXLocale];
	[dateFormatter setDateFormat:@"yyyy-MM-dd'T'HH:mm:ssZZZZZZ"];
	
  _listeners = [[NSMutableDictionary alloc] init];
	_dateFormatter = dateFormatter;
}

- (NSString*)formatDate:(NSDate* )date {
	return [_dateFormatter stringFromDate:date];
}

- (void)init:(CDVInvokedUrlCommand* )command {
	CDVPluginResult* pluginResult = nil;
	NSString* apiKey = [command.arguments objectAtIndex:0];

	if (apiKey != nil) {
		[LXProxSeeSDKManager launchProxSeeWithApiKey:apiKey];
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
	} else {
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"apiKey was null"];
	}

	[self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)listen:(CDVInvokedUrlCommand* )command {
  CDVPluginResult* pluginResult = nil;
  ProxseeBroadcastProxy* pbp = [[ProxseeBroadcastProxy alloc] init];
  NSTimeInterval time = [[NSDate date] timeIntervalSince1970];
  NSString* identString = [NSString stringWithFormat:@"%ld", (long)(time * 1000)];
  [_listeners setObject:pbp forKey:identString];
  [pbp setAdapter:self];
  [pbp setCommand:command];
  [pbp listen];
  
	pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:identString];
	pluginResult.keepCallback = @1;
  [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)stopListen:(CDVInvokedUrlCommand* )command {
  CDVPluginResult* pluginResult = nil;
  NSString* identString = [command.arguments objectAtIndex:0];
  ProxseeBroadcastProxy* pbp = [_listeners objectForKey:identString];
  if (pbp != nil) {
    [_listeners removeObjectForKey:identString];
    [pbp stopListening];
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
  } else {
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"No such listener ID"];
  }
  [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)monitor:(CDVInvokedUrlCommand* )command {
  [LXProxSeeSDKManager sharedInstance].isMonitoringEnabled = YES;
  [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

- (void)stopMonitor:(CDVInvokedUrlCommand* )command {
  [LXProxSeeSDKManager sharedInstance].isMonitoringEnabled = NO;
  [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

- (void)setData:(CDVInvokedUrlCommand* )command {
  NSDictionary *data = [command.arguments objectAtIndex:0];
  __weak ProxseeCordovaAdapter* _weakSelf = self;
  [[LXProxSeeSDKManager sharedInstance] updateMetadata:data completionHandler:^(BOOL success, NSError *error) {
    CDVPluginResult* pluginResult = nil;
    if (success)
      pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    else
      pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
    [_weakSelf.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
  }];
}

@end
