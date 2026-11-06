import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { BloodpressureResponseDto } from '../../../models/bloodpressure-response-dto';
import { BloodpressureRequestDto } from '../../../models/bloodpressure-request-dto';
import { PatientRequestDto } from '../../../models/patient-request-dto';
import { PatientViewModel } from '../../../models/patient-view-model';
import { BloodpressureService } from '../../../services/bloodpressure/bloodpressure.service';
import { PatientService } from '../../../services/patient/patient.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import {
  ChartConfiguration,
  ChartData,
  Chart, registerables
} from 'chart.js';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-patient-list',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    BaseChartDirective
  ],
  templateUrl: './patient-list.component.html',
  styleUrls: ['./patient-list.component.css']
})
export class PatientListComponent implements OnInit {
  patients: PatientViewModel[] = [];
  loadingPatients = false;
  creatingBloodpressure = false;
  loading = false;

  selectedPatient: PatientViewModel | null = null;
  bloodpressures: BloodpressureResponseDto[] = [];
  loadingBloodpressures = false;
  editingBloodpressureId: string | null = null;

  public lineChartData: ChartData<'line'> = {
    labels: [],
    datasets: [
      {
        label: 'Systole',
        data: [],
        borderColor: 'rgb(54, 162, 235)',
        backgroundColor: 'rgba(54, 162, 235, 0.2)',
        tension: 0.3
      },
      {
        label: 'Diastole',
        data: [],
        borderColor: 'rgb(255, 99, 132)',
        backgroundColor: 'rgba(255, 99, 132, 0.2)',
        tension: 0.3
      }
    ]
  };

  public lineChartOptions: ChartConfiguration<'line'>['options'] = {
    responsive: true,
    plugins: {
      legend: {
        display: true
      }
    }
  };

  private updateChart(): void {
    this.lineChartData = {
      labels: this.bloodpressures.map(bp =>
        new Date(bp.createdAt).toLocaleDateString()
      ),
      datasets: [
        {
          label: 'Systole',
          data: this.bloodpressures.map(bp => bp.systole),
          borderColor: 'rgb(54,162,235)',
          backgroundColor: 'rgba(54,162,235,0.2)',
          tension: 0.3
        },
        {
          label: 'Diastole',
          data: this.bloodpressures.map(bp => bp.diastole),
          borderColor: 'rgb(255,99,132)',
          backgroundColor: 'rgba(255,99,132,0.2)',
          tension: 0.3
        }
      ]
    };
  }

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
    private bpService: BloodpressureService,
    private cdr: ChangeDetectorRef
  ) {
    Chart.register(...registerables);
  }

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.loadingPatients = true;
    this.patientService.getAllPatients()
      .pipe(finalize(() => {
        this.loadingPatients = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (data) => {
          this.patients = [...data];
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error(err);
          this.loadingPatients = false;
          this.patients = [];
          this.cdr.detectChanges();
          console.log('Fehler beim Laden der Patientenliste:', err);
        }
      });
  }

  getStatus(bp: BloodpressureResponseDto): string {
    if (bp.systole >= 180 || bp.diastole >= 120) {
      return '🔴 Hypertensive Crisis';
    }
    if (bp.systole >= 140 || bp.diastole >= 90) {
      return '🟠 High Blood Pressure';
    }
    if (bp.systole >= 120 && bp.diastole < 80) {
      return '🟡 Elevated';
    }
    return '🟢 Normal';
  }

  getStatusClass(bp: BloodpressureResponseDto): string {
    if (bp.systole >= 180 || bp.diastole >= 120) {
      return 'badge bg-danger';
    }
    if (bp.systole >= 140 || bp.diastole >= 90) {
      return 'badge bg-warning text-dark';
    }
    if (bp.systole >= 120 && bp.diastole < 80) {
      return 'badge bg-info text-dark';
    }
    return 'badge bg-success';
  }

  selectPatient(p: PatientViewModel): void {
    if (this.selectedPatient?.id === p.id) {
      this.clearSelection();
      return;
    }
    this.selectedPatient = p;
    this.bloodpressures = [];
    this.loadingBloodpressures = true;
    this.cdr.detectChanges();

    this.bpService.getByPatientId(p.id)
      .pipe(finalize(() => {
        this.loadingBloodpressures = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (bps) => {
          this.bloodpressures = bps;
          this.updateChart();
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error(err);
          this.bloodpressures = [];
          this.loadingBloodpressures = false;
          this.cdr.detectChanges();
        }
      });
  }

  clearSelection(): void {
    this.selectedPatient = null;
    this.bloodpressures = [];
    this.cdr.detectChanges();
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
      .pipe(finalize(() => {
        this.creatingPatient = false;
        this.cdr.detectChanges();
      }))
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
        error: (err) => {
          console.error('Fehler beim Erstellen', err);
          this.creatingPatient = false;
          this.cdr.detectChanges();
        }
      });
  }

  newBloodpressure = {
    systole: 0,
    diastole: 0,
    heartRate: 0
  };

  editBloodpressure(bp: BloodpressureResponseDto): void {
    this.editingBloodpressureId = bp.id;
    this.newBloodpressure = {
      systole: bp.systole,
      diastole: bp.diastole,
      heartRate: bp.heartRate
    };
    this.cdr.detectChanges();
  }

  deleteBloodpressure(id: string): void {
    if (!confirm('Delete this blood pressure measurement?')) {
      return;
    }
    this.bpService.deleteBloodpressure(id)
      .subscribe({
        next: () => {
          this.bloodpressures = this.bloodpressures.filter(bp => bp.id !== id);
          this.updateChart();
          this.cdr.detectChanges();
        },
        error: err => console.error(err)
      });
  }

  saveBloodpressure(): void {
    if (!this.selectedPatient) {
      return;
    }
    if (
      this.newBloodpressure.systole <= 0 ||
      this.newBloodpressure.diastole <= 0 ||
      this.newBloodpressure.heartRate <= 0
    ) {
      return;
    }

    const request: BloodpressureRequestDto = {
      patientId: this.selectedPatient.id,
      systole: this.newBloodpressure.systole,
      diastole: this.newBloodpressure.diastole,
      heartRate: this.newBloodpressure.heartRate
    };

    this.creatingBloodpressure = true;
    this.cdr.detectChanges();

    const request$: Observable<unknown> = this.editingBloodpressureId
      ? this.bpService.updateBloodpressure(this.editingBloodpressureId, request)
      : this.bpService.createBloodpressure(request);

    request$
      .pipe(
        finalize(() => {
          this.creatingBloodpressure = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.editingBloodpressureId = null;
          this.newBloodpressure = {
            systole: 0,
            diastole: 0,
            heartRate: 0
          };
          this.bpService.getByPatientId(this.selectedPatient!.id).subscribe({
            next: (bps) => {
              this.bloodpressures = bps;
              this.updateChart();
              this.cdr.detectChanges();
            },
            error: (err) => console.error(err)
          });
        },
        error: (err) => {
          console.error(err);
          this.creatingBloodpressure = false;
          this.cdr.detectChanges();
        }
      });
  }
}
