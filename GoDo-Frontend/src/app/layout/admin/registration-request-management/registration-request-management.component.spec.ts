import { ComponentFixture, TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';

import { RegistrationRequestManagementComponent } from './registration-request-management.component';

describe('RegistrationRequestManagementComponent', () => {
  let component: RegistrationRequestManagementComponent;
  let fixture: ComponentFixture<RegistrationRequestManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegistrationRequestManagementComponent],
      providers: testProviders
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegistrationRequestManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
