import { TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';

import { ReviewService } from '../review/review.service';

describe('ReviewService', () => {
  let service: ReviewService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: testProviders });
    service = TestBed.inject(ReviewService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
