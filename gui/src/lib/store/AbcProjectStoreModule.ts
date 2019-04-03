import {IProject} from '../../../../shared/dist';
import {Module} from 'vuex';
import {IRootState} from '@/lib/store/store';

export interface IProjectState {
    currentProject: IProject | null;
}

export enum ProjectMutations {
    PROJECT_UPDATE = 'PROJECT_UPDATE',
}

export enum ProjectActions {
    PROJECT_UPDATE = 'PROJECT_UPDATE',
}

export const abcProjectStoreModule: Module<IProjectState, IRootState> = {
    state: {
        currentProject: null,
    },
    getters: {
        currentProject: (state, getters, rootState, rootGetters) => {
            return state.currentProject;
        },
        projectName: (state: IProjectState) => {
            if (state.currentProject) {
                return state.currentProject.name;
            } else {
                return 'Pas de projet courant';
            }
        },
        projectLayers: (state: IProjectState) => {
            if (state.currentProject) {
                return state.currentProject.layers;
            } else {
                return [];
            }
        },
    },
    actions: {
        [ProjectActions.PROJECT_UPDATE]: (context: any, project: IProject) => {
            context.commit(ProjectMutations.PROJECT_UPDATE, {project});
        },
    },
    mutations: {
        [ProjectMutations.PROJECT_UPDATE]: (state: IProjectState, payload: { project: IProject }) => {
            state.currentProject = payload.project;
        },
    },
};
