import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from  '../services/auth/auth.service';

// 1. Schützt geschützte Seiten (z.B. /patients): Nur erlauben, wenn Token gültig
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isTokenValid()) {
    return true; // Zugriff erlaubt
  }

  // Nicht eingeloggt oder Token abgelaufen -> Token löschen und zum Login schicken
  authService.logout();
  router.navigate(['/login']);
  return false;
};

// 2. Schützt die Login/Register-Seiten: Wenn bereits eingeloggt, direkt zu /patients schicken
export const guestGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isTokenValid()) {
    router.navigate(['/patients']); // Bereits eingeloggt -> ab ins Dashboard
    return false;
  }

  return true; // Nicht eingeloggt -> Login/Register anzeigen
};
