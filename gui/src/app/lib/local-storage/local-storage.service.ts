import { Injectable } from '@angular/core';

export enum LSKey {
  CURRENT_PROJECT_ID = 'ABC_CURRENT_PROJECT',
  USER_TOKEN = 'ABC_USER_TOKEN',
  USERNAME = 'ABC_USERNAME',
}

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {

  constructor() { }

  public save(key: LSKey, value: string): void {
    localStorage.setItem(key, value);
  }

  public get(key: LSKey): string | null {
    return localStorage.getItem(key);
  }

}
