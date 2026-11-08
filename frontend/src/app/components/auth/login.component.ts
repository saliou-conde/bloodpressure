import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService, LoginRequest } from  '../../services/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef); // <-- Hier injizieren

  credentials: LoginRequest = {
    username: '',
    password: ''
  };

  showPassword = false;
  loading = false;
  errorMessage = '';
  togglePasswordVisibility(): void {
      this.showPassword = !this.showPassword;
  }

  onLogin(): void {
    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.credentials).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/patients']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Invalid username or password.';

        this.cdr.detectChanges(); // <-- Zwingt Angular dazu, die Fehlermeldung SOFORT anzuzeigen!
      }
    });
  }
}
