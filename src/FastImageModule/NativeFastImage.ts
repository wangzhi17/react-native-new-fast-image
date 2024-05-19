import type { TurboModule } from "react-native";
import { TurboModuleRegistry } from "react-native";
import { Source } from "../type";

export interface Spec extends TurboModule {

  clearMemoryCache(): Promise<boolean>;

  clearDiskCache(): Promise<boolean>;

  preload(sources: Source[]): Promise<boolean>;

}

export default TurboModuleRegistry.get<Spec>("FastImageViewModule") as Spec;
