import type { TurboModule } from "react-native";
import { TurboModuleRegistry } from "react-native";
import { Source } from "../type";

export interface Spec extends TurboModule {

  clearMemoryCache(): void;

  clearDiskCache(): void;

  preload(sources: Source[]): void;

}

export default TurboModuleRegistry.get<Spec>("FastImageViewModule") as Spec;
