import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService, ResetPasswordRequest } from  '../../services/auth/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './reset-password.component.html'
})
export class ResetPasswordComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  resetData: ResetPasswordRequest = {
    username: '',
    email: '',
    newPassword: ''
  };

  confirmPassword = '';
  showPassword = false;

  loading = false;
  successMessage = '';
  errorMessage = '';

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    if (this.resetData.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.resetPassword(this.resetData).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Password successfully reset! Redirecting to login...';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2500);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Password reset failed. Username or email incorrect.';
      }
    });
  }
}
