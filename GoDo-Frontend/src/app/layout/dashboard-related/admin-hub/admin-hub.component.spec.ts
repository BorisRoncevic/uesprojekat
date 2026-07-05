import { ComponentFixture, TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';

import { AdminHubComponent } from './admin-hub.component';

describe('AdminHubComponent', () => {
  let component: AdminHubComponent;
  let fixture: ComponentFixture<AdminHubComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminHubComponent],
      providers: testProviders
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminHubComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
