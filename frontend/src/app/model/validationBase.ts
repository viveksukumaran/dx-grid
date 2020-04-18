import { ValidationError } from './validationError';

export interface ValidationBase {
    valid: boolean;
    errors: ValidationError[];
  };