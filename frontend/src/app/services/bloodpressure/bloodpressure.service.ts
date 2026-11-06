import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BloodpressureResponseDto } from '../../models/bloodpressure-response-dto';
import { BloodpressureRequestDto } from '../../models/bloodpressure-request-dto';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class BloodpressureService {

  private readonly baseUrl = `${environment.backendUrl}/bloodpressure`;

  constructor(private http: HttpClient) {}

  getByPatientId(patientId: string): Observable<BloodpressureResponseDto[]> {
    return this.http.get<BloodpressureResponseDto[]>(`${this.baseUrl}/patient/${patientId}`);
  }

  createBloodpressure(
    dto: BloodpressureRequestDto
  ): Observable<BloodpressureResponseDto> {
    return this.http.post<BloodpressureResponseDto>(
      this.baseUrl,
      dto
    );
  }

updateBloodpressure(
  id: string,
  dto: BloodpressureRequestDto
): Observable<void> {

  return this.http.put<void>(
    `${this.baseUrl}/${id}`,
    dto
  );
}

deleteBloodpressure(id: string): Observable<void> {

  return this.http.delete<void>(
    `${this.baseUrl}/${id}`
  );
}

}
