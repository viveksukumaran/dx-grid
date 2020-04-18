import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { HttpWrapperService } from './httpWrapper.service';
import { Assessment } from '../model/assessment';
import { PagedResponse } from '../model/paged-response';

@Injectable()
export class AssessmentService {

    constructor(private httpWrapperService: HttpWrapperService) { }

    updateStatus(assessmentId: string, status: string): Observable<Assessment> {
        return this.httpWrapperService.put(`assessments/${assessmentId}`, status);
    }

    deProvision(assessmentId: string): Observable<Assessment> {
        return this.httpWrapperService.post(`assessments/${assessmentId}/deprovision`, {});
    }

    retryProvisioning(assessmentId: string): Observable<Assessment> {
        return this.httpWrapperService.post(`assessments/${assessmentId}/retry-provisioning`, {});
    }

    loadAssessments(loadOptions: any): Observable<PagedResponse<Assessment>> {
        return this.httpWrapperService.post(`assessments/load`, loadOptions);
    }
}
