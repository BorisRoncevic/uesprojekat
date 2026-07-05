import { ComponentFixture, TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';

import { EventPageVenueCardComponent } from './event-page-venue-card.component';

describe('EventPageVenueCardComponent', () => {
  let component: EventPageVenueCardComponent;
  let fixture: ComponentFixture<EventPageVenueCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventPageVenueCardComponent],
      providers: testProviders
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventPageVenueCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
