import { ValidationBase } from './validationBase';
import { LocalDateTime } from './local-date-time';
import { Region } from './region';

export interface Assessment extends ValidationBase {
    candidateName: string;
    candidateEmail: string;
    uuid: string;
    salesforceOrderId: string;
    provisionJobLink: string;
    provisioningJobId: string;
    manualRetryEnabled: boolean;
    rwaStatus: string;
    status: string;
    created: LocalDateTime;
    username: string;
    provisioningRegion: Region;
}
