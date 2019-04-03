export enum MapLayerType {
    Raster = 'Raster',
    Vector = 'Vector',
    Predefined = 'Predefined',
}

export interface IMapLayer {
    id: string;
    name: string;
    type: MapLayerType;
}

export interface IVectorLayer extends IMapLayer {
    type: MapLayerType.Vector;
    featureCollection: string;
}

export interface IRasterLayer extends IMapLayer {
    type: MapLayerType.Raster;
    url: string;
}

export enum PredefinedLayerPreset {
    OSM = 'OSM'
}

export interface IPredefinedLayer extends IMapLayer {
    type: MapLayerType.Predefined;
    preset: PredefinedLayerPreset;
}
