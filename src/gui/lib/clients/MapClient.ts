import {Ipc, IpcHandler} from '../../../api/ipc/Ipc';
import {TileLayer} from '../../../api/entities/layers/TileLayer';
import {EntitiesUtils} from '../../../api/utils/EntitiesUtils';
import {IpcSubject} from "../../../api/ipc/IpcSubject";
import {handleRejection} from "./clientUtils";
import * as Promise from 'bluebird';

const eu = new EntitiesUtils();

export class MapClient {

    private ipc: Ipc;

    constructor(ipc: Ipc) {
        this.ipc = ipc;
    }

    public onMapEvent(handler: IpcHandler): void {
        return this.ipc.listen(IpcSubject.MAP_EVENTS_BUS, handler);
    }

    public getDefaultWmsLayers(): Promise<TileLayer[]> {
        return this.ipc.send(IpcSubject.MAP_GET_WMS_DEFAULT_LAYERS).catch(handleRejection);
    }

    public importFiles(files: File[]) {
        return this.ipc.send(IpcSubject.MAP_IMPORT_FILES, {data: files}).catch(handleRejection);
    }
}
