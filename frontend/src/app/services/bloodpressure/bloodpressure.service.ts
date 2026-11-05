import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BloodpressureResponseDto } from '../../models/bloodpressure-response-dto';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class BloodpressureService {

  private readonly baseUrl = `${environment.backendUrl}/bloodpressure`;

  constructor(private http: HttpClient) {}

  getByPatientId(patientId: string): Observable<BloodpressureResponseDto[]> {
    return this.http.get<BloodpressureResponseDto[]>(`${this.baseUrl}/patient/${patientId}`);
  }

}
