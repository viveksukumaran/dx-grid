import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { HttpWrapperService } from './httpWrapper.service';
import { ItemListResponse } from '../model/item-list-response';
import { Region } from '../model/region';

@Injectable({
    providedIn: 'root'
})
export class RegionService {
    constructor(private httpWrapperService: HttpWrapperService) { }

    public getAllRegions(): Observable<ItemListResponse<Region>> {
        return this.httpWrapperService.get(`regions`);
    }
}
