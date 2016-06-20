//
//  LXProximitySDKManager.h
//  LXProximitySDK
//
//  Created by ELie Melki on 1/24/15.
//  Copyright (c) 2015 Lixar. All rights reserved.
//

#import <Foundation/Foundation.h>

/*
 *  LXProxSeeSDKManager
 *
 *  Discussion:
 *    LXProxSeeSDKManager object is a singleton object which manage the SDK.
 */

@interface LXProxSeeSDKManager : NSObject

/*
 *  sharedInstance:
 *
 *  Discussion:
 *      returns SDK Manager shared instance.
 */

+ (LXProxSeeSDKManager *) sharedInstance;


/*
 *  launchProxSeeWithApiKey:
 *
 *  Discussion:
 *      This MUST be called on application: didFinishLaunchingWithOptions:.
 *      In order for SDK to work your need pass an API key which you obtain from the server.
 */

+ (void) launchProxSeeWithApiKey:(NSString *)apiKey;

/*
 *  updateMetadata:
 *
 *  Discussion:
 *      Metadata are used to add more descriptive information per device which can be used later for reporting.
 *      Updating metadata with the same data more than once will only make one Api call. 
 *      In other terms if you keep executing the above providing the same data it will hit the server once. 
 *      Only when you change the date it hit the server.
 *      No Metadata is send if proxsee is turned off.
 */

- (void) updateMetadata:(NSDictionary *)object completionHandler:(void (^)(BOOL success, NSError *error))completionHandler;


/*
 *  isProxSeeEnabled
 *
 *  Discussion:
 *      Determines whether Proxsee is turned on or off.
 *      Turning on and off ProxSee SDK.
 */

@property (nonatomic) BOOL isMonitoringEnabled;


@end



