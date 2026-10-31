import { TestBed } from '@angular/core/testing';

import { BloodpressureService } from './bloodpressure.service';

describe('BloodpressureService', () => {
  let service: BloodpressureService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BloodpressureService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
