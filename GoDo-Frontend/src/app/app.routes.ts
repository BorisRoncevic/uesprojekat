import { Routes } from '@angular/router';
import { RegistrationRequestManagementComponent } from './layout/admin/registration-request-management/registration-request-management.component';
import { AuthGuard } from './guards/auth-guard';
import { RoleGuard } from './guards/role-guard';
import { AddVenueComponent } from './layout/admin/add-venue/add-venue.component';
import { ExploreVenuesPageComponent } from './layout/venue/explore-venues-page/explore-venues-page.component';
import { VenuePageComponent } from './layout/venue/venue-page/venue-page.component';
import { DashboardComponent } from './layout/dashboard-related/dashboard/dashboard.component';
import { ProfileStatusGuard } from './guards/profile-status-guard';
import { CompleteProfileDetailsFormComponent } from './layout/forms/complete-profile-details-form/complete-profile-details-form.component';
import { ProfilePageComponent } from './layout/user/profile/profile-page/profile-page.component';
import { ChangePersonalInformationComponent } from './layout/user/profile/change-personal-information/change-personal-information.component';
import { AddEventComponent } from './layout/event/add-event/add-event.component';
import { ManagementGuard } from './guards/management-guard';
import { EventPageComponent } from './layout/event/event-page/event-page.component';
import { ExploreEventPageComponent } from './layout/event/explore-event-page/explore-event-page.component';
import { ReviewPageComponent } from './layout/review/review-page/review-page.component';

export const routes: Routes = [
    {
        title: "Home",
        path: '',
        component: DashboardComponent,
        pathMatch: 'full',
    },
    {
        title: "Dashboard",
        path: 'dashboard',
        component: DashboardComponent,
        canActivate: [AuthGuard, ProfileStatusGuard],
    },
    {
        title: "Registration request management",
        path: 'registration-request-management',
        component: RegistrationRequestManagementComponent,
        canActivate: [AuthGuard, RoleGuard],
        data: {
            expectedRole: "ADMIN"
        }
    },
    {
        title: "Add venue",
        path: 'add-venue',
        component: AddVenueComponent,
        canActivate: [AuthGuard, RoleGuard],
        data: {
            expectedRole: "ADMIN"
        }
    },
    {
        title: "Explore venues",
        path: 'venue',
        component: ExploreVenuesPageComponent,
        canActivate: [AuthGuard, ProfileStatusGuard],
    },
    {
        title: "Venue",
        path: 'venue/:id',
        component: VenuePageComponent,
        canActivate: [AuthGuard, ProfileStatusGuard],
    },
    {
        title: 'Finish profile',
        path: 'finish-profile',
        component: CompleteProfileDetailsFormComponent,
        canActivate: [AuthGuard]
    },
    {
        title: 'Profile',
        path: 'profile',
        component: ProfilePageComponent,
        canActivate: [AuthGuard, ProfileStatusGuard]
    },
    {
        title: 'Change personal information',
        path: 'edit-profile',
        component: ChangePersonalInformationComponent,
        canActivate: [AuthGuard, ProfileStatusGuard]
    }, 
    {
        title: 'Add event',
        path: 'add-event/:venueId',
        component: AddEventComponent,
        canActivate: [AuthGuard, ProfileStatusGuard, ManagementGuard]
    }, 
    {
        title: 'Event',
        path: 'event/:id',
        component: EventPageComponent,
        canActivate: [AuthGuard, ProfileStatusGuard]
    },
    {
        title: 'Explore events',
        path: 'event',
        component: ExploreEventPageComponent,
        canActivate: [AuthGuard, ProfileStatusGuard]
    },
    {
        title: 'Reviews',
        path: 'review/venue/:id',
        component: ReviewPageComponent,
        canActivate: [AuthGuard, ProfileStatusGuard]
    }
];
