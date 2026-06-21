import { Component, Input, OnInit } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { VenueOverviewDto } from '../../../models/venue/VenueOverviewDto';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-venue-card',
  imports: [ButtonModule, RouterModule],
  templateUrl: './venue-card.component.html',
  styleUrl: './venue-card.component.css',
})
export class VenueCardComponent implements OnInit {
  @Input() venue!: VenueOverviewDto;

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

  venueType: string = '';

  ngOnInit(): void {
    if (this.venue) {
      this.venueType = this.VenueTypeMap.get(this.venue.type) ?? 'Unknown Type';
    }
  }

  public venueTypeColor(): string {
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

  public downloadPdf(): void {
    if (this.venue?.pdfPath) {
      const link = document.createElement('a');
      link.href = this.venue.pdfPath;
      
      // Extract file name from path or default to a generic name
      const fileName = this.venue.pdfPath.split('/').pop() || `${this.venue.name}-info.pdf`;
      link.download = fileName;
      
      link.target = '_blank';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  }
}
