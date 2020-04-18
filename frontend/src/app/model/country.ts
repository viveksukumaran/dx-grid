import { ValidationBase } from './validationBase';

export interface Country extends ValidationBase {
    code: string;
    name: string;
}
