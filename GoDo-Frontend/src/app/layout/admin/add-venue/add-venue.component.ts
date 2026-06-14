import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { FloatLabelModule } from 'primeng/floatlabel';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { FileUploadModule } from 'primeng/fileupload';
import { VenueType } from '../../../enums/venueType';
import { TextareaModule } from 'primeng/textarea';
import { TooltipModule } from 'primeng/tooltip';
import { VenueService } from '../../../services/venue/venue.service';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-add-venue',
  imports: [
    FloatLabelModule,
    CommonModule,
    ReactiveFormsModule,
    SelectModule,
    FormsModule,
    InputTextModule,
    ButtonModule,
    FileUploadModule,
    TextareaModule,
    TooltipModule
  ],
  templateUrl: './add-venue.component.html',
  styleUrl: './add-venue.component.css',
})
export class AddVenueComponent {
  public venueTypes = [
    { name: 'Bar', value: VenueType.BAR },
    { name: 'Cultural Center', value: VenueType.CULTURAL_CENTER },
    { name: 'Museum', value: VenueType.MUSEUM },
    { name: 'Night Club', value: VenueType.NIGHT_CLUB },
    { name: 'Restaurant', value: VenueType.RESTAURANT },
    { name: 'Rooftop', value: VenueType.ROOFTOP },
    { name: 'Stadium', value: VenueType.STADIUM },
    { name: 'Theater', value: VenueType.THEATER },
  ];

  private VenueTypeMap: Map<String, number> = new Map([
    ['Cultural Center', 0],
    ['Bar', 1],
    ['Night Club', 2],
    ['Restaurant', 3],
    ['Theater', 4],
    ['Rooftop', 5],
    ['Stadium', 6],
    ['Museum', 7],
  ]);

  selectedImageFile: File | null = null;
  selectedPDFFile: File | null = null;

  addVenueForm = new FormGroup({
    name: new FormControl('', Validators.required),
    address: new FormControl('', Validators.required),
    description: new FormControl('', Validators.required),
    type: new FormControl(this.venueTypes[0], Validators.required),
  });

  constructor(public venueService: VenueService, private router: Router, private messageService: MessageService) {}

  onImageUpload(event: any) {
    this.selectedImageFile = event.files[0];
  }

  onPDFUpload(event: any) {
    this.selectedPDFFile = event.files[0];
  }

  isValid(): unknown {
    return this.addVenueForm.valid && this.selectedImageFile != null && this.selectedPDFFile != null;
  }

  submitAddVenueForm() {
    let name = this.addVenueForm.value.name ?? '';
    let address = this.addVenueForm.value.address ?? '';
    let description = this.addVenueForm.value.description ?? '';
    let type =
      this.VenueTypeMap.get(this.addVenueForm.value.type?.name ?? '') ?? 0;

    if (this.selectedImageFile != null && this.selectedPDFFile != null) {
      this.venueService
        .addVenue(
          {
            name: name,
            address: address,
            description: description,
            type: type,
          },
          this.selectedImageFile,
          this.selectedPDFFile
        )
        .subscribe({
          next: (response) => {
            this.router.navigate(['dashboard']);
            this.messageService.add({
              severity: 'success',
              summary: 'Venue succesfully added!'
            })
          },
          error: (error) => {
            this.addVenueError(error.error.message);
          },
        });
    } else {
      this.addVenueError("You must provide a venue image and description PDF!");
    }
  }

  addVenueError(message: string) {
    this.messageService.add({
      severity: 'error',
      summary: 'An error occurred while trying to add venue!',
      detail : message
    })
  }
}
