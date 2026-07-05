import { ComponentFixture, TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';

import { ChangePersonalInformationComponent } from './change-personal-information.component';

describe('ChangePersonalInformationComponent', () => {
  let component: ChangePersonalInformationComponent;
  let fixture: ComponentFixture<ChangePersonalInformationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangePersonalInformationComponent],
      providers: testProviders
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangePersonalInformationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
