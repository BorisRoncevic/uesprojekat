import { Component, OnInit } from '@angular/core';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { VenueCardComponent } from '../venue-card/venue-card.component';
import { CommonModule } from '@angular/common';
import { VenueService } from '../../../services/venue/venue.service';
import { VenueOverviewDto } from '../../../models/venue/VenueOverviewDto';
import { FilterVenueDto } from '../../../models/venue/FilterVenueDto';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { FloatLabelModule } from 'primeng/floatlabel';

@Component({
  selector: 'app-explore-venues-page',
  imports: [
    InputTextModule,
    ButtonModule,
    SelectModule,
    PaginatorModule,
    VenueCardComponent,
    CommonModule,
    ReactiveFormsModule,
    FloatLabelModule,
    FormsModule
  ],
  templateUrl: './explore-venues-page.component.html',
  styleUrl: './explore-venues-page.component.css',
})
export class ExploreVenuesPageComponent implements OnInit {
  venues: VenueOverviewDto[] = [];
  totalElements: number = 0;
  rows: number = 8;
  currentPage: number = 0;
  first: number = 0;
  currentFilter: string = '';
  // currentType: any = { name: 'Select venue type', value: -1 };

  // public venueTypes = [
  //   { name: 'Select venue type', value: -1 },
  //   { name: 'Bar', value: 1 },
  //   { name: 'Cultural Center', value: 0 },
  //   { name: 'Museum', value: 7 },
  //   { name: 'Night Club', value: 2 },
  //   { name: 'Restaurant', value: 3 },
  //   { name: 'Rooftop', value: 5 },
  //   { name: 'Stadium', value: 6 },
  //   { name: 'Theater', value: 4 },
  // ];

  filterForm = new FormGroup({
    filter: new FormControl(''),
  })

  constructor(private venueService: VenueService) {}

  ngOnInit(): void {
    this.loadVenues({ filter: '' }, 0);
  }

  onPageChange($event: PaginatorState) {
    this.first = $event.first ?? 0;

    this.loadVenues(
      { filter: this.currentFilter },
      $event.page ?? 0
    );
  }

  filter() {
    this.currentFilter = this.filterForm.value.filter?.trim() ?? '';
    this.loadVenues({ filter: this.currentFilter }, 0)
  }

  loadVenues(filterVenueDto: FilterVenueDto, page: number): void {
    this.venueService
      .filterVenues(
        filterVenueDto,
        page
      )
      .subscribe({
        next: (response) => {
          this.venues = response.content;
          this.totalElements = response.totalElements;
        },
        error: (error) => {
          console.error('Failed to load venues', error);
        },
      });
  }
}
