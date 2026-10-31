import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BloodpressureResponseDto } from '../../models/bloodpressure-response-dto';

@Injectable({ providedIn: 'root' })
export class BloodpressureService {
  private readonly baseUrl = 'http://localhost:9999/api/v1/bloodpressure';

  constructor(private http: HttpClient) {}

  getByPatientId(patientId: string): Observable<BloodpressureResponseDto[]> {
    return this.http.get<BloodpressureResponseDto[]>(`${this.baseUrl}/patient/${patientId}`);
  }

}
