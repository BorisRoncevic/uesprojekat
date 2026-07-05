import { TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';

import { AccountRequestService } from './account-request.service';

describe('AccountRequestService', () => {
  let service: AccountRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: testProviders });
    service = TestBed.inject(AccountRequestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
