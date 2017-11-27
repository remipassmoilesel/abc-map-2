import {EventType} from "./IpcEventTypes";

export interface IpcEvent {
    type?: EventType;
    data?: any;
}

export class IpcEventImpl implements IpcEvent {
    public type: EventType;
    public data: any;

    constructor(type, data?) {
        this.type = type;
        this.data = data;
    }

}