import { Component, OnInit } from '@angular/core';
import { VenueOverviewDto } from '../../../models/venue/VenueOverviewDto';
import { VenueService } from '../../../services/venue/venue.service';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { CommonModule } from '@angular/common';
import { InputTextModule } from 'primeng/inputtext';
import { FloatLabel } from 'primeng/floatlabel';
import { ButtonModule } from 'primeng/button';
import { VenueType } from '../../../enums/venueType';
import {
  FormControl,
  FormGroup,
  FormsModule,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { SelectModule } from 'primeng/select';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
import { MultiSelectModule } from 'primeng/multiselect';
import { UserService } from '../../../services/user/user.service';
import { ManagesService } from '../../../services/manages/manages.service';
import { forkJoin } from 'rxjs';
import { EventService } from '../../../services/event/event.service';
import { CardModule } from 'primeng/card';
import { UpcomingEventCardComponent } from '../../event/upcoming-event-card/upcoming-event-card.component';
import { RatingModule, RatingRateEvent } from 'primeng/rating';
import { MessageModule } from 'primeng/message';
import { ReviewService } from '../../../services/review/review.service';
import { ReviewCardComponent } from '../../review/review-card/review-card.component';
import { RatingOverviewDto } from '../../../models/review/RatingOverviewDto';
import { VenueCardComponent } from '../venue-card/venue-card.component';

@Component({
  selector: 'app-venue-page',
  imports: [
    CommonModule,
    InputTextModule,
    FloatLabel,
    ButtonModule,
    FormsModule,
    FormsModule,
    SelectModule,
    ReactiveFormsModule,
    ToastModule,
    DialogModule,
    MultiSelectModule,
    CardModule,
    RouterModule,
    UpcomingEventCardComponent,
    RatingModule,
    MessageModule,
    ReviewCardComponent,
    VenueCardComponent,
  ],
  templateUrl: './venue-page.component.html',
  styleUrl: './venue-page.component.css',
})
export class VenuePageComponent implements OnInit {
  public venue: VenueOverviewDto | undefined;
  venueType: string = '';
  isEdit: boolean = false;
  initialVenueTypeIndex: number = 0;
  isLoading: boolean = false;
  isDeleteDialogVisible: boolean = false;
  isManagementDialogVisible: boolean = false;
  isManager: boolean = false;

  selectedUsers: any;
  managerOptions: any;

  upcomingEvents: any[] = [];

  reviewEventOptions: any[] = [{ id: -1, name: 'Select event' }];

  reviewList: any[] = [];
  similarVenues: VenueOverviewDto[] = [];
  ratingOverview: RatingOverviewDto = {averageRating: 0, reviewCount: 0}

  VenueTypeMap = new Map<string, string>([
    ['CULTURAL_CENTER', 'Cultural Center'],
    ['BAR', 'Bar'],
    ['NIGHT_CLUB', 'Night Club'],
    ['RESTAURANT', 'Restaurant'],
    ['THEATER', 'Theater'],
    ['ROOFTOP', 'Rooftop'],
    ['STADIUM', 'Stadium'],
    ['MUSEUM', 'Museum'],
  ]);

  public VenueTypes = [
    { name: 'Bar', value: VenueType.BAR },
    { name: 'Cultural Center', value: VenueType.CULTURAL_CENTER },
    { name: 'Museum', value: VenueType.MUSEUM },
    { name: 'Night Club', value: VenueType.NIGHT_CLUB },
    { name: 'Restaurant', value: VenueType.RESTAURANT },
    { name: 'Rooftop', value: VenueType.ROOFTOP },
    { name: 'Stadium', value: VenueType.STADIUM },
    { name: 'Theater', value: VenueType.THEATER },
  ];

  private VenueTypeConversionMap: Map<String, number> = new Map([
    ['Cultural Center', 0],
    ['Bar', 1],
    ['Night Club', 2],
    ['Restaurant', 3],
    ['Theater', 4],
    ['Rooftop', 5],
    ['Stadium', 6],
    ['Museum', 7],
  ]);

  editVenueForm: FormGroup<any> = new FormGroup({});
  isRateEventDialogVisible: boolean = false;

  constructor(
    private venueService: VenueService,
    private route: ActivatedRoute,
    private authService: AuthService,
    private messageService: MessageService,
    private router: Router,
    private userService: UserService,
    private managesService: ManagesService,
    private eventService: EventService,
    private reviewService: ReviewService
  ) {}

  ngOnInit(): void {
    let venueId = this.route.snapshot.paramMap.get('id') ?? '0';

    this.venueService.findVenueById(venueId).subscribe({
      next: (response: VenueOverviewDto) => {
        this.venue = response;

        for (let i = 0; i < this.VenueTypes.length; i++) {
          if (
            this.VenueTypes[i].name == this.VenueTypeMap.get(this.venue.type)
          ) {
            this.initialVenueTypeIndex = i;
            break;
          }
        }

        this.venueType = this.VenueTypeMap.get(this.venue.type) ?? '';

        this.editVenueForm = new FormGroup({
          name: new FormControl(this.venue?.name, Validators.required),
          address: new FormControl(this.venue?.address, Validators.required),
          description: new FormControl(
            this.venue?.description,
            Validators.required
          ),
          type: new FormControl(
            this.VenueTypes[this.initialVenueTypeIndex],
            Validators.required
          ),
        });
      },
      error: (error) => {
        this.showError(error.error.message);
      },
    });

    this.managesService
      .doesManagementExist(venueId, this.authService.getUsername() ?? '')
      .subscribe({
        next: (response) => {
          this.isManager = response.exists;
        },
        error: (error) => {
          this.isManager = false;
        },
      });

    this.eventService.getUpcomingEventsByVenueId(venueId).subscribe({
      next: (response) => {
        this.upcomingEvents = response;
        for (let event in response) {
        }
      },
      error: (error) => {},
    });

    this.reviewService.getReviewsByVenueId(venueId).subscribe({
      next: (response) => {
        this.reviewList = response;
      },
      error: (error) => {
        console.log(error);
        this.showError('An error has occurred while fetching feedback');
      },
    });

    this.reviewService.getRatingOverview(venueId).subscribe({
      next: (response) => {
        this.ratingOverview = response;
      },
      error: (error) => {
        this.showError(
          'An error has occurred while fetching venue rating overview!'
        );
      },
    });

    this.venueService.findMoreLikeThis(Number(venueId), 0).subscribe({
      next: (response) => {
        this.similarVenues = response.content ?? [];
      },
      error: () => {
        this.similarVenues = [];
      },
    });
  }

  venueTypeColor(): string {
    if (!this.venue) {
      return '#6c757d';
    }

    switch (this.venue.type) {
      case 'RESTAURANT':
        return '#AEEA00';
      case 'BAR':
        return '#FF9100';
      case 'NIGHT_CLUB':
        return '#F50057';
      case 'THEATER':
        return '#FF3D00';
      case 'STADIUM':
        return '#00E5FF';
      case 'CULTURAL_CENTER':
        return '#1DE9B6';
      case 'MUSEUM':
        return '#FFAB00';
      case 'ROOFTOP':
        return '#FFD600';
      default:
        return '#E0E0E0';
    }
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  // review

  reviewForm: FormGroup<any> = new FormGroup({
    event: new FormControl(this.reviewEventOptions[0], Validators.required),
    performance: new FormControl<number | null>(null, Validators.required),
    ambient: new FormControl<number | null>(null, Validators.required),
    venue: new FormControl<number | null>(null, Validators.required),
    overall: new FormControl<number | null>(null, Validators.required),
    comment: new FormControl(),
  });

  showReviewDialog() {
    this.eventService.getEventReviewOptions(this.venue?.id ?? 0).subscribe({
      next: (next) => {
        next.forEach((element: any) => {
          this.reviewEventOptions.push(element);
        });

        this.isRateEventDialogVisible = true;
      },
      error: (error) => {},
    });
  }

  hideReviewDialog() {
    this.isRateEventDialogVisible = false;
    this.reviewForm.reset();
  }

  submitReview() {
    if (this.isReviewSubmitButtonDisabled()) {
      this.showError('Please check all fields before re-submitting!');
      return;
    }

    this.reviewService
      .createReview({
        reviewer: this.authService.getUsername() ?? '',
        venueId: this.venue?.id ?? 0,
        eventId: this.reviewForm.value.event.id ?? 0,
        comment: this.reviewForm.value.comment ?? '',
        performanceGrade: this.reviewForm.value.performance ?? 0,
        venueGrade: this.reviewForm.value.venue ?? 0,
        ambientGrade: this.reviewForm.value.ambient ?? 0,
        overallImpression: this.reviewForm.value.overall ?? 0,
      })
      .subscribe({
        next: (response) => {
          this.showSuccess('Venue successfully reviewed!');
          this.hideReviewDialog();
        },
        error: (error) => {
          this.showError('An error has occurred while reviewing event');
        },
      });
  }

  isReviewSubmitButtonDisabled() {
    return !this.reviewForm.valid;
  }

  // edit venue
  submitChanges() {
    this.isLoading = true;
    let updateVenueRequest = {
      name: this.editVenueForm.value.name ?? '',
      address: this.editVenueForm.value.address ?? '',
      description: this.editVenueForm.value.description ?? '',
      venueType:
        this.VenueTypeConversionMap.get(this.editVenueForm.value.type.value) ??
        0,
    };

    let venueId = this.route.snapshot.params['id'];
    this.venueService.updateVenue(venueId, updateVenueRequest).subscribe({
      next: (response) => {
        if (this.venue != undefined) {
          this.venue.name = response.name;
          this.venue.address = response.address;
          this.venue.description = response.description;

          for (let i = 0; i < this.VenueTypes.length; i++) {
            if (
              this.VenueTypes[i].name ==
              this.VenueTypeMap.get(response.venueType)
            ) {
              this.venue.type = this.VenueTypes[i].name
                .toUpperCase()
                .trim()
                .replace(' ', '_');
              this.venueType = this.VenueTypes[i].name;
              let venueTypeLabel = document.querySelector('#venueTypeLabel');
              venueTypeLabel?.setAttribute(
                'style',
                `color: ${this.venueTypeColor()}`
              );
              break;
            }
          }
        }

        this.isLoading = false;
        this.isEdit = false;
        this.showSuccess('Venue successfully updated!');
      },
      error: (error) => {
        this.isLoading = false;
        this.isEdit = false;
        this.showError(error.error.message);
      },
    });
  }

  switchToEdit() {
    this.isEdit = true;
  }

  discardChanges() {
    this.isEdit = false;
    this.editVenueForm = new FormGroup({
      name: new FormControl(this.venue?.name, Validators.required),
      address: new FormControl(this.venue?.address, Validators.required),
      description: new FormControl(
        this.venue?.description,
        Validators.required
      ),
      type: new FormControl(
        this.VenueTypes[this.initialVenueTypeIndex],
        Validators.required
      ),
    });
  }

  // delete venue
  deleteVenue() {
    let venueId = this.route.snapshot.paramMap.get('id') ?? '0';
    this.venueService.deleteVenue(venueId).subscribe({
      next: (response) => {
        this.router.navigate(['venue']);
        this.showSuccess('Venue successfully removed!');
      },
      error: (error) => {
        this.showError(error.error.message);
      },
    });
  }

  // dialog logic
  showDialog() {
    this.isDeleteDialogVisible = true;
  }

  hideDialog() {
    this.isDeleteDialogVisible = false;
  }

  // management
  showVenueManagementDialog() {
    if (!this.venue?.id) {
      this.showError('Cannot manage managers without a selected venue.');
      return;
    }

    const managerOptions$ = this.userService.getManagerOptions();
    const selectedManagers$ = this.managesService.getManagersByVenueId(
      this.venue.id
    );

    forkJoin({
      options: managerOptions$,
      selected: selectedManagers$,
    }).subscribe({
      next: (response) => {
        this.managerOptions = response.options;
        this.selectedUsers = response.selected;

        this.isManagementDialogVisible = true;
      },
      error: (error) => {
        this.showError(
          'An error occurred while preparing the manager management dialog.'
        );
      },
    });
  }

  submitManagerSelection() {
    this.managesService
      .updateManagers(this.venue?.id ?? 0, this.selectedUsers)
      .subscribe({
        next: (response) => {
          this.isManagementDialogVisible = false;
        },
        error: (error) => {
          this.showError('An error has occurred while updating managers');
        },
      });
  }

  // toast handling
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
