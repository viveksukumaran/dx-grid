import { Component, OnInit, ViewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import DataSource from 'devextreme/data/data_source';
import { DxDataGridModule, DxDataGridComponent } from 'devextreme-angular';

import { AuthenticationService } from '../services/authentication.service';
import { RedirectService } from '../services/redirect.service';
import { AssessmentService } from '../services/assessment.service';
import { Assessment } from '../model/assessment';
import { ValidationError } from '../model/validationError';
import { LocalDateTime } from '../model/local-date-time';
import { environment } from 'src/environments/environment';
import { PagedResponse } from '../model/paged-response';
import { BaseComponent } from '../model/base-component';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent extends BaseComponent implements OnInit {
    errorMessage: string = null;
    loadingCount: number = 0;
    validPageSizes: number[] = environment.pageSizes;
    selectedPageSize: number = this.validPageSizes[0];
    gridDataSource: DataSource;
    statusFilters = [{
        text: "scheduled",
        value: ["status", "=", "scheduled"]
    },
    {
        text: "in progress",
        value: ["status", "=", "in progress"]
    },
    {
        text: "hired",
        value: ["status", "=", "hired"]
    },
    {
        text: "failed",
        value: ["status", "=", "failed"]
    },
    {
        text: "rescheduled",
        value: ["status", "=", "rescheduled"]
    }];
    rwaStatusFilters = [
        {
            text: "in progress",
            value: ["rwaStatus", "=", "in progress"]
        },
        {
            text: "active",
            value: ["rwaStatus", "=", "active"]
        },
        {
            text: "inactive",
            value: ["rwaStatus", "=", "inactive"]
        },
        {
            text: "failed",
            value: ["rwaStatus", "=", "failed"]
        }
    ];
    validStatuses: string[] = this.statusFilters.map((val, index) => val.text);
    selectedStatusFilters: string[][] = this.statusFilters
        .filter(val => ['scheduled', 'in progress', 'rescheduled'].includes(val.text)).map((val, index) => val.value);
    selectedRwaStatusFilters: string[][] = this.rwaStatusFilters
        .filter(val => ['active', 'in progress', 'failed'].includes(val.text)).map((val, index) => val.value);

    @ViewChild(DxDataGridComponent) dataGrid: DxDataGridComponent;

    constructor(authService: AuthenticationService, private assessmentService: AssessmentService
        , redirect: RedirectService) {
        super(authService, redirect);
    }

    ngOnInit() {
        this.loadAssessments();
    }

    deProvision(index: number): void {
        this.init();
        let assessment: Assessment = this.gridDataSource.items()[index];
        this.assessmentService.deProvision(assessment.uuid).subscribe(
            result => {
                this.successRowUpdate(index, result);
            }, error => {
                this.failure("Failed to deprovision.", error);
            }
        );
    }

    prefixUsername(cellInfo): string {
        return 'mtqa.' + cellInfo.value;
    }

    customizeExcelCell(e) {
        if (e.gridCell.rowType === 'data') {
            if (e.gridCell.column.dataField == "status") {
                e.value = e.gridCell.data.status;
            }
        }
    }

    showRetryButton(index: number): boolean {
        let assessment: Assessment = this.gridDataSource.items()[index];
        let retry = assessment && 'failed' === assessment.rwaStatus && assessment.manualRetryEnabled;
        return retry;
    }

    retryProvisioning(index: number): void {
        this.init();
        let assessment: Assessment = this.gridDataSource.items()[index];
        this.assessmentService.retryProvisioning(assessment.uuid).subscribe(
            result => {
                this.successRowUpdate(index, result);
            }, error => {
                this.failure("Failed to provision.", error);
            }
        );
    }

    toDate(data: Assessment): Date {
        return data.created ? new Date(data.created.date.year, data.created.date.month - 1, data.created.date.day) : new Date();
    }

    private loadAssessments(): void {
        if (this.gridDataSource) {
            this.gridDataSource.dispose();
        }
        this.gridDataSource = new DataSource({
            update: (key, values) => {
                return this.assessmentService.updateStatus(key.uuid, values.status).toPromise();
            },
            load: (loadOptions) => {
                this.init();
                return this.assessmentService.loadAssessments(loadOptions)
                    .toPromise()
                    .then(result => {
                        this.successGridUpdate();
                        return {
                            data: result.items,
                            totalCount: result.totalElements
                        }
                    })
                    .catch(error => {
                        this.failure('Loading failed', error);
                        return {
                            data: [],
                            totalCount: 0
                        }
                    });
            }
        });
    }

    private init(): void {
        this.loadingCount++;
        this.errorMessage = null;
    }

    private successGridUpdate(): void {
        this.loadingCount--;
    }

    private successRowUpdate(index: number, result: Assessment): void {
        this.loadingCount--;
        this.gridDataSource.items()[index] = result;
        this.dataGrid.instance.refresh();
    }

    private failure(message: string, error: HttpErrorResponse): void {
        this.loadingCount--;
        this.errorMessage = this.consolidateError(message, error);
    }
}
