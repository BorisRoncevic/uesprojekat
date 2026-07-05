import { Component } from '@angular/core';
import { ReviewOverviewDto } from '../../../models/review/ReviewOverviewDto';
import { ReviewService } from '../../../services/review/review.service';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { ReviewCardComponent } from '../review-card/review-card.component';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MessageService } from 'primeng/api';
import { CommonModule } from '@angular/common';
import { ManagesService } from '../../../services/manages/manages.service';
import { AuthService } from '../../../services/auth/auth.service';
import { ButtonModule } from "primeng/button";

@Component({
  selector: 'app-review-page',
  imports: [PaginatorModule, ReviewCardComponent, CommonModule, ButtonModule, RouterModule],
  templateUrl: './review-page.component.html',
  styleUrl: './review-page.component.css',
})
export class ReviewPageComponent {
  reviews: ReviewOverviewDto[] = [];
  totalElements: number = 0;
  rows: number = 5;
  currentPage: number = 0;
  first: number = 0;
  venueId: string = '';
  isManager: boolean = false;
  selectedSortField = 'createdAt';
  selectedSortDirection = 'desc';

  sortFieldOptions = [
    { label: 'Date', value: 'createdAt' },
    { label: 'Performance', value: 'rating.performance' },
    { label: 'Sound and light', value: 'rating.ambient' },
    { label: 'Venue', value: 'rating.venue' },
    { label: 'Overall', value: 'rating.overallImpression' },
  ];

  sortDirectionOptions = [
    { label: 'Descending', value: 'desc' },
    { label: 'Ascending', value: 'asc' },
  ];

  constructor(
    private reviewService: ReviewService,
    private route: ActivatedRoute,
    private messageService: MessageService,
    private managesService: ManagesService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.venueId = this.route.snapshot.paramMap.get('id') ?? '0';
    this.loadReviews(0);
    this.managesService
      .doesManagementExist(this.venueId, this.authService.getUsername())
      .subscribe({
        next: (response) => {
          this.isManager = response.exists;
        },
        error: (error) => {
          this.isManager = false;
        },
      });
  }

  onPageChange($event: PaginatorState) {
    this.first = $event.first ?? 0;

    this.loadReviews($event.page ?? 0);
  }

  filter() {
    this.first = 0;
    this.loadReviews(0);
  }

  loadReviews(page: number): void {
    this.reviewService.getRatingPageByVenueId(
      this.venueId,
      page,
      this.selectedSortField,
      this.selectedSortDirection
    ).subscribe({
      next: (response) => {
        this.reviews = response.content;
        this.totalElements = response.totalElements;
      },
      error: (error) => {
        console.error('Failed to load venues', error);
      },
    });
  }

  showError(message: string) {
    this.messageService.add({
      summary: message,
      severity: 'error',
    });
  }

  showSuccess(message: string) {
    this.messageService.add({
      summary: message,
      severity: 'success',
    });
  }
}
