import { TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';

import { VenueService } from './venue.service';

describe('VenueService', () => {
  let service: VenueService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: testProviders });
    service = TestBed.inject(VenueService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
