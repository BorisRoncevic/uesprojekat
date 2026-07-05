import { ComponentFixture, TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';

import { EventCardComponent } from './event-card.component';
import { EventOverviewDto } from '../../../models/event/EventOverviewDto';

describe('EventCardComponent', () => {
  let component: EventCardComponent;
  let fixture: ComponentFixture<EventCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventCardComponent],
      providers: testProviders
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventCardComponent);
    component = fixture.componentInstance;
    component.event = {
      id: 1,
      name: 'Test event',
      description: 'Test description',
      date: '2026-06-26',
      type: 'CONCERT',
      price: 0,
      imagePath: 'data:image/gif;base64,R0lGODlhAQABAAAAACw=',
      recurrent: false,
      venueId: 1,
    } satisfies EventOverviewDto;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
