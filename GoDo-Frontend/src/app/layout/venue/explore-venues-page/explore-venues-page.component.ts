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
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
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
  currentCriteria: FilterVenueDto = { operator: 'AND', sortDirection: 'asc' };
  operatorOptions = [
    { label: 'AND', value: 'AND' },
    { label: 'OR', value: 'OR' },
  ];
  sortOptions = [
    { label: 'Name A-Z', value: 'asc' },
    { label: 'Name Z-A', value: 'desc' },
  ];
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
    name: new FormControl(''),
    description: new FormControl(''),
    pdfDescription: new FormControl(''),
    reviewCountFrom: new FormControl<number | null>(null),
    reviewCountTo: new FormControl<number | null>(null),
    ratingPerformanceFrom: new FormControl<number | null>(null),
    ratingPerformanceTo: new FormControl<number | null>(null),
    ratingAmbientFrom: new FormControl<number | null>(null),
    ratingAmbientTo: new FormControl<number | null>(null),
    ratingVenueFrom: new FormControl<number | null>(null),
    ratingVenueTo: new FormControl<number | null>(null),
    ratingOverallImpressionFrom: new FormControl<number | null>(null),
    ratingOverallImpressionTo: new FormControl<number | null>(null),
    operator: new FormControl<'AND' | 'OR'>('AND'),
    sortDirection: new FormControl<'asc' | 'desc'>('asc'),
  })

  constructor(private venueService: VenueService) {}

  ngOnInit(): void {
    this.loadVenues(this.currentCriteria, 0);
  }

  onPageChange($event: PaginatorState) {
    this.first = $event.first ?? 0;

    this.loadVenues(this.currentCriteria, $event.page ?? 0);
  }

  filter() {
    this.first = 0;
    this.currentCriteria = this.buildCriteria();
    this.loadVenues(this.currentCriteria, 0)
  }

  resetFilters() {
    this.filterForm.reset({
      filter: '',
      name: '',
      description: '',
      pdfDescription: '',
      reviewCountFrom: null,
      reviewCountTo: null,
      ratingPerformanceFrom: null,
      ratingPerformanceTo: null,
      ratingAmbientFrom: null,
      ratingAmbientTo: null,
      ratingVenueFrom: null,
      ratingVenueTo: null,
      ratingOverallImpressionFrom: null,
      ratingOverallImpressionTo: null,
      operator: 'AND',
      sortDirection: 'asc',
    });
    this.first = 0;
    this.currentCriteria = { operator: 'AND', sortDirection: 'asc' };
    this.loadVenues(this.currentCriteria, 0);
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

  private buildCriteria(): FilterVenueDto {
    const value = this.filterForm.value;

    return {
      filter: value.filter?.trim() || null,
      name: value.name?.trim() || null,
      description: value.description?.trim() || null,
      pdfDescription: value.pdfDescription?.trim() || null,
      reviewCountFrom: this.toNumber(value.reviewCountFrom),
      reviewCountTo: this.toNumber(value.reviewCountTo),
      ratingPerformanceFrom: this.toNumber(value.ratingPerformanceFrom),
      ratingPerformanceTo: this.toNumber(value.ratingPerformanceTo),
      ratingAmbientFrom: this.toNumber(value.ratingAmbientFrom),
      ratingAmbientTo: this.toNumber(value.ratingAmbientTo),
      ratingVenueFrom: this.toNumber(value.ratingVenueFrom),
      ratingVenueTo: this.toNumber(value.ratingVenueTo),
      ratingOverallImpressionFrom: this.toNumber(value.ratingOverallImpressionFrom),
      ratingOverallImpressionTo: this.toNumber(value.ratingOverallImpressionTo),
      operator: value.operator ?? 'AND',
      sortDirection: value.sortDirection ?? 'asc',
    };
  }

  private toNumber(value: unknown): number | null {
    if (value === null || value === undefined || value === '') {
      return null;
    }

    const parsedValue = Number(value);
    return Number.isNaN(parsedValue) ? null : parsedValue;
  }
}
