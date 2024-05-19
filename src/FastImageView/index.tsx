import React, { ComponentRef, forwardRef, useRef } from "react";
import {
  View,
  Image,
  StyleSheet,
  ImageRequireSource,
  Platform,
  ImageResolvedAssetSource
} from "react-native";
import RCTFastImageView, { FastImageType } from "./FastImageViewNativeComponent";
import { FastImageProps } from "../type";

const resolveDefaultSource = (defaultSource?: ImageRequireSource): string | number | null => {
  if (!defaultSource) {
    return null
  }
  if (Platform.OS === 'android') {
    // Android receives a URI string, and resolves into a Drawable using RN's methods.
    const resolved = Image.resolveAssetSource(
      defaultSource as ImageRequireSource,
    )

    if (resolved) {
      return resolved.uri
    }

    return null
  }
  // iOS or other number mapped assets
  // In iOS the number is passed, and bridged automatically into a UIImage
  return defaultSource
}

const FastImageView = forwardRef<{}, FastImageProps>(({
                                                        source,
                                                        defaultSource,
                                                        tintColor,
                                                        onLoadStart,
                                                        onProgress,
                                                        onLoad,
                                                        onError,
                                                        onLoadEnd,
                                                        style,
                                                        children,
                                                        resizeMode = "cover",
                                                        ...props
                                                      }, ref) => {
  // this type differs based on the `source` prop passed
  const resolvedSource = Image.resolveAssetSource(source as any) as ImageResolvedAssetSource & { headers: any };
  if (resolvedSource?.headers) {
    // we do it like that to trick codegen
    const headersArray: { name: string, value: string }[] = [];
    Object.keys(resolvedSource.headers).forEach(key => {
      headersArray.push({ name: key, value: resolvedSource.headers[key] });
    });
    resolvedSource.headers = headersArray;
  }
  const resolvedDefaultSource = resolveDefaultSource(defaultSource);
  const ezvizPlayerViewRef = useRef<ComponentRef<FastImageType> | null>(null);
  return (
    <View style={[styles.imageContainer, style]}>
      <RCTFastImageView
        ref={ezvizPlayerViewRef}
        {...props}
        tintColor={tintColor}
        style={StyleSheet.absoluteFill}
        source={resolvedSource}
        defaultSource={resolvedDefaultSource}
        onFastImageLoadStart={onLoadStart}
        onFastImageProgress={onProgress}
        onFastImageLoad={onLoad}
        onFastImageError={onError}
        onFastImageLoadEnd={onLoadEnd}
        resizeMode={resizeMode}
      />
      {children}
    </View>
  );
});



const styles = StyleSheet.create({
  imageContainer: {
    overflow: "hidden"
  }
});

export default FastImageView;
