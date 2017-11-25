import Vue from 'vue';
import Component from 'vue-class-component';
import {MainStore} from "../../lib/store/store";
import {IpcEvent} from "../../../api/ipc/IpcEvent";
import {Promise} from "es6-promise";
import {Evt} from "../../../api/ipc/IpcEventTypes";
import {Ats} from "../../lib/store/mutationsAndActions";
import {Logger} from "../../../api/dev/Logger";
import {Clients} from "../../lib/clients/Clients";

const logger = Logger.getLogger('StoreUpdaterComponent');

@Component({
    template: "<div></div>",
})
export default class StoreUpdaterComponent extends Vue {

    public $store: MainStore;
    public clients: Clients;

    public mounted() {
        this.registerHandlers();
    }

    private registerHandlers() {
        this.clients.projectClient.onProjectEvent((event: IpcEvent) => {

            logger.info('Receiving project event', event);

            if (event.type === Evt.PROJECT_NEW_CREATED) {
                this.$store.dispatch(Ats.PROJECT_UPDATE, event.data);
            }

            return Promise.resolve();
        });
    }
}
