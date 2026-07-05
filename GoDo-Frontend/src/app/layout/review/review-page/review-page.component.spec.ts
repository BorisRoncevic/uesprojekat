import { ComponentFixture, TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';

import { ReviewPageComponent } from './review-page.component';

describe('ReviewPageComponent', () => {
  let component: ReviewPageComponent;
  let fixture: ComponentFixture<ReviewPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewPageComponent],
      providers: testProviders
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
