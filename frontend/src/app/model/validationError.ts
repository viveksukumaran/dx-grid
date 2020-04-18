import { BaseError } from './baseError';

export interface ValidationError {
    key: string;
    error: BaseError;
  }