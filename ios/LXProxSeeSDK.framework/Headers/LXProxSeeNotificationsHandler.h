//
//  LXProxSeeNotificationHandler.h
//  LXProxSeeSDK
//
//  Created by ELie Melki on 2/12/15.
//  Copyright (c) 2015 Lixar. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LXProxSeeNotificationObject.h"

/*
 *  LXProxSeeNotificationsHandler
 *
 *  Discussion:
 *      Notification handler protocol
 */

@protocol LXProxSeeNotificationsHandler <NSObject>

@optional

/*
 *  didChangeTagsSet:
 *
 *  Discussion:
 *      Fire when tags changes.
 */
- (void) didChangeTagsSet:(LXProxSeeNotificationObject *)proximityNotificationObject;

@end

/*
 *  NSObject(LXProxSeeNotificationObserver)
 *
 *  Discussion:
 *      NSObject category that allow objects to add and remove observer without the need to know 
 *      the notification name and parse the notification object.
 *      Later we should make sure this category conforms to LXProxSeeNotificationsHandler so 
 *      these methods are only availble for objects that implements the protocol
 */
@interface NSObject(LXProxSeeNotificationObserver)<LXProxSeeNotificationsHandler>

/*
 *  addProxSeeNotifcationObserver
 *
 *  Discussion:
 *      Adds a notification observer. Make sure it is called once so we dont up calling the hanlder more than once.
 */
- (void) addProxSeeNotifcationObserver;

/*
 *  removeProxSeeNotificationObserver
 *
 *  Discussion:
 *      remove notification observer. Make sure you call this method before your observer object gets deallocated or your application will crash.
 */
- (void) removeProxSeeNotificationObserver;

@end