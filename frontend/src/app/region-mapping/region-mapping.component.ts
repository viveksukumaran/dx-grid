import { Component } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import DataSource from 'devextreme/data/data_source';

import { environment } from 'src/environments/environment';
import { AuthenticationService } from '../services/authentication.service';
import { RedirectService } from '../services/redirect.service';
import { RegionService } from '../services/region.service';
import { BaseComponent } from '../model/base-component';
import { CountryService } from '../services/country.service';

@Component({
    selector: 'app-region-mapping',
    templateUrl: './region-mapping.component.html',
    styleUrls: ['./region-mapping.component.css']
})
export class RegionMappingComponent extends BaseComponent {
    validPageSizes: number[] = environment.pageSizes;
    selectedPageSize: number = this.validPageSizes[0];
    errorMessage: string;
    gridDataSource: DataSource;
    regions: string[];
    loadingCount: number = 0;

    constructor(authService: AuthenticationService, private regionService: RegionService
        , redirect: RedirectService, private countryService: CountryService) { super(authService, redirect); }

    ngOnInit() {
        this.loadingCount++;
        this.regionService.getAllRegions().subscribe(result => {
            this.loadingCount--;
            this.regions = result.items.map(item => item.name);
            this.loadMappings();
        }, error => {
            this.loadingCount--;
            this.failure('Failed to load regions', error);
        });
    }

    private loadMappings(): void {
        this.gridDataSource = new DataSource({
            update: (key, values) => {
                return this.countryService.updateMapping([{
                    country: key.country, region: values.region, valid: true, errors: null
                }]).toPromise();
            },
            load: (loadOptions) => {
                this.init();
                return this.countryService.getMappings(loadOptions)
                    .toPromise()
                    .then(result => {
                        return {
                            data: result.items,
                            totalCount: result.totalElements
                        };
                    })
                    .catch(error => {
                        this.failure('Loading failed', error);
                        return {
                            data: [],
                            totalCount: 0
                        };
                    });
            }
        });
    }

    private init(): void {
        this.errorMessage = null;
    }

    private failure(message: string, error: HttpErrorResponse): void {
        this.errorMessage = this.consolidateError(message, error);
    }
}
