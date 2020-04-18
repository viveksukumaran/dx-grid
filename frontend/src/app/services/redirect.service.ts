import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable()
export class RedirectService {

    constructor(private router: Router) { }

    login(): void {
        this.router.navigateByUrl('/login');
    }
    
    dashboard(): void {
        this.router.navigateByUrl('/dashboard');
    }
    
    regions(): void {
        this.router.navigateByUrl('/regions');
    }
}
