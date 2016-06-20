//
//  ProxseeCordovaAdapter.h
//  Proxdova
//
//  Created by Christian Boisjoli on 2016-06-10.
//
//  This is the iOS Proxsee SDK <-> Cordova adapter.

#import <Foundation/Foundation.h>
//#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>

@interface ProxseeCordovaAdapter : CDVPlugin

- (NSString*)formatDate:(NSDate* )date;

@end
