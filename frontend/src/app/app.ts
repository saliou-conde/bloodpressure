import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PatientListComponent } from './components/patient-list/patient-list.component/patient-list.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule, PatientListComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App {
  protected title = 'Blood Pressure App';
}
