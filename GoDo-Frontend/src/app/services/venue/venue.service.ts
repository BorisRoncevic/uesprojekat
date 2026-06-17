import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment.development';
import { AddVenueRequest } from '../../models/venue/AddVenueRequest';
import { Observable } from 'rxjs';
import { FilterVenueDto } from '../../models/venue/FilterVenueDto';
import { UpdateVenueDto } from '../../models/venue/UpdateVenueDto';

@Injectable({
  providedIn: 'root',
})
export class VenueService {
  constructor(private http: HttpClient) {}

  public addVenue(venueData: AddVenueRequest, venueImage: File, venueDesc: File) {
    let formData = new FormData();
    formData.append('venue', JSON.stringify(venueData));
    formData.append('image', venueImage);
    formData.append('description', venueDesc);

    return this.http.post(environment.apiUrl + '/venue', formData);
  }

  public filterVenues(
    filterVenueDto: FilterVenueDto,
    pageNumber: number
  ): Observable<any> {
    let venueTypeParam = null;


    let queryParameters =
      '?filter=' +
      filterVenueDto.filter +
      '&page=' +
      pageNumber.toString() +
      '&size=8';
    
    return this.http.get(environment.apiUrl + '/venue' + queryParameters);
  }

  public findVenueById(venueId: string) : Observable<any> {
    return this.http.get(`${environment.apiUrl}/venue/${venueId}`);
  }

  public updateVenue(venueId: string, body: UpdateVenueDto) : Observable<any> {
    return this.http.put(`${environment.apiUrl}/venue/${venueId}`, body)
  }

  public deleteVenue(venueId: string) : Observable<any> {
    return this.http.delete(`${environment.apiUrl}/venue/${venueId}`);
  }

  public getTopVenues(): Observable<any> {
    return this.http.get(`${environment.apiUrl}/venue/top`);
  }
}
