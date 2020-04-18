import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { HttpWrapperService } from './httpWrapper.service';
import { PagedResponse } from '../model/paged-response';
import { CountryRegion } from '../model/country-region';
import { ItemListResponse } from '../model/item-list-response';

@Injectable({
    providedIn: 'root'
})
export class CountryService {
    constructor(private httpWrapperService: HttpWrapperService) { }

    public getMappings(loadOptions: any): Observable<PagedResponse<CountryRegion>> {
        return this.httpWrapperService.post(`countries/regions`, loadOptions);
    }

    public updateMapping(mappings: CountryRegion[]): Observable<ItemListResponse<CountryRegion>> {
        return this.httpWrapperService.put(`countries/regions`, mappings);
    }
}
