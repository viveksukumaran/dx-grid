import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { AssessmentComponent } from './assessment/assessment.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LoginComponent } from './login/login.component';
import { RegionMappingComponent } from './region-mapping/region-mapping.component';

const routes: Routes = [{ path: 'assessment', component: AssessmentComponent },
{ path: 'dashboard', component: DashboardComponent },
{ path: 'login', component: LoginComponent },
{ path: 'regions', component: RegionMappingComponent },
{ path: '**', redirectTo: '/assessment', pathMatch: 'full' }];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule { }
