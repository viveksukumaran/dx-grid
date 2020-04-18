import { HttpErrorResponse } from '@angular/common/http';

import { AuthenticationService } from '../services/authentication.service';
import { RedirectService } from '../services/redirect.service';

export abstract class BaseComponent {

    constructor(protected authService: AuthenticationService, protected redirect: RedirectService) {
        if (!this.authService.authenticated) {
            this.authService.authenticateWithSavedCredentials(null, () => this.redirect.login());            
        }
    }

    logout(): void {
        this.authService.logout();
    }

    consolidateError(message: string, error: HttpErrorResponse): string {
        if (!error) {
            return message;
        }
        if (!error.error) {
            return message + '-' + error.status + '-' + error.message;
        }
        if (!error.error.errors || error.error.errors.length === 0) {
            return message;
        }
        return message + '-' + error.error.errors[0].key + '-' + error.error.errors[0].error.message;
    }
}
