//
//  RCTFastImageViewModule.m
//  RNFastImage
//
//  Created by 汪志 on 2024/5/19.
//

#import <Foundation/Foundation.h>
#import "RCTFastImageViewModule.h"
#import <SDWebImage/SDImageCache.h>
#import <SDWebImage/SDWebImagePrefetcher.h>
#import "FFFastImageSource.h"
#import <SDWebImage/SDWebImageDownloader.h>

@implementation RCTFastImageViewModule

RCT_EXPORT_MODULE(FastImageViewModule)

- (void)clearDiskCache:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject { 
    [SDImageCache.sharedImageCache clearDiskOnCompletion:^(){
        resolve(@YES);
    }];
}

- (void)clearMemoryCache:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject { 
    [SDImageCache.sharedImageCache clearMemory];
    resolve(@YES);
}

- (void)preload:(nonnull NSArray<FFFastImageSource *> *)sources resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject { 
    NSMutableArray *urls = [NSMutableArray arrayWithCapacity:sources.count];

    [sources enumerateObjectsUsingBlock:^(FFFastImageSource * _Nonnull source, NSUInteger idx, BOOL * _Nonnull stop) {
        [source.headers enumerateKeysAndObjectsUsingBlock:^(NSString *key, NSString* header, BOOL *stop) {
            [[SDWebImageDownloader sharedDownloader] setValue:header forHTTPHeaderField:key];
        }];
        [urls setObject:source.url atIndexedSubscript:idx];
    }];

    [[SDWebImagePrefetcher sharedImagePrefetcher] prefetchURLs:urls];
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeFastImageSpecJSI>(params);
}

@end
