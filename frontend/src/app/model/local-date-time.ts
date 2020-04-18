import { ValidationBase } from './validationBase';

export interface LocalDateTime extends ValidationBase {
    date: {year:number, month:number, day:number};
    time: {hour:number, minute:number, second:number, nano:number};
}
