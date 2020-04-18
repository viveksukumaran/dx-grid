import { Component, OnInit } from '@angular/core';
import { FormControl, FormBuilder, FormArray, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CookieService } from 'ngx-cookie-service';

import { AuthenticationService } from '../services/authentication.service';
import { RedirectService } from '../services/redirect.service';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
    errorMessage: string;
    loginForm = this.fb.group({
        username: ['', Validators.required],
        password: ['', Validators.required],
        rememberMe: [false]
    });

    constructor(private fb: FormBuilder, private auth: AuthenticationService, private http: HttpClient
        , private redirect: RedirectService, protected cookieService: CookieService) { }

    ngOnInit() {
        if (this.cookieService.get(this.auth.USERNAME)) {
            this.loginForm.get('username').setValue(this.cookieService.get(this.auth.USERNAME));
            this.loginForm.get('password').setValue(this.cookieService.get(this.auth.PASSWORD));
            this.loginForm.get('rememberMe').setValue(true);
        }
    }

    login(): void {
        this.errorMessage = null;
        this.saveCredentials();
        this.auth.authenticate(this.loginForm.value, () => {
            this.redirect.dashboard();
        }, () => {
            this.errorMessage = 'Authentication failed';
        });
    }

    private saveCredentials(): void {
        if (this.loginForm.get('rememberMe').value) {
            this.cookieService.set(this.auth.USERNAME, this.loginForm.get('username').value);
            this.cookieService.set(this.auth.PASSWORD, this.loginForm.get('password').value);
            this.cookieService.set(this.auth.EXPIRED, 'false');
        } else {
            this.cookieService.delete(this.auth.USERNAME);
            this.cookieService.delete(this.auth.PASSWORD);
            this.cookieService.delete(this.auth.EXPIRED);
        }
    }
}
