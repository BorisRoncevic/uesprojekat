import { ComponentFixture, TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';

import { CompleteProfileDetailsFormComponent } from './complete-profile-details-form.component';

describe('CompleteProfileDetailsFormComponent', () => {
  let component: CompleteProfileDetailsFormComponent;
  let fixture: ComponentFixture<CompleteProfileDetailsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompleteProfileDetailsFormComponent],
      providers: testProviders
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompleteProfileDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
