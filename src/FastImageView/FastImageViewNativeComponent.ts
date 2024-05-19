import codegenNativeComponent from "react-native/Libraries/Utilities/codegenNativeComponent";
import codegenNativeCommands from "react-native/Libraries/Utilities/codegenNativeCommands";
import type { ViewProps, ColorValue, HostComponent } from "react-native";
import type { DirectEventHandler, Float, Int32, WithDefault } from "react-native/Libraries/Types/CodegenTypes";

type OnLoadEvent = Readonly<{
  width: Float,
  height: Float,
}>

type OnProgressEvent = Readonly<{
  loaded: Int32,
  total: Int32,
}>
type Headers = ReadonlyArray<Readonly<{ name: string, value: string }>>;
type Priority = WithDefault<"low" | "normal" | "high", "normal">
type CacheControl = WithDefault<"immutable" | "web" | "cacheOnly", "web">

type FastImageSource = Readonly<{
  uri?: string,
  headers?: Headers,
  priority?: Priority,
  cache?: CacheControl,
}>

interface NativeProps extends ViewProps {
  onFastImageError?: DirectEventHandler<Readonly<{}>>,
  onFastImageLoad?: DirectEventHandler<OnLoadEvent>,
  onFastImageLoadEnd?: DirectEventHandler<Readonly<{}>>,
  onFastImageLoadStart?: DirectEventHandler<Readonly<{}>>,
  onFastImageProgress?: DirectEventHandler<OnProgressEvent>,
  source?: FastImageSource,
  defaultSource?: string | null,
  resizeMode?: WithDefault<"contain" | "cover" | "stretch" | "center", "cover">,
  tintColor?: ColorValue,
}

export type FastImageType = HostComponent<NativeProps>;


export interface FastImageCommands {

}

export const Commands: FastImageCommands = codegenNativeCommands<FastImageCommands>({
  supportedCommands: []
});

export default codegenNativeComponent<NativeProps>("FastImageView") as HostComponent<NativeProps>;
