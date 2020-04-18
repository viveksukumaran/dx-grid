import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { HttpService } from './http.service';
import { RedirectService } from './redirect.service';
import { Credentials } from '../model/credentials';
import { environment } from 'src/environments/environment';

@Injectable()
export class HttpWrapperService {

    constructor(private httpService: HttpService, private redirect: RedirectService) {
    }

    post(path: string, body: any): Observable<any> {
        return this.httpService.post(`${environment.apiEndpoint}${path}`, body)
            .pipe(catchError(err => this.handleError(err)));
    }

    get(path: string): Observable<any> {
        return this.httpService.get(`${environment.apiEndpoint}${path}`)
            .pipe(catchError(err => this.handleError(err)));
    }

    put(path: string, body: any): Observable<any> {
        return this.httpService.put(`${environment.apiEndpoint}${path}`, body)
            .pipe(catchError(err => this.handleError(err)));
    }

    setCredentials(credentials: Credentials): void {
        this.httpService.addHeader('authorization', 'Basic ' + btoa(credentials.username + ':' + credentials.password));
    }

    clearCredentials(): void {
        this.httpService.addHeader('authorization', '');
    }

    private handleError(error: any): Observable<any> {
        if (error.status === 401) {
            this.redirect.login();
        }
        return throwError(error);
    }
}
