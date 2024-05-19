import { Float, Int32 } from "react-native/Libraries/Types/CodegenTypes";
import {
  AccessibilityProps,
  ColorValue,
  FlexStyle,
  ImageRequireSource,
  LayoutChangeEvent,
  ShadowStyleIOS,
  StyleProp,
  TransformsStyle,
  ViewProps
} from "react-native";
import React from "react";

export type Source = {
  uri?: string
  headers?: { [key: string]: string }
  priority?: Priority
  cache?: CacheControl
}

export type Headers = { [key: string]: string };
export type Priority = "low" | "normal" | "high"
type CacheControl = "immutable" | "web" | "cacheOnly"

export type OnLoadEvent = Readonly<{
  width: Float,
  height: Float,
}>

export type OnProgressEvent = Readonly<{
  loaded: Int32,
  total: Int32,
}>

export interface onProgressEvent {
  nativeEvent: {
    loaded: number
    total: number
  };
}

export interface onLoadEvent {
  nativeEvent: {
    width: number
    height: number
  };
}

export interface ImageStyle extends FlexStyle, TransformsStyle, ShadowStyleIOS {
  backfaceVisibility?: "visible" | "hidden";
  borderBottomLeftRadius?: number;
  borderBottomRightRadius?: number;
  backgroundColor?: string;
  borderColor?: string;
  borderWidth?: number;
  borderRadius?: number;
  borderTopLeftRadius?: number;
  borderTopRightRadius?: number;
  overlayColor?: string;
  opacity?: number;
}

export interface FastImageProps extends AccessibilityProps, ViewProps {
  source?: ImageRequireSource | Source;
  defaultSource?: ImageRequireSource;
  resizeMode?: ResizeMode;

  onLoadStart?(): void;

  onProgress?(event: onProgressEvent): void;

  onLoad?(event: onLoadEvent): void;

  onError?(): void;

  onLoadEnd?(): void;

  /**
   * onLayout function
   *
   * Invoked on mount and layout changes with
   *
   * {nativeEvent: { layout: {x, y, width, height}}}.
   */
  onLayout?: (event: LayoutChangeEvent) => void;

  /**
   *
   * Style
   */
  style?: StyleProp<ImageStyle>;

  /**
   * TintColor
   *
   * If supplied, changes the color of all the non-transparent pixels to the given color.
   */

  tintColor?: ColorValue;

  /**
   * A unique identifier for this element to be used in UI Automation testing scripts.
   */
  testID?: string;

  /**
   * Render children within the image.
   */
  children?: React.ReactNode;
}

export type ResizeMode = "contain" | "cover" | "stretch" | "center"

export const resizeMode = {
  contain: "contain",
  cover: "cover",
  stretch: "stretch",
  center: "center"
} as const;

export const cacheControl = {
  // Ignore headers, use uri as cache key, fetch only if not in cache.
  immutable: "immutable",
  // Respect http headers, no aggressive caching.
  web: "web",
  // Only load from cache.
  cacheOnly: "cacheOnly"
} as const;

export const priority = {
  low: "low",
  normal: "normal",
  high: "high"
} as const;

export interface FastImageStaticProperties {
  resizeMode: typeof resizeMode;
  priority: typeof priority;
  cacheControl: typeof cacheControl;
  preload: (sources: Source[]) => void;
  clearMemoryCache: () => Promise<void>;
  clearDiskCache: () => Promise<void>;
}
