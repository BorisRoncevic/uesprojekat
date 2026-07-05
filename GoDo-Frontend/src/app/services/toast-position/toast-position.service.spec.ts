import { TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';
import { ToastPositionService } from '../toast-position/toast-position.service';

describe('ToastPositionService', () => {
  let service: ToastPositionService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: testProviders });
    service = TestBed.inject(ToastPositionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
