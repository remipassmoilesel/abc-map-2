import {Injectable} from '@angular/core';
import {AuthenticationClient} from './AuthenticationClient';
import {ILoginRequest, IUserCreationRequest} from 'abcmap-shared';
import {ToastService} from '../notifications/toast.service';
import {tap} from 'rxjs/operators';
import {LocalStorageService, LSKey} from '../local-storage/local-storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor(private client: AuthenticationClient,
              private localStorage: LocalStorageService,
              private toasts: ToastService) {
  }

  public getToken(): string | null {
    return this.localStorage.get(LSKey.USER_TOKEN);
  }

  public setToken(token: string): void {
    this.localStorage.save(LSKey.USER_TOKEN, token);
  }

  public registerUser(request: IUserCreationRequest) {
    return this.client.registerUser(request)
      .pipe(
        tap(res => this.toasts.info('Vous êtes inscrit !'),
          err => this.toasts.error('Erreur lors de l\'inscription, veuillez réessayer plus tard !'))
      );
  }

  public login(request: ILoginRequest) {
    return this.client.login(request)
      .pipe(
        tap(res => {
            this.toasts.info('Vous êtes connecté !');
            this.setToken(res.token);
          },
          err => this.toasts.error('Identifiants incorrects !'))
      );
  }
}