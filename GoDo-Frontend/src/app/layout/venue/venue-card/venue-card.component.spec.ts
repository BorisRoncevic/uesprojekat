import { ComponentFixture, TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';

import { VenueCardComponent } from './venue-card.component';
import { VenueOverviewDto } from '../../../models/venue/VenueOverviewDto';

describe('VenueCardComponent', () => {
  let component: VenueCardComponent;
  let fixture: ComponentFixture<VenueCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VenueCardComponent],
      providers: testProviders
    })
    .compileComponents();

    fixture = TestBed.createComponent(VenueCardComponent);
    component = fixture.componentInstance;
    component.venue = {
      id: 1,
      name: 'Test venue',
      description: 'Test description',
      address: 'Test address',
      averageRating: 4.5,
      type: 'THEATER',
      imagePath: 'data:image/gif;base64,R0lGODlhAQABAAAAACw=',
      pdfPath: '',
    } satisfies VenueOverviewDto;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
