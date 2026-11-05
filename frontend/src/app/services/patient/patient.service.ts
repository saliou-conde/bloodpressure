import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { PatientRequestDto } from '../../models/patient-request-dto';
import { PatientResponseDto } from '../../models/patient-response-dto';
import { PatientViewModel } from '../../models/patient-view-model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PatientService {

  private readonly baseUrl = `${environment.backendUrl}/patients`;

  constructor(private readonly http: HttpClient) {}

  getAllPatients(): Observable<PatientViewModel[]> {
    return this.http.get<PatientResponseDto[]>(this.baseUrl).pipe(
      map((arr) =>
        arr.map((p) => ({
          id: p.id,
          firstName: p.firstName,
          lastName: p.lastName,
          email: p.email,
          gender: p.gender,
          birthDate: p.birthDate,
          age: this.calculateAge(p.birthDate),
          isDeleted: p.isDeleted,
          bloodpressureRequestDtoList: p.bloodpressureRequestDtoList,
          address: p.address
        }))
      )
    );
  }

  createPatient(dto: PatientRequestDto): Observable<PatientResponseDto> {
    return this.http.post<PatientResponseDto>(this.baseUrl, dto);
  }

  getPatientById(id: string): Observable<PatientViewModel> {
    return this.http.get<PatientResponseDto>(`${this.baseUrl}/${id}`).pipe(
      map((p) => ({
        id: p.id,
        firstName: p.firstName,
        lastName: p.lastName,
        email: p.email,
        gender: p.gender,
        birthDate: p.birthDate,
        age: this.calculateAge(p.birthDate),
        isDeleted: p.isDeleted,
        bloodpressureRequestDtoList: p.bloodpressureRequestDtoList,
        address: p.address
      }))
    );
  }

  updatePatient(id: string, dto: PatientRequestDto): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/${id}`, dto);
  }

  deletePatient(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  private calculateAge(birthDateIso?: string): number {
    if (!birthDateIso) return 0;
    const birth = new Date(birthDateIso + 'T00:00:00');
    const today = new Date();
    let age = today.getFullYear() - birth.getFullYear();
    const m = today.getMonth() - birth.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--;
    return age;
  }

}
