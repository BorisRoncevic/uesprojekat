import { ComponentFixture, TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';

import { VenuePageComponent } from './venue-page.component';

describe('VenuePageComponent', () => {
  let component: VenuePageComponent;
  let fixture: ComponentFixture<VenuePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VenuePageComponent],
      providers: testProviders
    })
    .compileComponents();

    fixture = TestBed.createComponent(VenuePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
