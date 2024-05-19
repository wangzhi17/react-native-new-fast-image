import React from "react";
import FastImageView from "./src/FastImageView";
import FastImageModule from "./src/FastImageModule";
import { resizeMode, cacheControl, priority, Source, FastImageProps, FastImageStaticProperties } from "./src/type";

const FastImage: React.ComponentType<FastImageProps> & FastImageStaticProperties = FastImageView as any;


FastImage.resizeMode = resizeMode;
FastImage.cacheControl = cacheControl;

FastImage.priority = priority;

FastImage.clearMemoryCache = () => FastImageModule.clearMemoryCache().then();

FastImage.clearDiskCache = () => FastImageModule.clearDiskCache().then();
FastImage.preload = (sources: Source[]) => FastImageModule.preload(sources).then();

export  {FastImageModule}
export default FastImage;
