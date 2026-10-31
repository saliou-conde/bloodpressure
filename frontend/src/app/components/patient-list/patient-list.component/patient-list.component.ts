import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { BloodpressureResponseDto } from '../../../models/bloodpressure-response-dto';
import { PatientRequestDto } from '../../../models/patient-request-dto';
import { PatientViewModel } from '../../../models/patient-view-model';
import { BloodpressureService } from '../../../services/bloodpressure/bloodpressure.service';
import { PatientService } from '../../../services/patient/patient.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-patient-list',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './patient-list.component.html',
  styleUrls: ['./patient-list.component.css']
})
export class PatientListComponent implements OnInit {
  patients: PatientViewModel[] = [];
  loadingPatients = false;

  selectedPatient: PatientViewModel | null = null;
  bloodpressures: BloodpressureResponseDto[] = [];
  loadingBloodpressures = false;

  // Neues Patient-Formular
  newPatient: PatientRequestDto = {
    firstName: '',
    lastName: '',
    email: '',
    gender: 'MALE',
    birthDate: '',
    address: {
      street: '',
      city: '',
      state: '',
      zipCode: ''
    }
  };
  creatingPatient = false;

  constructor(
    private patientService: PatientService,
    private bpService: BloodpressureService
  ) {}

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.loadingPatients = true;
    this.patientService.getAllPatients()
      .pipe(finalize(() => (this.loadingPatients = false)))
      .subscribe({
        next: (data) => (this.patients = data),
        error: (err) => {
          console.error(err);
          this.patients = [];
        }
      });
  }

  selectPatient(p: PatientViewModel): void {
    if (this.selectedPatient?.id === p.id) {
      this.clearSelection();
      return;
    }
    this.selectedPatient = p;
    this.bloodpressures = [];

    if (p.bloodpressureRequestDtoList && p.bloodpressureRequestDtoList.length > 0) {
      this.bloodpressures = p.bloodpressureRequestDtoList.map((r, idx) => ({
        id: `tmp-${idx}`,
        systole: r.systole,
        diastole: r.diastole,
        heartRate: r.heartRate,
        map: r.map ?? null,
        createdAt: new Date().toISOString(),
        updatedAt: null,
        isDeleted: false
      }));
      return;
    }

    this.loadingBloodpressures = true;
    this.bpService.getByPatientId(p.id)
      .pipe(finalize(() => (this.loadingBloodpressures = false)))
      .subscribe({
        next: (bps) => this.bloodpressures = bps,
        error: (err) => { console.error(err); this.bloodpressures = []; }
      });
  }

  clearSelection(): void {
    this.selectedPatient = null;
    this.bloodpressures = [];
  }

  formatName(p: PatientViewModel): string {
    return `${p.firstName} ${p.lastName}`;
  }

  createPatient(): void {
    if (!this.newPatient.firstName || 
      !this.newPatient.lastName || 
      !this.newPatient.email || 
      !this.newPatient.birthDate || 
      !this.newPatient.address) 
      return;
    this.creatingPatient = true;
    this.patientService.createPatient(this.newPatient)
      .pipe(finalize(() => (this.creatingPatient = false)))
      .subscribe({
        next: () => {
          this.newPatient = { 
            firstName: '', 
            lastName: '', 
            email: '', 
            gender: 'MALE', 
            birthDate: '',
            address: {
              street: '',
              city: '',
              state: '',
              zipCode: ''
            } 
      };
          this.loadPatients();
        },
        error: (err) => console.error('Fehler beim Erstellen', err)
      });
  }
}
