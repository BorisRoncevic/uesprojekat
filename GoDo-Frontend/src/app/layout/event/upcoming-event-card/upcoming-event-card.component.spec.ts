import { ComponentFixture, TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';

import { UpcomingEventCardComponent } from './upcoming-event-card.component';

describe('UpcomingEventCardComponent', () => {
  let component: UpcomingEventCardComponent;
  let fixture: ComponentFixture<UpcomingEventCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpcomingEventCardComponent],
      providers: testProviders
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpcomingEventCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
