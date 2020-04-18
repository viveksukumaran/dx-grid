import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError} from 'rxjs/operators';

@Injectable()
export class HttpService {
  private headers = new HttpHeaders();

  constructor(private httpClient: HttpClient) {
  }

  addHeader(key: string, value: string): void {
    this.headers = this.headers.set(key, value);
  }

  post(path: string, body: any): Observable<any> {
    return this.httpClient.post(path, body, {headers: this.headers}).pipe(
      catchError((error) => {
        throw error;
      })
    );
  }

  put(path: string, body: any): Observable<any> {
    return this.httpClient.put(path, body, {headers: this.headers}).pipe(
      catchError((error) => {
        throw error;
      })
    );
  }

  patch(path: string, body: any): Observable<any> {
    return this.httpClient.patch(path, body, {headers: this.headers}).pipe(
      catchError((error) => {
        throw error;
      })
    );
  }

  delete(path: string): Observable<any> {
    return this.httpClient.delete(path, {headers: this.headers}).pipe(
      catchError((error) => {
        throw error;
      })
    );
  }

  get(path: string): Observable<any> {
    return this.httpClient.get(path, {headers: this.headers}).pipe(
      catchError((error) => {
        throw error;
      })
    );
  }
}
