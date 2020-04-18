import { ValidationBase } from './validationBase';

export interface ItemListResponse<T> extends ValidationBase {
    items: T[];
}
