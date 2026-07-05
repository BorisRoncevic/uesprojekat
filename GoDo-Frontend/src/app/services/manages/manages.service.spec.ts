import { TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';

import { ManagesService } from './manages.service';

describe('ManagesService', () => {
  let service: ManagesService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: testProviders });
    service = TestBed.inject(ManagesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
