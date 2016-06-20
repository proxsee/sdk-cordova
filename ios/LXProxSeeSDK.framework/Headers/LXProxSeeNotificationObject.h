//
//  LXBeaconNotificationObject.h
//  LXProxSeeSDK
//
//  Created by ELie Melki on 4/23/15.
//  Copyright (c) 2015 Lixar. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LXProxSeeTagsChangeSet.h"

/*
 *  LXProxSeeNotificationObject
 *
 *  Discussion:
 *    LXProxSeeNotificationObject object passed when a tags change is detected.
 *    It contains the previous and current changeset
 */

@interface LXProxSeeNotificationObject : NSObject

- (id) initWithPreviousTagsChangeSet:(id<LXProxSeeTagsChangeSet>)previousTagsChangeSet currentTagsChangeSet:(id<LXProxSeeTagsChangeSet>)currentTagsChangeSet;

@property (nonatomic, strong, readonly) id<LXProxSeeTagsChangeSet> previousTagsChangeSet;
@property (nonatomic, strong, readonly) id<LXProxSeeTagsChangeSet> currentTagsChangeSet;

@end