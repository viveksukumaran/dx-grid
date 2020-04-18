import { ValidationBase } from './validationBase';
import { ItemListResponse } from './item-list-response';

export interface PagedResponse<T> extends ItemListResponse<T> {
    totalElements: number;
}
