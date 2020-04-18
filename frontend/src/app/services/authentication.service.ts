import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';

import { Credentials } from '../model/credentials';
import { HttpWrapperService } from './httpWrapper.service';
import { RedirectService } from './redirect.service';

@Injectable()
export class AuthenticationService {
    USERNAME: string = 'rwa_username';
    PASSWORD: string = 'rwa_password';
    EXPIRED: string = 'rwa_expired';
    authenticated = false;

    constructor(private httpWrapperService: HttpWrapperService, private redirect: RedirectService, protected cookieService: CookieService) { }

    authenticateWithSavedCredentials(successCallback: () => void, failureCallback: () => void) : void {
        if(this.cookieService.get(this.USERNAME) && this.cookieService.get(this.EXPIRED) === 'false') {
            const credentials: Credentials = {username: this.cookieService.get(this.USERNAME), password: this.cookieService.get(this.PASSWORD)}; 
            this.authenticate(credentials, successCallback, failureCallback);
        } else {
            this.failure(failureCallback);
        }
    }

    authenticate(credentials: Credentials, successCallback: () => void, failureCallback: () => void): void {
        this.httpWrapperService.setCredentials(credentials);
        this.httpWrapperService.get('users').subscribe(response => {
            this.success(successCallback);
        }, error => {
            this.failure(failureCallback);
        });
    }

    logout(): void {
        this.httpWrapperService.clearCredentials();
        this.authenticated = false;
        this.cookieService.set(this.EXPIRED,'true');
        this.redirect.login();
    }

    private success(callback: () => void): void {
        this.authenticated = true;
        if(callback) {
            callback();    
        }
    }

    private failure(callback: () => void): void {
        this.authenticated = false;
        if(callback) {
            callback();    
        }
    }
}
