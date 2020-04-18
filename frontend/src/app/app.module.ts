import { FormsModule, ReactiveFormsModule } from '@angular/forms'; // <-- NgModel lives here
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { DxDataGridModule } from 'devextreme-angular';
import { CookieService } from 'ngx-cookie-service';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { environment } from 'src/environments/environment';
import { HttpService } from './services/http.service';
import { AuthenticationService } from './services/authentication.service';
import { RedirectService } from './services/redirect.service';
import { HttpWrapperService } from './services/httpWrapper.service';
import { AssessmentService } from './services/assessment.service';

import { Tab } from './tab/tab.component';
import { Tabs } from './tabs/tabs.component';
import { AssessmentComponent } from './assessment/assessment.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LoginComponent } from './login/login.component';
import { RegionMappingComponent } from './region-mapping/region-mapping.component';

export function HttpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http, environment.apiEndpoint, '.json');
}

const appRoutes: Routes = [
    { path: '', component: AppComponent, pathMatch: 'full' }
];

@NgModule({
    declarations: [
        AppComponent,
        Tabs,
        Tab,
        AssessmentComponent,
        DashboardComponent,
        LoginComponent,
        RegionMappingComponent
    ],
    imports: [
        NgbModule,
        BrowserModule,
        FormsModule,
        ReactiveFormsModule,
        AppRoutingModule,
        HttpClientModule,
        TranslateModule.forRoot({ loader: { provide: TranslateLoader, useFactory: HttpLoaderFactory, deps: [HttpClient] } }),
        RouterModule.forRoot(appRoutes),
        NgMultiSelectDropDownModule.forRoot(),
        DxDataGridModule
    ],
    providers: [
        HttpService,
        HttpWrapperService,
        AssessmentService,
        AuthenticationService,
        RedirectService,
        CookieService
    ],
    bootstrap: [AppComponent]
})
export class AppModule { }
