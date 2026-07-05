import { ComponentFixture, TestBed } from '@angular/core/testing';
import { testProviders } from '@testing/test-providers';

import { ExploreVenuesPageComponent } from './explore-venues-page.component';

describe('ExploreVenuesPageComponent', () => {
  let component: ExploreVenuesPageComponent;
  let fixture: ComponentFixture<ExploreVenuesPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExploreVenuesPageComponent],
      providers: testProviders
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExploreVenuesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
