import {ProjectDao} from './ProjectDao';
import * as loglevel from 'loglevel';
import {
    IProject,
    IProjectEventContent,
    LayerHelper,
    PredefinedLayerPreset,
    ProjectEvent,
    ProjectHelper,
} from 'abcmap-shared';
import {MongodbHelper} from '../lib/database/MongodbHelper';
import {AbstractService} from '../lib/AbstractService';
import EventEmitter = require('events');

export class ProjectService extends AbstractService {

    private logger = loglevel.getLogger('ProjectService');
    private emitter = new EventEmitter();

    constructor(private projectDao: ProjectDao) {
        super();
    }

    public postConstruct(): Promise<any> {
        return this.projectDao.connect();
    }

    public async findProject(projectId: string): Promise<IProject> {
        const project = await this.projectDao.findById(projectId);
        return MongodbHelper.withoutMongoId(project);
    }

    public updateProject(project: IProject): Promise<IProject> {
        return this.projectDao.update(project).then((insertResult) => {
            this.notifyProjectUpdated(project.id);
            return MongodbHelper.withoutMongoId(project);
        });
    }

    public async createNewProject(projectName: string): Promise<IProject> {
        if (!projectName) {
            return Promise.reject('Project name is mandatory');
        }
        const vectorLayer = LayerHelper.newVectorLayer();
        const newProject: IProject = {
            id: ProjectHelper.generateProjectId(),
            name: projectName,
            layers: [
                LayerHelper.newPredefinedLayer(PredefinedLayerPreset.OSM),
                vectorLayer,
            ],
            activeLayerId: vectorLayer.id,
        };
        await this.projectDao.insert(newProject);
        return MongodbHelper.withoutMongoId(newProject);
    }

    public notifyProjectUpdated(projectId: string) {
        const eventContent: IProjectEventContent = {name: ProjectEvent.PROJECT_UPDATED, projectId};
        this.emitter.emit(ProjectEvent.PROJECT_UPDATED, eventContent);
    }

    public getEmitter(): NodeJS.EventEmitter {
        return this.emitter;
    }

}
