import React from "react";
import FastImageView from "./src/FastImageView";
import FastImageModule from "./src/FastImageModule";
import { resizeMode, cacheControl, priority, Source, FastImageProps, FastImageStaticProperties } from "./src/type";

const FastImage: React.ComponentType<FastImageProps> & FastImageStaticProperties = FastImageView as any;


FastImage.resizeMode = resizeMode;
FastImage.cacheControl = cacheControl;

FastImage.priority = priority;

FastImage.clearMemoryCache = () => FastImageModule.clearMemoryCache();

FastImage.clearDiskCache = () => FastImageModule.clearDiskCache();
FastImage.preload = (sources: Source[]) => FastImageModule.preload(sources);

export  {FastImageModule}
export default FastImage;
