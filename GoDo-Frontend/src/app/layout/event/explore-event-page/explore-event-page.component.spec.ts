import { ComponentFixture, TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';

import { ExploreEventPageComponent } from './explore-event-page.component';

describe('ExploreEventPageComponent', () => {
  let component: ExploreEventPageComponent;
  let fixture: ComponentFixture<ExploreEventPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExploreEventPageComponent],
      providers: testProviders
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExploreEventPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
