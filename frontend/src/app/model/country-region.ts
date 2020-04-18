import { ValidationBase } from './validationBase';
import { Country } from './country';
import { Region } from './region';

export interface CountryRegion extends ValidationBase {
    country: Country;
    region: Region;
}
