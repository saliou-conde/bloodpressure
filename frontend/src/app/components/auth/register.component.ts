import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService, RegisterRequest } from  '../../services/auth/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  registerData: RegisterRequest = {
    username: '',
    email: '',
    password: ''
  };

  confirmPassword = ''; // <-- Neu: Speichert das wiederholte Passwort

  showPassword = false;          // Für das erste Passwortfeld
  showConfirmPassword = false;   // Für das Bestätigungsfeld
  loading = false;
  errorMessage = '';

  togglePassword(): void {
      this.showPassword = !this.showPassword;
    }

    toggleConfirmPassword(): void {
      this.showConfirmPassword = !this.showConfirmPassword;
    }

  onRegister(): void {
    // ---- NEU: Prüfung, ob Passwörter übereinstimmen ----
    if (this.registerData.password !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }
    // ----------------------------------------------------

    this.loading = true;
    this.errorMessage = '';

    this.authService.register(this.registerData).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/patients']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Registration failed. User might already exist.';
      }
    });
  }
}
